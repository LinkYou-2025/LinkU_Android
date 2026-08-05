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
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.home.paging.RecommendationPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
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

        categoryLoaded = false
        myLinkuCount = 0L

        isRecommendModeState.value = false
        needMoreForRecommendationState.value = false
        recommendationRequestState.value = null
    }

    // 사용자가 저장한 링크 개수
    private var myLinkuCount = 0L

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

        if (myLinkuCount < MIN_RECOMMENDATION_LINK_COUNT) {
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

    // 검색창 탑 시트 가시성 상태
    var searchTopSheetVisible by mutableStateOf(false)
        private set

    fun updateSearchTopSheetVisible(newState: Boolean) {
        Log.d("searchTopSheetVisible", newState.toString())
        searchTopSheetVisible = newState
    }

}
