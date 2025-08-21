package com.example.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.core.model.AiArticle
import com.example.core.model.LinkResultInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.model.search.RecentQuery
import com.example.core.repository.AIArticleRepository
import com.example.core.repository.CategoryRepository
import com.example.core.repository.LinkuRepository
import com.example.core.repository.RecentSearchRepository
import com.example.core.repository.UserRepository
import com.example.data.preference.AuthPreference
import com.example.data.util.toCategoryColorStyleMap
import com.example.design.FastSearchItem
import com.example.design.theme.color.CategoryColorStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val aiArticleRepository: AIArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val recentRepository: RecentSearchRepository,
) : ViewModel() {

    // 최초 진입 시 프로필 로드 (유지 가능)
    init {
        loadRecentLinks()
        loadUserBasics() // userId 없으면 조용히 리턴하게 바꿉니다
    }


    // 사용자 닉네임
    private val userNameState = mutableStateOf<String?>(null)
    val userName get() = userNameState.value

    // 직업 ID 보관
    private val jobIdState = mutableStateOf<Long?>(null)
    val jobId get() = jobIdState.value

    // 👇 파일 모듈과 동일한 타입을 그대로 노출(순서가 보장되는 LinkedHashMap이라고 가정)
    private val _categoryColorMap = MutableStateFlow<Map<String, CategoryColorStyle>>(emptyMap())
    val categoryColorMap: StateFlow<Map<String, CategoryColorStyle>> = _categoryColorMap.asStateFlow()

    // 최초 진입 시 프로필 로드
    init {
        loadRecentLinks()
        loadUserBasics()
        loadCategoryColors()
    }

    fun loadCategoryColors() {
        viewModelScope.launch {
            runCatching {
                categoryRepository.getCategoryColor().toCategoryColorStyleMap()
            }.onSuccess { map ->
                _categoryColorMap.value = map
            }.onFailure { e ->
                Log.e("HomeVM", "loadCategoryColors failed", e)
                _categoryColorMap.value = emptyMap()
            }
        }
    }

    // 🔧 1) public 으로, 그리고 userId 없으면 그냥 return
    fun loadUserBasics() {
        viewModelScope.launch {
            val userId = authPreference.userId
            if (userId == null || userId <= 0L) {
                // 로그인 전이므로 조용히 무시. (재진입에서 다시 호출할 것)
                return@launch
            }

            runCatching { userRepository.getUserInfo(userId) }
                .onSuccess { info ->
                    userNameState.value = info.nickname
                    jobIdState.value = info.jobId.toLong()
                }
                .onFailure { e ->
                    Log.e("HomeVM", "loadUserBasics failed", e)
                }
        }
    }

    // 🔧 2) 로그인 직후 한 번에 리프레시할 진입점
    fun refreshAfterLogin() {
        // userId/토큰이 저장된 '로그인 직후' 다시 호출
        loadUserBasics()
        loadRecentLinks()
    }


//    private fun loadUserBasics() {
//        viewModelScope.launch {
//            runCatching {
//                val userId = authPreference.userId ?: error("userId is null")
//                require(userId > 0L) { "invalid userId=$userId" }   // ✅ 음수/0 차단
//                userRepository.getUserInfo(userId)                  // ✅ 파라미터 전달
//            }.onSuccess { info ->
//                userNameState.value = info.nickname
//                jobIdState.value = info.jobId.toLong()
//            }.onFailure { e ->
//                Log.e("HomeVM", "loadUserBasics failed", e)
//            }
//        }
//    }

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

    // 새로운 링크 저장
    private val imageState = mutableStateOf<File?>(null)
    private val urlState = mutableStateOf("")
    private val memoState = mutableStateOf("")
    private val emotionIdState = mutableStateOf<Long?>(null)
    private val isSavingState = mutableStateOf(false)

    // URL 유효성 검사
    private val isCheckingUrlState = mutableStateOf(false)
    private val isDuplicateUrlState = mutableStateOf<Boolean?>(null)
    private var checkJob: Job? = null

    val image get() = imageState.value
    val url get() = urlState.value
    val memo get() = memoState.value
    val selectedEmotionId get() = emotionIdState.value
    val isSaving get() = isSavingState.value

    val isCheckingUrl get() = isCheckingUrlState.value
    val isDuplicateUrl get() = isDuplicateUrlState.value
    private val isInvalidUrlState = mutableStateOf(false)
    val isInvalidUrl get() = isInvalidUrlState.value

    // 추천에 필요한 링크 수 부족 안내 플래그
    private val needMoreForRecommendationState = mutableStateOf(false)
    val needMoreForRecommendation get() = needMoreForRecommendationState.value
    fun clearNeedMoreNotice() { needMoreForRecommendationState.value = false }

    // 추천 링크
    private val recommendedLinksState = mutableStateOf<List<LinkSimpleInfo>>(emptyList())
    val recommendedLinks get() = recommendedLinksState.value

    private val isRecommendingState = mutableStateOf(false)
    val isRecommending get() = isRecommendingState.value

    private val showRecommendationsState = mutableStateOf(false)
    val showRecommendations get() = showRecommendationsState.value

    fun setImage(file: File?) { imageState.value = file }
    fun setUrl(newUrl: String) {
        urlState.value = newUrl

        // invalid 판정
        isInvalidUrlState.value =
            newUrl.isNotBlank() && !android.webkit.URLUtil.isValidUrl(newUrl)

        // 디바운스 검사
        checkJob?.cancel()
        isDuplicateUrlState.value = null
        if (newUrl.isBlank()) {
            isCheckingUrlState.value = false
            return
        }
        checkJob = viewModelScope.launch {
            isCheckingUrlState.value = true
            delay(300)
            runCatching { linkuRepository.checkLink(newUrl) }
                .onSuccess { exists -> isDuplicateUrlState.value = exists }
                .onFailure { isDuplicateUrlState.value = null }
            isCheckingUrlState.value = false
        }
    }
    fun setMemo(newMemo: String) { memoState.value = newMemo }
    fun selectEmotion(id: Long?) { emotionIdState.value = id }



    // 저장 폼 초기화
    fun resetForm() {
        imageState.value = null
        urlState.value = ""
        memoState.value = ""
        emotionIdState.value = null
    }

    // 최근 조회 링크 상태
    private val recentLinksState = mutableStateOf<List<LinkSimpleInfo>>(emptyList())
    val recentLinks get() = recentLinksState.value

    // AI 요약
    private val aiArticleDetailState = mutableStateOf<AiArticle?>(null)
    val aiArticleDetail get() = aiArticleDetailState.value

    private val isLoadingAiArticleState = mutableStateOf(false)
    val isLoadingAiArticle get() = isLoadingAiArticleState.value


    // 링크 저장
    fun saveLink(
        onSucceed: (saved: LinkSimpleInfo) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        if (isSavingState.value) return // 중복 클릭 방지

        val currentUrl = urlState.value
        if (currentUrl.isBlank()) {
            onFailed(IllegalArgumentException("URL을 입력해 주세요."))
            return
        }
        if (isDuplicateUrlState.value == true) {
            onFailed(IllegalStateException("이미 저장된 링크입니다."))
            return
        }

        isSavingState.value = true
        viewModelScope.launch {
            try {
                val saved = linkuRepository.saveNewLink(
                    image = imageState.value,
                    url = currentUrl,
                    memo = memoState.value.ifBlank { null },
                    emotionId = emotionIdState.value
                )
                onSucceed(saved)
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                isSavingState.value = false
            }
        }
    }

    // 링크 유효성 검사


    // 링크 추천
    fun fetchRecommendations(
        situationId: Long,
        emotionId: Long,
        size: Int = 10,
        onDone: () -> Unit = {}
    ) {
        if (isRecommendingState.value) return
        viewModelScope.launch {
            isRecommendingState.value = true
            needMoreForRecommendationState.value = false

            runCatching {
                linkuRepository.recommendLinks(
                    situationId = situationId,
                    emotionId = emotionId,
                    page = 0,
                    size = size
                )
            }.onSuccess {
                recommendedLinksState.value = it
                showRecommendationsState.value = true
            }.onFailure { e ->
                if (e.isLinku4003()) {
                    // 3개 미만 케이스
                    needMoreForRecommendationState.value = true
                    recommendedLinksState.value = emptyList()
                    showRecommendationsState.value = true
                } else {
                    // 그 외 실패
                    needMoreForRecommendationState.value = false
                    recommendedLinksState.value = emptyList()
                    showRecommendationsState.value = true
                }
            }

            isRecommendingState.value = false
            onDone()
        }
    }

//    // ‘최근’으로 되돌리기
//    fun showRecent() {
//        showRecommendationsState.value = false
//    }

    // 최근 조회 링크 로딩
    fun loadRecentLinks() {
        viewModelScope.launch {
            runCatching { linkuRepository.getRecentLinks(limit = 10) }
                .onSuccess { recentLinksState.value = it }
                .onFailure {
                    Log.e("HomeVM", "loadRecentLinks failed", it)
                    recentLinksState.value = emptyList()
                }
        }
    }

    // 상세 불러오기
    private val linkDetailState = mutableStateOf<LinkResultInfo?>(null)
    val linkDetail get() = linkDetailState.value

    private val isLoadingLinkDetailState = mutableStateOf(false)
    val isLoadingLinkDetail get() = isLoadingLinkDetailState.value

    fun loadLinkDetail(linkuId: Long) {
        viewModelScope.launch {
            isLoadingLinkDetailState.value = true

            // 상세 요청 전에 요청 파라미터 로깅
            Log.d("SaveLinkFlow", "상세 요청 -> linkuId = $linkuId")

            runCatching { linkuRepository.getLinkDetail(linkuId) }
                .onSuccess { info ->
                    // 상세 응답(도메인 모델) 로깅
                    Log.d("SaveLinkFlow", "상세 응답 -> LinkResultInfo = $info")
                    linkDetailState.value = info

                    // 조건부 AI 요약 호출
                    val hasSummary = !info.summary.isNullOrBlank()
                    val hasKeyword = !info.keyword.isNullOrBlank()
                    if (!info.aiArticleExists && !hasSummary && !hasKeyword) {
                        // 서버에 요약이 전혀 없을 때만 생성 API 호출
                        loadAiArticle(linkuId)
                    } else {
                        // 이미 상세에 담겨온 경우: 별도 호출 없이 화면에서 바로 사용
                        aiArticleDetailState.value = null // 사용 안 함 (UI는 linkDetail의 요약/키워드 사용)
                    }
                }
                .onFailure { e ->
                    Log.e("SaveLinkFlow", "상세 응답 실패", e)
                    linkDetailState.value = null
                }

            isLoadingLinkDetailState.value = false
        }
    }

    // AI 요약
    fun loadAiArticle(linkuId: Long) {
        viewModelScope.launch {
            isLoadingAiArticleState.value = true
            runCatching { aiArticleRepository.getAiArticle(linkuId) }
                .onSuccess { aiArticleDetailState.value = it }
                .onFailure { e ->
                    Log.e("SaveLinkFlow", "AI 요약 불러오기 실패", e)
                    aiArticleDetailState.value = null
                }
            isLoadingAiArticleState.value = false
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
