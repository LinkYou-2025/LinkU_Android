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
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.RecommendationRequest
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.CheckLinkUseCase
import com.linku.data.preference.AuthPreference
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val categoryRepository: CategoryRepository,
    private val alarmRepository: AlarmRepository,
    private val checkLinkUseCase: CheckLinkUseCase,
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

    /** 백엔드 검사까지 통과하여 클립보드 배너에 표시할 수 있는 URL입니다. */
    private val _validatedClipboardUrl = MutableStateFlow<String?>(null)

    /**
     * 프론트 및 백엔드 유효성 검사를 모두 통과한 클립보드 URL입니다.
     *
     * `null`이면 검사할 후보가 없거나, 검사 중이거나, 검사에 실패한 상태를 의미합니다.
     */
    val validatedClipboardUrl: StateFlow<String?> = _validatedClipboardUrl.asStateFlow()

    /** 새 클립보드 후보가 들어왔을 때 이전 백엔드 검사를 취소하기 위한 작업입니다. */
    private var clipboardValidationJob: Job? = null

    /** 취소 시점과 겹쳐 완료된 이전 요청의 결과 반영을 차단하는 요청 식별자입니다. */
    private var clipboardValidationRequestId = 0L

    /** 현재 홈 화면이 전달한 가장 최신의 유효한 클립보드 후보입니다. */
    private var currentClipboardCandidateUrl: String? = null

    /**
     * 클립보드 URL을 프론트에서 먼저 검사하고, 통과한 경우에만 백엔드 검사를 실행합니다.
     *
     * 새 후보를 검사하기 시작하면 기존 배너 URL을 즉시 제거합니다. 이전 화면 세션에서 이미 노출했거나
     * 최근 저장에 성공한 URL이면 백엔드 호출 없이 종료합니다. 새 URL에 대해 백엔드가 신규 링크 또는
     * 이미 저장한 링크로 응답하면 중복 저장 가능 정책에 따라 모두 유효한 URL로 공개합니다.
     * 공개 전에 URL을 영속화하므로 화면 재진입과 앱 재실행에서도 같은 값은 반복 노출되지 않습니다.
     * 프론트 검사 실패와 백엔드·네트워크 오류는 배너를 노출하지 않고 종료합니다.
     *
     * @param candidateUrl 시스템 클립보드에서 읽은 URL 후보. 후보가 없으면 `null`
     */
    fun validateClipboardUrl(candidateUrl: String?) {
        clipboardValidationJob?.cancel()
        clipboardValidationJob = null
        _validatedClipboardUrl.value = null

        val requestId = ++clipboardValidationRequestId
        val normalizedUrl = candidateUrl?.trim().orEmpty()
        val frontendValidationResult = validateUrlInput(normalizedUrl)

        if (frontendValidationResult != UrlValidationResult.Valid) {
            currentClipboardCandidateUrl = null
            return
        }

        currentClipboardCandidateUrl = normalizedUrl

        clipboardValidationJob = viewModelScope.launch {
            try {
                val userId = authPreference.getUserId() ?: return@launch

                if (!isCurrentClipboardValidation(requestId, normalizedUrl)) {
                    return@launch
                }

                val lastPresentedUrl = authPreference.getLastPresentedClipboardUrl(userId)
                if (!isCurrentClipboardValidation(requestId, normalizedUrl)) {
                    return@launch
                }

                val lastSavedUrl = authPreference.getLastSavedLinkUrl(userId)
                if (!isCurrentClipboardValidation(requestId, normalizedUrl)) {
                    return@launch
                }

                if (normalizedUrl == lastPresentedUrl || normalizedUrl == lastSavedUrl) {
                    return@launch
                }

                when (checkLinkUseCase(normalizedUrl)) {
                    LinkCheckResult.Available,
                    LinkCheckResult.AlreadySaved -> {
                        if (!isCurrentClipboardValidation(requestId, normalizedUrl)) {
                            return@launch
                        }

                        val isPresentationSaved =
                            authPreference.saveLastPresentedClipboardUrl(normalizedUrl, userId)

                        if (isPresentationSaved && isCurrentClipboardValidation(requestId, normalizedUrl)) {
                            _validatedClipboardUrl.value = normalizedUrl
                        }
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("HomeVM", "clipboard link validation failed", error)
            }
        }
    }

    /**
     * 현재 클립보드 후보가 사용자 동작으로 처리되었음을 반영하고 배너를 즉시 숨깁니다.
     *
     * 배너 닫기·붙여넣기와 링크 저장 성공이 모두 이 진입점을 사용합니다. 전달된 URL이 최신
     * 클립보드 후보와 다르면 무관한 링크 저장으로 판단해 현재 배너 상태를 변경하지 않습니다.
     *
     * @param url 사용자가 닫거나 저장한 URL
     */
    fun markClipboardUrlHandled(url: String) {
        val normalizedUrl = url.trim()
        if (currentClipboardCandidateUrl != normalizedUrl) {
            return
        }

        clipboardValidationJob?.cancel()
        clipboardValidationJob = null
        clipboardValidationRequestId++
        _validatedClipboardUrl.value = null
    }

    /** 홈 화면 Composition이 종료될 때 이전 세션의 배너 상태가 남지 않도록 정리합니다. */
    fun endClipboardBannerSession() {
        clipboardValidationJob?.cancel()
        clipboardValidationJob = null
        clipboardValidationRequestId++
        _validatedClipboardUrl.value = null
    }

    /** 요청 식별자와 URL이 모두 최신 클립보드 후보를 가리키는지 확인합니다. */
    private fun isCurrentClipboardValidation(requestId: Long, url: String): Boolean =
        requestId == clipboardValidationRequestId && currentClipboardCandidateUrl == url

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
        _recentLinks.value = emptyList()
        _categoryColorMap.value = emptyMap()
        _isUnreadAlarmExists.value = false
        categoryLoaded = false
        myLinkuCount = null

        isRecommendModeState.value = false
        needMoreForRecommendationState.value = false
        recommendationRequestState.value = null

        clipboardValidationJob?.cancel()
        clipboardValidationJob = null
        clipboardValidationRequestId++
        currentClipboardCandidateUrl = null
        _validatedClipboardUrl.value = null
    }

    // 사용자가 저장한 링크 개수
    private var myLinkuCount: Long? = null

    // 추천에 필요한 링크 수 부족 안내 플래그
    private val needMoreForRecommendationState = mutableStateOf(false)
    val needMoreForRecommendation get() = needMoreForRecommendationState.value

    // 추천 모드 여부
    private val isRecommendModeState = mutableStateOf(false)
    val isRecommendMode get() = isRecommendModeState.value

    /*
     * null이면 추천 목록을 수집하지 않습니다.
     *
     * requestId가 있으므로 동일한 감정/상황으로 다시 요청해도
     * 새로운 PagingSource가 생성됩니다.
     */
    private val recommendationRequestState =
        MutableStateFlow<RecommendationRequest?>(null)

    val recommendedLinks: Flow<PagingData<LinkSimpleInfo>> =
        recommendationRequestState
            .flatMapLatest { request ->
                if (request == null) {
                    emptyFlow()
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

    // 최근 조회 링크 상태
    private val _recentLinks = MutableStateFlow<List<LinkSimpleInfo>>(emptyList())
    val recentLinks: StateFlow<List<LinkSimpleInfo>> = _recentLinks.asStateFlow()

    // 최근 조회 링크 로딩
    // 가장 먼저 호출되는 api? 토큰 달고 요청을 함.
    fun loadRecentLinks() {
        viewModelScope.launch {
            runCatching { linkuRepository.getRecentLinks(limit = 10) }
                .onSuccess { _recentLinks.value = it }
                .onFailure {
                    Log.e("HomeVM", "loadRecentLinks failed", it)
                    _recentLinks.value = emptyList()
                }
        }
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
