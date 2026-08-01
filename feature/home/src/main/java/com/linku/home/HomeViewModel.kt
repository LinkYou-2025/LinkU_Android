package com.linku.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.repository.UserRepository
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.data.preference.AuthPreference
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.top.search.FastSearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val categoryRepository: CategoryRepository,
    private val recentRepository: RecentSearchRepository,
) : ViewModel() {

    private companion object {
        const val MIN_RECOMMENDATION_LINK_COUNT = 3L
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

        // 추천 상태값도 초기화
        isRecommendModeState.value = false
        recommendedLinksState.value = emptyList()
        needMoreForRecommendationState.value = false

        recommendationNextCursor = null
        recommendationHasNext = false
        recommendationSituationId = null
        recommendationEmotionId = null
        recommendationPageSize = 5

        isRecommendingState.value = false
        isLoadingMoreRecommendationsState.value = false
    }

    private fun Throwable.isLinku4003(): Boolean {
        // 예외 메시지에 코드가 섞여 오는 경우
        if (message?.contains("LINKU4003") == true) return true

        // Retrofit HttpException인 경우 에러 바디에서 코드 텍스트만 탐지
        val http = this as? HttpException ?: return false
        return try {
            val body = http.response()?.errorBody()?.string()
            body?.contains("\"code\":\"LINKU4003\"") == true || body?.contains("LINKU4003") == true
        } catch (_: Exception) {
            false
        }
    }

    // 사용자가 저장한 링크 개수
    private var myLinkuCount = 0L

    // 추천에 필요한 링크 수 부족 안내 플래그
    private val needMoreForRecommendationState = mutableStateOf(false)
    val needMoreForRecommendation get() = needMoreForRecommendationState.value

    // 추천 링크
    private val recommendedLinksState = mutableStateOf<List<LinkSimpleInfo>>(emptyList())
    val recommendedLinks get() = recommendedLinksState.value

    // 추천 모드 여부
    private val isRecommendModeState = mutableStateOf(false)
    val isRecommendMode get() = isRecommendModeState.value

    fun exitRecommendMode() {
        isRecommendModeState.value = false
        needMoreForRecommendationState.value = false
    }

    // 추천 커서 페이징 상태
    private var recommendationNextCursor: String? = null
    private var recommendationHasNext: Boolean = false

    private var recommendationSituationId: Long? = null
    private var recommendationEmotionId: Long? = null
    private var recommendationPageSize: Int = 5

    private val isRecommendingState = mutableStateOf(false)
    val isRecommending get() = isRecommendingState.value

    // 추가 로딩 여부
    private val isLoadingMoreRecommendationsState = mutableStateOf(false)
    val isLoadingMoreRecommendations get() = isLoadingMoreRecommendationsState.value

    // 링크 추천
    fun fetchRecommendations(
        situationId: Long,
        emotionId: Long,
        size: Int = 5,
        onDone: () -> Unit = {},
    ) {
        if (isRecommendingState.value) return

        if (myLinkuCount < MIN_RECOMMENDATION_LINK_COUNT) {
            isRecommendModeState.value = true
            needMoreForRecommendationState.value = true
            recommendedLinksState.value = emptyList()

            recommendationNextCursor = null
            recommendationHasNext = false
            recommendationSituationId = null
            recommendationEmotionId = null
            recommendationPageSize = 5

            onDone()
            return
        }

        isRecommendModeState.value = true

        viewModelScope.launch {
            isRecommendingState.value = true
            needMoreForRecommendationState.value = false

            // 새로운 추천 조건으로 다시 요청하므로 기존 페이징 상태 초기화
            recommendationNextCursor = null
            recommendationHasNext = false

            recommendationSituationId = situationId
            recommendationEmotionId = emotionId
            recommendationPageSize = size

            runCatching {
                linkuRepository.recommendLinks(
                    situationId = situationId,
                    emotionId = emotionId,
                    cursor = null,
                    size = size,
                )
            }.onSuccess { page ->
                recommendedLinksState.value = page.items
                recommendationNextCursor = page.nextCursor
                recommendationHasNext = page.hasNext
            }.onFailure { error ->
                val needMoreLinks = error.isLinku4003()

                needMoreForRecommendationState.value = needMoreLinks
                recommendedLinksState.value = emptyList()

                recommendationNextCursor = null
                recommendationHasNext = false
                recommendationSituationId = null
                recommendationEmotionId = null
                recommendationPageSize = 5

                if (!needMoreLinks) {
                    LinkuLog.e(
                        "HomeVM",
                        "fetchRecommendations failed",
                        error,
                    )
                }
            }

            isRecommendingState.value = false
            onDone()
        }
    }

    fun loadMoreRecommendations() {
        if (isRecommendingState.value) return
        if (isLoadingMoreRecommendationsState.value) return
        if (!recommendationHasNext) return

        val cursor = recommendationNextCursor ?: return
        val situationId = recommendationSituationId ?: return
        val emotionId = recommendationEmotionId ?: return

        viewModelScope.launch {
            isLoadingMoreRecommendationsState.value = true

            runCatching {
                linkuRepository.recommendLinks(
                    situationId = situationId,
                    emotionId = emotionId,
                    cursor = cursor,
                    size = recommendationPageSize,
                )
            }.onSuccess { page ->
                recommendedLinksState.value =
                    recommendedLinksState.value + page.items

                recommendationNextCursor = page.nextCursor
                recommendationHasNext = page.hasNext
            }.onFailure { error ->
                LinkuLog.e(
                    "HomeVM",
                    "loadMoreRecommendations failed",
                    error,
                )
            }

            isLoadingMoreRecommendationsState.value = false
        }
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

    // ---------- search method ----------
    // 검색창 탑 시트 가시성 상태
    var searchTopSheetVisible by mutableStateOf(false)
        private set
    fun updateSearchTopSheetVisible(newState: Boolean) {
        Log.d("searchTopSheetVisible", newState.toString())
        searchTopSheetVisible = newState
    }

    // 빠른 링크 검색 목록
    private var _fastSearchItems = MutableStateFlow<List<FastSearchItem>>(emptyList())
    val fastSearchItems: StateFlow<List<FastSearchItem>> = _fastSearchItems.asStateFlow()

    // 빠른 링크 검색
    fun fastSearch(keyword: String){
        Log.d("HomeViewModel", "fastSearch")

        viewModelScope.launch{
            Log.d("HomeViewModel", "fastSearch launch")
            try{
                Log.d("HomeViewModel", "fastSearch try")

                _fastSearchItems.value = linkuRepository.fastSearch(keyword).map{
                    FastSearchItem(
                        id = it.linkuId,
                        title = it.title,
                        url = it.linkUrl
                    )
                }

                Log.d("HomeViewModel", "fastSearch try result: ${_fastSearchItems.value}")
            }catch (e: Exception){
                Log.d("HomeViewModel", "fastSearch catch: $e.message")

            }finally {
                Log.d("HomeViewModel", "fastSearch finally")
            }
        }
    }

    //최근 검색 목록
    val recentQueryList: StateFlow<List<RecentQuery>> =
        recentRepository.observe(limit = 20)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // 최근 검색 기록 추가
    fun addRecentQuery(query: String) {
        Log.d("HomeViewModel", "addRecentQuery")

        viewModelScope.launch {
            Log.d("HomeViewModel", "addRecentQuery launch")

            try{
                Log.d("HomeViewModel", "addRecentQuery try")

                recentRepository.add(query)
            }catch (e: Exception){
                Log.d("HomeViewModel", "addRecentQuery catch: $e.message")
            }finally {
                Log.d("HomeViewModel", "addRecentQuery finally")
            }
        }
        Log.d("HomeViewModel", "addRecentQuery return")
    }

    // 최근 검색 기록 삭제
    fun removeRecentQuery(query: String) {
        Log.d("HomeViewModel", "removeRecentQuery")

        viewModelScope.launch {
            Log.d("HomeViewModel", "removeRecentQuery launch")

            try{
                Log.d("HomeViewModel", "removeRecentQuery try")

                recentRepository.remove(query)

            }catch (e: Exception){
                Log.d("HomeViewModel", "removeRecentQuery catch: $e.message")
            }finally {
                Log.d("HomeViewModel", "removeRecentQuery finally")
            }
        }
        Log.d("HomeViewModel", "removeRecentQuery return")
    }


    // 최근 검색 기록 전체 삭제
    fun clearRecentQuery() {
        Log.d("HomeViewModel", "clearRecentQuery")

        viewModelScope.launch {
            Log.d("HomeViewModel", "clearRecentQuery launch")

            try{
                Log.d("HomeViewModel", "clearRecentQuery try")

                recentRepository.clear()

            }catch (e: Exception){
                Log.d("HomeViewModel", "clearRecentQuery catch: $e.message")
            }finally {
                Log.d("HomeViewModel", "clearRecentQuery finally")
            }
        }
        Log.d("HomeViewModel", "clearRecentQuery return")
    }
    // ---------- search method ----------
}
