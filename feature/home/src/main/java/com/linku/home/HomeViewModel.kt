package com.linku.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.RecommendationRequest
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.home.model.ClipboardLinkCandidate
import com.linku.home.model.RecentLinksLoadStatus
import com.linku.home.model.RecentLinksUiState
import com.linku.home.paging.RecommendationPagingSource
import com.linku.home.util.UrlValidationResult
import com.linku.home.util.validateUrlInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val categoryRepository: CategoryRepository,
    private val alarmRepository: AlarmRepository,
) : ViewModel() {

    private companion object {
        const val MIN_RECOMMENDATION_LINK_COUNT = 3L
        const val RECOMMENDATION_PAGE_SIZE = 5
        const val RECOMMENDATION_PREFETCH_DISTANCE = 2
    }

    fun refreshHomeData() {
        loadUserBasics()
        loadRecentLinks()
    }

    init {
        loadCategoryColors()
    }

    /** 앱 진입 시 홈의 링크 붙여넣기 말풍선에 표시할 클립보드 항목입니다. */
    private val _clipboardBannerCandidate = MutableStateFlow<ClipboardLinkCandidate?>(null)

    /**
     * 프론트 유효성 검사와 사용자별 중복 검사를 통과한 클립보드 링크와 복사 이벤트입니다.
     *
     * `null`이면 처리할 새 복사 이벤트가 없거나 검사 중인 상태를 의미합니다.
     */
    val clipboardBannerCandidate: StateFlow<ClipboardLinkCandidate?> =
        _clipboardBannerCandidate.asStateFlow()

    /** 새 앱 진입 후보가 들어왔을 때 이전 준비 작업을 취소하기 위한 작업입니다. */
    private var clipboardPreparationJob: Job? = null

    /** 취소 시점과 겹쳐 완료된 이전 준비 결과 반영을 차단하는 요청 식별자입니다. */
    private var clipboardPreparationRequestId = 0L

    /** 앱이 포그라운드로 진입하면서 읽은 가장 최신의 유효한 클립보드 후보입니다. */
    private var currentClipboardCandidate: ClipboardLinkCandidate? = null

    /**
     * 앱 진입 시 읽은 클립보드 URL을 홈 말풍선 표시 후보로 준비합니다.
     *
     * URL과 복사 시각이 모두 같은 항목을 이전 앱 진입에서 이미 처리했거나, 해당 항목을 복사한 뒤 같은
     * URL 저장에 성공했다면 말풍선을 표시하지 않습니다. 새 후보는 사용자별로 먼저 영속화한 뒤 공개하므로
     * 화면 전환이나 앱 재실행이 발생해도 같은 복사 이벤트가 반복 처리되지 않습니다. 네트워크 검사는
     * 저장 버튼을 누르는 기존 흐름에서 수행하여 앱 진입 직후 화면 이동을 지연시키지 않습니다.
     *
     * @param candidate 시스템 클립보드에서 읽은 URL과 복사 시각. 후보가 없으면 `null`
     */
    fun prepareClipboardBannerCandidate(candidate: ClipboardLinkCandidate?) {
        clipboardPreparationJob?.cancel()
        clipboardPreparationJob = null
        _clipboardBannerCandidate.value = null

        val requestId = ++clipboardPreparationRequestId
        val normalizedCandidate = candidate?.copy(url = candidate.url.trim())
        val frontendValidationResult = validateUrlInput(normalizedCandidate?.url.orEmpty())

        if (normalizedCandidate == null || frontendValidationResult != UrlValidationResult.Valid) {
            currentClipboardCandidate = null
            return
        }

        currentClipboardCandidate = normalizedCandidate

        clipboardPreparationJob = viewModelScope.launch {
            try {
                val userId = authPreference.getUserId() ?: return@launch

                if (!isCurrentClipboardPreparation(requestId, normalizedCandidate)) {
                    return@launch
                }

                val wasPresented = authPreference.wasClipboardLinkPresented(
                    url = normalizedCandidate.url,
                    copiedAtMillis = normalizedCandidate.copiedAtMillis,
                    userId = userId,
                )
                if (!isCurrentClipboardPreparation(requestId, normalizedCandidate)) {
                    return@launch
                }

                val wasSavedAfterCopy = authPreference.wasLinkSavedAfterClipboardCopy(
                    url = normalizedCandidate.url,
                    copiedAtMillis = normalizedCandidate.copiedAtMillis,
                    userId = userId,
                )
                if (!isCurrentClipboardPreparation(requestId, normalizedCandidate)) {
                    return@launch
                }

                if (wasPresented || wasSavedAfterCopy) {
                    return@launch
                }

                val isPresentationSaved = authPreference.savePresentedClipboardLink(
                    url = normalizedCandidate.url,
                    copiedAtMillis = normalizedCandidate.copiedAtMillis,
                    userId = userId,
                )

                if (
                    isPresentationSaved &&
                    isCurrentClipboardPreparation(requestId, normalizedCandidate)
                ) {
                    _clipboardBannerCandidate.value = normalizedCandidate
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("HomeVM", "clipboard banner preparation failed", error)
            }
        }
    }

    /**
     * 현재 클립보드 후보의 말풍선 표시가 사용자 동작으로 끝났음을 반영합니다.
     *
     * URL과 복사 시각 중 하나라도 최신 후보와 다르면 다른 복사 이벤트로 판단해 현재 상태를 변경하지
     * 않습니다. 일치하는 후보는 일회성 이동이 끝났으므로 메모리 상태에서도 제거합니다.
     *
     * @param candidate 사용자가 닫거나 저장 화면으로 전달한 정확한 클립보드 후보
     */
    fun dismissClipboardBannerCandidate(candidate: ClipboardLinkCandidate) {
        val normalizedCandidate = candidate.copy(url = candidate.url.trim())
        if (currentClipboardCandidate != normalizedCandidate) {
            return
        }

        clipboardPreparationJob?.cancel()
        clipboardPreparationJob = null
        clipboardPreparationRequestId++
        _clipboardBannerCandidate.value = null
        currentClipboardCandidate = null
    }

    /** 요청 식별자와 후보 전체가 모두 최신 앱 진입 클립보드 항목을 가리키는지 확인합니다. */
    private fun isCurrentClipboardPreparation(
        requestId: Long,
        candidate: ClipboardLinkCandidate,
    ): Boolean =
        requestId == clipboardPreparationRequestId && currentClipboardCandidate == candidate

    // 직업 ID 보관
    private val jobIdState = mutableStateOf<Long?>(null)
    val jobId get() = jobIdState.value

    // 👇 파일 모듈과 동일한 타입을 그대로 노출(순서가 보장되는 LinkedHashMap이라고 가정)
    private val _categoryColorMap = MutableStateFlow<Map<String, CategoryColorStyle>>(emptyMap())
    val categoryColorMap: StateFlow<Map<String, CategoryColorStyle>> = _categoryColorMap.asStateFlow()

    private var categoryLoaded = false

    fun loadCategoryColors(force: Boolean = false) {
        if (!force && categoryLoaded && _categoryColorMap.value.isNotEmpty()) return
        viewModelScope.launch {
            runCatching { categoryRepository.getCategoryColor().toCategoryColorStyleMap() }
                .onSuccess { map ->
                    _categoryColorMap.value = map
                    categoryLoaded = true
                }
                .onFailure {
                    Log.e("HomeVM", "loadCategoryColors failed", it)
                }
        }
    }

    // 🔧 1) public 으로, 그리고 userId 없으면 그냥 return
    // 여기는 토큰 사용이 없음.
    fun loadUserBasics() {
        viewModelScope.launch {
            val userId = authPreference.getUserId()
            if (userId == null || userId <= 0L) {
                // 로그인 전이므로 조용히 무시. (재진입에서 다시 호출할 것)
                return@launch
            }

            userRepository.getUserInfo(userId)
                .onSuccess { userInfo ->
//                    userNameState.value = userInfo.nickname
                    jobIdState.value = userInfo.jobId
                    myLinkuCount = userInfo.myLinku
                    needMoreForRecommendationState.value =
                        userInfo.myLinku < MIN_RECOMMENDATION_LINK_COUNT
                }
                .onFailure { e ->
                    Log.e("HomeVM", "loadUserBasics failed", e)
                }
        }
    }

    // 🔧 2) 로그인 직후 한 번에 리프레시할 진입점
    fun refreshAfterLogin() {
        // userId/토큰이 저장된 '로그인 직후' 다시 호출
        refreshHomeData()
    }

    // 로그아웃 시 모든 데이터 비워주는 기능
    fun clearData() {
        // 모든 상태값 초기화
//        userNameState.value = null
        jobIdState.value = null
        recentLinksLoadJob?.cancel()
        recentLinksLoadJob = null
        recentLinksRequestId++
        _recentLinksUiState.value = RecentLinksUiState()
        _categoryColorMap.value = emptyMap()
        _isUnreadAlarmExists.value = false
        categoryLoaded = false
        myLinkuCount = null

        isRecommendModeState.value = false
        needMoreForRecommendationState.value = false
        recommendationRequestState.value = null
        _deletedRecommendedLinkIds.value = emptySet()

        clipboardPreparationJob?.cancel()
        clipboardPreparationJob = null
        clipboardPreparationRequestId++
        currentClipboardCandidate = null
        _clipboardBannerCandidate.value = null
    }

    // 사용자가 저장한 링크 개수
    private var myLinkuCount: Long? = null

    // 추천에 필요한 링크 수 부족 안내 플래그
    private val needMoreForRecommendationState = mutableStateOf(false)
    val needMoreForRecommendation get() = needMoreForRecommendationState.value

    // 추천 모드 여부
    private val isRecommendModeState = mutableStateOf(false)
    val isRecommendMode get() = isRecommendModeState.value

    /**
     * 추천 요청을 시작하거나 해제하기 위한 상태입니다.
     *
     * [RecommendationRequest.requestId]가 있으므로 동일한 감정과 상황으로 다시 요청해도
     * 새로운 [RecommendationPagingSource]가 생성됩니다.
     */
    private val recommendationRequestState =
        MutableStateFlow<RecommendationRequest?>(null)

    /**
     * 삭제가 완료된 추천 링크를 서버 페이징 결과에서 다시 노출하지 않기 위한 세션 오버레이입니다.
     *
     * 상세 조회와 삭제 API에 사용할 수 있는 양수 사용자 링크 ID만 보관하며, 로그아웃 시
     * [clearData]에서 초기화해 다음 사용자 세션에 이전 삭제 상태가 전달되지 않도록 합니다.
     */
    private val _deletedRecommendedLinkIds = MutableStateFlow<Set<Long>>(emptySet())

    /**
     * 활성 추천 요청의 원본 페이징 데이터를 ViewModel 생명주기 동안 캐시합니다.
     *
     * 추천 모드를 종료하면 빈 [PagingData]를 방출해 수집 중인 목록을 즉시 초기화합니다.
     * 삭제 오버레이가 바뀔 때 같은 [PagingData]를 다시 변환하므로, 원본 스트림을 먼저 캐시해
     * 페이지 이벤트를 안전하게 재수집할 수 있도록 합니다.
     */
    private val pagedRecommendedLinks: Flow<PagingData<LinkSimpleInfo>> =
        recommendationRequestState
            .flatMapLatest { request ->
                if (request == null) {
                    flowOf(PagingData.empty())
                } else {
                    Pager(
                        config = PagingConfig(
                            pageSize = request.pageSize,
                            initialLoadSize = request.pageSize,
                            prefetchDistance = RECOMMENDATION_PREFETCH_DISTANCE,
                            enablePlaceholders = false,
                        ),
                        pagingSourceFactory = {
                            RecommendationPagingSource(
                                linkuRepository = linkuRepository,
                                situationId = request.situationId,
                                emotionId = request.emotionId,
                                pageSize = request.pageSize,
                            )
                        },
                    ).flow
                }
            }
            .cachedIn(viewModelScope)

    /**
     * 홈 화면에 노출할 추천 링크의 페이징 데이터입니다.
     *
     * 홈 또는 다른 화면에서 삭제가 완료된 사용자 링크 ID를 원본 페이지에서 즉시 제외합니다.
     * 이후 추천 조건을 다시 요청해 서버가 삭제 전 항목을 반환하더라도 현재 사용자 세션에서는
     * 다시 표시하지 않으며, 변환된 최종 스트림도 ViewModel 생명주기 동안 캐시합니다.
     */
    val recommendedLinks: Flow<PagingData<LinkSimpleInfo>> =
        combine(
            pagedRecommendedLinks,
            _deletedRecommendedLinkIds,
        ) { pagingData, deletedRecommendedLinkIds ->
            pagingData.filter { link ->
                link.userLinkuId !in deletedRecommendedLinkIds
            }
        }.cachedIn(viewModelScope)

    fun fetchRecommendations(
        situationId: Long,
        emotionId: Long,
        size: Int = RECOMMENDATION_PAGE_SIZE,
        onDone: () -> Unit = {},
    ) {
        isRecommendModeState.value = true

        val linkCount = myLinkuCount

        if (linkCount != null && linkCount < MIN_RECOMMENDATION_LINK_COUNT) {
            needMoreForRecommendationState.value = true
            recommendationRequestState.value = null

            onDone()
            return
        }

        needMoreForRecommendationState.value = false

        recommendationRequestState.value =
            RecommendationRequest(
                situationId = situationId,
                emotionId = emotionId,
                pageSize = size,
                requestId = System.nanoTime(),
            )

        onDone()
    }

    fun exitRecommendMode() {
        isRecommendModeState.value = false
        needMoreForRecommendationState.value = false
        recommendationRequestState.value = null
    }

    /** 최근 조회 링크 목록과 로딩 결과를 함께 보관하는 내부 상태입니다. */
    private val _recentLinksUiState = MutableStateFlow(RecentLinksUiState())

    /** 홈 화면에 노출할 최근 조회 링크 목록과 현재 요청 상태입니다. */
    val recentLinksUiState: StateFlow<RecentLinksUiState> =
        _recentLinksUiState.asStateFlow()

    /** 동시에 실행되는 최근 조회 링크 요청을 하나로 제한하기 위한 작업입니다. */
    private var recentLinksLoadJob: Job? = null

    /** 취소 이후 늦게 도착한 이전 요청 결과를 무시하기 위한 요청 식별자입니다. */
    private var recentLinksRequestId = 0L

    /**
     * 최근 조회 링크를 갱신합니다.
     *
     * 이미 요청이 실행 중이면 중복 요청을 시작하지 않습니다. 재조회 중에는 기존 링크를 유지하며,
     * 요청 실패 시에도 마지막 성공 목록을 보존해 화면이 빈 상태로 되돌아가지 않도록 합니다.
     */
    fun loadRecentLinks() {
        if (recentLinksLoadJob?.isActive == true) {
            return
        }

        val requestId = ++recentLinksRequestId

        // 기존 링크는 유지하고 요청 단계만 갱신해 재조회 중 카드가 사라지지 않게 합니다.
        _recentLinksUiState.value = _recentLinksUiState.value.copy(
            loadStatus = RecentLinksLoadStatus.Loading,
        )

        recentLinksLoadJob = viewModelScope.launch {
            try {
                val links = linkuRepository.getRecentLinks(limit = 10)

                if (requestId != recentLinksRequestId) {
                    return@launch
                }

                _recentLinksUiState.value = RecentLinksUiState(
                    links = links,
                    loadStatus = RecentLinksLoadStatus.Success,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("HomeVM", "loadRecentLinks failed", error)

                if (requestId == recentLinksRequestId) {
                    _recentLinksUiState.value = _recentLinksUiState.value.copy(
                        loadStatus = RecentLinksLoadStatus.Error,
                    )
                }
            }
        }
    }

    /**
     * 삭제 완료된 링크를 홈 추천 및 최근 목록에서 즉시 제거하고 관련 데이터를 재조회합니다.
     *
     * 양수 사용자 링크 ID를 추천 목록의 세션 오버레이에 먼저 기록하므로, 서버 Paging 캐시나
     * 후속 추천 요청에 삭제 전 항목이 남아 있어도 다시 노출하지 않습니다. 삭제 전에 시작한 최근
     * 링크 요청은 취소하고 요청 식별자를 무효화한 뒤 최신 서버 상태를 불러옵니다.
     * 상세 조회와 삭제 API에 사용할 수 없는 `0` 이하 값은 상태를 변경하지 않고 무시합니다.
     *
     * @param userLinkuId 삭제 완료된 사용자 링크 식별자
     */
    fun onLinkDeleted(userLinkuId: Long) {
        if (userLinkuId <= 0L) return

        _deletedRecommendedLinkIds.update { deletedRecommendedLinkIds ->
            deletedRecommendedLinkIds + userLinkuId
        }

        recentLinksLoadJob?.cancel()
        recentLinksLoadJob = null
        recentLinksRequestId++

        _recentLinksUiState.value = _recentLinksUiState.value.copy(
            links = _recentLinksUiState.value.links.filterNot { link ->
                link.userLinkuId == userLinkuId
            },
        )

        loadUserBasics()
        loadRecentLinks()
    }

    private val _isUnreadAlarmExists = MutableStateFlow(false)
    val isUnreadAlarmExists = _isUnreadAlarmExists.asStateFlow()

    fun refreshUnreadAlarm() {
        viewModelScope.launch {
            alarmRepository.getUnreadAlarmExists()
                .onSuccess { _isUnreadAlarmExists.value = it }
        }
    }


}
