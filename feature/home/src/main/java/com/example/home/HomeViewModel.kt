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
import com.example.core.repository.AlarmRepository
import com.example.core.repository.CategoryRepository
import com.example.core.repository.LinkuRepository
import com.example.core.repository.RecentSearchRepository
import com.example.core.repository.UserRepository
import com.example.data.preference.AuthPreference
import com.example.data.util.DomainIdMapper
import com.example.data.util.toCategoryColorStyleMap
import com.example.design.top.search.FastSearchItem
import com.example.design.theme.color.CategoryColorStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
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
    private val alarmRepository: AlarmRepository,
) : ViewModel() {

    // 자돌 로그인 하고 이 함수가 가장 먼저 실행함.
    // 최초 진입 시 프로필 로드 (유지 가능)
    init {
        loadRecentLinks()
        loadUserBasics() // userId 없으면 조용히 리턴하게 바꿉니다
        loadCategoryColors()
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

    //로그아웃 시 모든 데이터 비워주는 기능
    fun clearData() {
        // 모든 상태값 초기화
        userNameState.value = null
        jobIdState.value = null
        recentLinksState.value = emptyList()
        linkDetailState.value = null
        linkCache.clear() // 상세 정보 캐시도 삭제
        _categoryColorMap.value = emptyMap()
        categoryLoaded = false
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

    // 진행률
    private val _aiProgress = MutableStateFlow(0f)
    val aiProgress: StateFlow<Float> = _aiProgress.asStateFlow()

    private var aiJob: Job? = null
    private var aiProgressJob: Job? = null

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

                // 낙관적 업데이트: 메모리의 최근 목록 즉시 갱신
                recentLinksState.value = buildList {
                    add(saved)
                    addAll(
                        recentLinksState.value
                            .filter { it.linkuId != saved.linkuId } // 중복 제거
                            .take(9) // 최대 10개 유지
                    )
                }

                onSucceed(saved)

                // 서버에서 실제 최근 목록 재조회(정렬/정책 싱크)
                loadRecentLinks()
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
    // 가장 먼저 호출되는 api? 토큰 달고 요청을 함.
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

//    fun loadLinkDetail(linkuId: Long) {
//        viewModelScope.launch {
//            isLoadingLinkDetailState.value = true
//
//            // 상세 요청 전에 요청 파라미터 로깅
//            Log.d("SaveLinkFlow", "상세 요청 -> linkuId = $linkuId")
//
//            runCatching { linkuRepository.getLinkDetail(linkuId) }
//                .onSuccess { info ->
//                    // 상세 응답(도메인 모델) 로깅
//                    Log.d("SaveLinkFlow", "상세 응답 -> LinkResultInfo = $info")
//                    linkDetailState.value = info
//
//                    // 자동 생성은 하지 않고, 항상 초기화만 수행
//                    aiArticleDetailState.value = null
//                }
//                .onFailure { e ->
//                    Log.e("SaveLinkFlow", "상세 응답 실패", e)
//                    linkDetailState.value = null
//                }
//
//            isLoadingLinkDetailState.value = false
//        }
//    }
    private data class Cached<T>(val value: T, val ts: Long = System.currentTimeMillis())
    private val linkCache = mutableMapOf<Long, Cached<LinkResultInfo>>()
    private val DETAIL_TTL = 60_000L // 60초

    fun loadLinkDetail(linkuId: Long, forceRefresh: Boolean = false) {
        val now = System.currentTimeMillis()
        val cached = linkCache[linkuId]
        if (!forceRefresh && cached != null && now - cached.ts < DETAIL_TTL) {
            // ✅ 캐시 즉시 노출(스피너 최소화)
            linkDetailState.value = cached.value
            isLoadingLinkDetailState.value = false
            // 백그라운드 갱신 (선택)
            viewModelScope.launch {
                runCatching { linkuRepository.getLinkDetail(linkuId) }
                    .onSuccess {
                        linkCache[linkuId] = Cached(it)
                        linkDetailState.value = it
                        aiArticleDetailState.value = null
                    }
                    .onFailure { Log.e("SaveLinkFlow", "refresh failed", it) }
            }
            return
        }

        viewModelScope.launch {
            isLoadingLinkDetailState.value = cached == null // 캐시 없을 때만 스피너
            runCatching { linkuRepository.getLinkDetail(linkuId) }
                .onSuccess {
                    linkCache[linkuId] = Cached(it)
                    linkDetailState.value = it
                    aiArticleDetailState.value = null
                }
                .onFailure {
                    Log.e("SaveLinkFlow", "상세 실패", it)
                    if (cached == null) linkDetailState.value = null
                }
            isLoadingLinkDetailState.value = false
        }
    }

    // AI 요약
    fun loadAiArticle(linkuId: Long) {
//        viewModelScope.launch {
//            isLoadingAiArticleState.value = true
//            runCatching { aiArticleRepository.getAiArticle(linkuId) }
//                .onSuccess { aiArticleDetailState.value = it }
//                .onFailure { e ->
//                    Log.e("SaveLinkFlow", "AI 요약 불러오기 실패", e)
//                    aiArticleDetailState.value = null
//                }
//            isLoadingAiArticleState.value = false
//        }

        Log.d("HomeVM", "loadAiArticle 진입: linkuId=$linkuId, 현재 isLoading=${isLoadingAiArticleState.value}")
        if (isLoadingAiArticleState.value) {
            Log.d("HomeVM", "이미 로딩중 → 조기 리턴")
            return
        }

        isLoadingAiArticleState.value = true
        Log.d("HomeVM", "isLoadingAiArticle = true 로 세팅 완료")
        _aiProgress.value = 0.1f

        // 진행률을 0.85f까지 서서히 끌어올리는 타이머
        aiProgressJob?.cancel()
        aiProgressJob = viewModelScope.launch {
            Log.d("HomeVM", "aiProgressJob 시작")
            val cap = 0.85f
            while (isActive && _aiProgress.value < cap) {
                delay(100)
                _aiProgress.value = (_aiProgress.value + 0.02f).coerceAtMost(cap)
            }
            Log.d("HomeVM", "aiProgressJob 종료")
        }

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            Log.d("HomeVM", "AI API 호출 시작")
            runCatching { aiArticleRepository.getAiArticle(linkuId) }
                .onSuccess {
                    Log.d("HomeVM", "AI API 성공: $it")
                    aiArticleDetailState.value = it
                }
                .onFailure { e ->
                    Log.e("HomeVM", "AI API 실패", e)
                    aiArticleDetailState.value = null
                }

            // 완료 처리
            aiProgressJob?.cancel()
            _aiProgress.value = 1f
            isLoadingAiArticleState.value = false
            Log.d("HomeVM", "isLoadingAiArticle = false 로 세팅 완료")

            // (선택) 잠깐 100% 보여준 뒤 초기화
            launch {
                delay(300)
                _aiProgress.value = 0f
                Log.d("HomeVM", "aiProgress 0으로 초기화")
            }
        }
    }

    fun cancelAiArticleJob() {
        aiJob?.cancel()
        aiProgressJob?.cancel()
        isLoadingAiArticleState.value = false
        _aiProgress.value = 0f
    }

    // 링크 수정
    private val isUpdatingLinkState = mutableStateOf(false)
    val isUpdatingLink get() = isUpdatingLinkState.value

    fun updateLink(
        title: String,
        memo: String?,
        categoryId: Long?,
        emotionId: Long?,
        onSucceed: (LinkResultInfo) -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val current = linkDetailState.value ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }
        if (isUpdatingLinkState.value) return

        // 서버에서 내려준 값으로 고정
        val fixedLinkuId = current.linkuId
        val fixedLinku   = current.linku

        // domainId 매핑
        val computedDomainId = DomainIdMapper.resolve(
            url = fixedLinku,
            domain = current.domain
        )

        viewModelScope.launch {
            isUpdatingLinkState.value = true
            runCatching {
                linkuRepository.updateLink(
                    linkuId   = fixedLinkuId,
                    categoryId= categoryId ?: current.categoryId ?: 0L,
                    linku     = fixedLinku, // 고정
                    memo      = memo,       // null/"" 그대로 전달
                    emotionId = emotionId ?: current.emotionId ?: 0L,
                    domainId  = computedDomainId,
                    title     = title.ifBlank { current.title }
                )
            }.onSuccess { updated ->
                linkDetailState.value = updated
                loadRecentLinks()  // 최근 조회 목록 갱신
                onSucceed(updated)
            }.onFailure { e ->
                onFailed(e)
            }
            isUpdatingLinkState.value = false
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

    // 알림
    fun registerFcmToken(token: String) {
        viewModelScope.launch {
            alarmRepository.registerFcmToken(token)
                .onSuccess { Log.d("HomeViewModel", "FCM 토큰 등록 성공") }
                .onFailure { e -> Log.e("HomeViewModel", "FCM 토큰 등록 실패", e) }
        }
    }
}
