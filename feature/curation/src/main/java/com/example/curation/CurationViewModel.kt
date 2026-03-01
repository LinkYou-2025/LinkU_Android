package com.example.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.core.model.CurationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.core.repository.CurationRepository
import com.example.core.repository.UserRepository
import com.example.data.preference.AuthPreference
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.core.model.search.RecentQuery
import com.example.core.repository.LinkuRepository
import com.example.core.repository.RecentSearchRepository
import com.example.core.session.SessionStore
import com.example.design.top.search.FastSearchItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


@HiltViewModel
class CurationViewModel @Inject constructor(
    private val repository: CurationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val sessionStore: SessionStore,
    private val recentRepository: RecentSearchRepository,
    private val linkuRepository: LinkuRepository,
) : ViewModel() {

    private var hasPrefetched = false //한 번만 실행.

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val session = sessionStore.session //닉네임, 직업 정보

    // 닉네임, 직업 session에서 직접
    val nickname = sessionStore.session
        .map { it.nickname ?: "세나" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "세나")

    val jobName = sessionStore.session
        .map { it.jobName ?: "직장인" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "직장인")

    private val _recentCuration = MutableStateFlow<CurationItem?>(null)
    val recentCuration: StateFlow<CurationItem?> = _recentCuration

    // 추가: 네비게이션용 ID들
    private val _userId = MutableStateFlow(-1L)
    val userId: StateFlow<Long> = _userId

    private val _currentCurationId = MutableStateFlow(-1L)
    val currentCurationId: StateFlow<Long> = _currentCurationId

    //큐레이션 추천
    private val _likedCurations = MutableStateFlow<List<CurationItem>>(emptyList())
    val likedCurations: StateFlow<List<CurationItem>> = _likedCurations

    private val _likedLoading = MutableStateFlow(false)
    val likedLoading: StateFlow<Boolean> = _likedLoading

    private val _likedError = MutableStateFlow<String?>(null)
    val likedError: StateFlow<String?> = _likedError

    // 하이라이트(현재 큐레이션) 좋아요 상태
    val highlightLiked: StateFlow<Boolean?> =
        combine(_recentCuration, _likedCurations) { recent, likedList ->
            recent?.let { r -> likedList.any { it.id == r.id } } ?: null
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // 로딩/중복탭 방지 플래그(선택)
    private val _likeBusy = MutableStateFlow(false)
    val likeBusy: StateFlow<Boolean> = _likeBusy

    // --- 공통 uid 가드
    private fun requireUserId(): Long {
        val uid = authPreference.userId ?: -1L
        _userId.value = uid
        return uid
    }



    // retrofit2에 의존하지 않고, 예외에 code() 메서드가 있으면 꺼내오는 유틸
    private fun httpStatusCodeOrMinus1(throwable: Throwable): Int {
        return runCatching {
            val m = throwable::class.java.methods
                .firstOrNull { it.name == "code" && it.parameterCount == 0 }
            (m?.invoke(throwable) as? Int) ?: -1
        }.getOrDefault(-1)
    }

    // hasPrefetched == true  ➜ "빈 상태를 확정했고, 재호출 막기" 의미로만 사용
    fun loadMonthlyCuration(forceReload: Boolean = false) {
        viewModelScope.launch {
            // 1) 동시 중복 방지(여전히 유지)
            if (_isGenerating.value) return@launch
            // 2) 빈 상태로 잠가둔 경우만 막는다 (강제 새로고침은 예외)
            if (hasPrefetched && !forceReload) return@launch

            _isGenerating.value = true
            _errorMessage.value = null

            val uid = requireUserId()
            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")

            // 로그인 안됨 → 빈 상태 확정하고 재호출 막기
            if (uid <= 0L) {
                setEmptyCurationState(markPrefetched = true) // ⬅ empty lock ON
                _isGenerating.value = false
                return@launch
            }
            try {
                val item = repository.getMyRecentCuration(uid)

                // (방어) "result"가 null이거나 id가 잘못된 값 → 빈 상태 확정(+재호출 막기)
                if (item == null || item.id <= 0L) {
                    setEmptyCurationState(markPrefetched = true) // ⬅ empty lock ON
                    _isGenerating.value = false   // 무한로딩 강제종료
                    return@launch
                }

                // ✅ 정상 데이터: 상태 갱신
                _recentCuration.value = item
                _currentCurationId.value = item.id

//            try {
//                val item = repository.getMyRecentCuration(uid)
//
//                // (방어) 잘못된 값 → 빈 상태 확정(+재호출 막기)
//                if (item.id <= 0L) {
//                    setEmptyCurationState(markPrefetched = true) // ⬅ empty lock ON
//                    return@launch
//                }
//
//                // ✅ 정상 데이터: 상태 갱신
//                _recentCuration.value = item
//                _currentCurationId.value = item.id

//                runCatching { repository.isCurationLiked(item.id, uid) }
//                    .onSuccess { _highlightLiked.value = it }
//                    .onFailure { _highlightLiked.value = false }

                loadHomeRecommendedLinksTop2(uid, item.id)
                loadLikedCurations()

                Log.d("CurationVM", "큐레이션 불러오기 성공: $item")

                // ✅ 데이터가 있으므로 이후에도 재호출 허용
                hasPrefetched = false // ⬅ empty lock OFF
            } catch (e: Exception) {
                val code = httpStatusCodeOrMinus1(e)

                // “진짜로 최신 없음/권한 없음”은 빈 상태 확정하고 재호출 막기
                if (e is NoSuchElementException || code == 403 || code == 404) {
                    val tag = if (e is NoSuchElementException) "NoSuchElement" else "HTTP $code"
                    Log.i("CurationVM", "최신 큐레이션 없음/권한없음($tag) → 빈 상태 확정")
                    setEmptyCurationState(markPrefetched = true)  // ⬅ empty lock ON
                    return@launch
                }

                // 그 외 에러는 사용자 노출 + 재시도 허용 (데이터가 있을 수 있으니 막지 않음)
                val msg = e.message.orEmpty()
                _errorMessage.value =
                    if (msg.contains("Token", true) && msg.contains("expired", true))
                        "세션이 만료됐어요. 다시 로그인해 주세요."
                    else
                        "큐레이션 조회에 실패했어요. 잠시 후 다시 시도해 주세요."

                Log.e("CurationVM", "큐레이션 불러오기 실패(code=$code)", e)
                hasPrefetched = false // ⬅ empty lock OFF (재시도 허용)
            } finally {
                _isGenerating.value = false
            }
        }
    }

//    fun loadMonthlyCuration() {
//        viewModelScope.launch {
//            if (hasPrefetched) return@launch
//
//            _isGenerating.value = true
//            _errorMessage.value = null
//
//            val uid = requireUserId()
//            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")
//
//            // ⬇️ 로그인 안돼 있으면 ‘빈 상태’로 조용히 표시하고 종료
//            if (uid <= 0L) {
//                setEmptyCurationState(markPrefetched = false) // ← false 로 변경 (재시도 허용)
//                return@launch
//            }
//
//            try {
//                val item = repository.getMyRecentCuration(uid)
//                if (item == null || item.id <= 0L) {
//                    setEmptyCurationState(markPrefetched = true)
//                    return@launch
//                }
//
//                _recentCuration.value = item
//                _currentCurationId.value = item.id
//
//                // 현재 큐레이션 좋아요 상태
//                runCatching { repository.isCurationLiked(item.id, uid) }
//                    .onSuccess { _highlightLiked.value = it }
//                    .onFailure { _highlightLiked.value = false }
//
//                // 추천 2개 + 좋아요 리스트
//                loadHomeRecommendedLinksTop2(uid, item.id)
//                loadLikedCurations()
//
//                Log.d("CurationVM", "큐레이션 불러오기 성공: $item")
//                hasPrefetched = true
//            } catch (e: Exception) {
//                // ⬇️ retrofit2 없이도 403/404만 골라냄
//                val code = httpStatusCodeOrMinus1(e)
//                if (code == 403 || code == 404) {
//                    Log.w("CurationVM", "큐레이션 없음/권한 없음(HTTP $code) → 빈 상태 표시")
//                    setEmptyCurationState(markPrefetched = false) // ← false 로 변경 (재시도 허용)
//                    return@launch
//                } else {
//                    val msg = e.message.orEmpty()
//                    _errorMessage.value = when {
//                        msg.contains("Token", true) && msg.contains("expired", true) ->
//                            "세션이 만료됐어요. 다시 로그인해 주세요."
//                        else -> "큐레이션 조회에 실패했어요. 잠시 후 다시 시도해 주세요."
//                    }
//                    Log.e("CurationVM", "큐레이션 불러오기 실패(code=$code)", e)
//                    hasPrefetched = false
//                }
//            } finally {
//                _isGenerating.value = false
//            }
//        }
//    }
//    fun loadMonthlyCuration() {
//        viewModelScope.launch {
//            // 한 번만 로드, 실패 시엔 다시 시도 가능하도록 플래그 관리
//            if (hasPrefetched) return@launch
//
//            _isGenerating.value = true
//            _errorMessage.value = null
//
//            val uid = requireUserId()
//            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")
//
//            if (uid <= 0L) {
//                _errorMessage.value = "로그인이 필요합니다."
//                _isGenerating.value = false
//                return@launch
//            }
//
//            try {
//                val item = repository.getMyRecentCuration(uid)
//                _recentCuration.value = item
//                _currentCurationId.value = item.id
//
//                // 현재 큐레이션 좋아요 상태
//                runCatching { repository.isCurationLiked(item.id, uid) }
//                    .onSuccess { _highlightLiked.value = it }
//                    .onFailure { _highlightLiked.value = false }
//
//                // Top2/좋아요 리스트
//                loadHomeRecommendedLinksTop2(uid, item.id)
//                loadLikedCurations()
//
//                Log.d("CurationVM", "큐레이션 불러오기 성공: $item")
//                hasPrefetched = true // 성공했을 때만 true
//            } catch (e: Exception) {
//                // 토큰 만료/401 문구는 사용자에게 명확히
//                val msg = e.message.orEmpty()
//                _errorMessage.value = when {
//                    msg.contains("Token", true) && msg.contains("expired", true) ->
//                        "세션이 만료됐어요. 다시 로그인해 주세요."
//                    else -> "큐레이션 조회에 실패했어요. 잠시 후 다시 시도해 주세요."
//                }
//                Log.e("CurationVM", "큐레이션 불러오기 실패", e)
//                hasPrefetched = false // 실패하면 재시도 가능
//            } finally {
//                _isGenerating.value = false
//            }
//        }
//    }
//    fun loadMonthlyCuration() {
//        viewModelScope.launch {
//            _isGenerating.value = true
//            _errorMessage.value = null
//
//            val uid = authPreference.userId ?: -1L
//            _userId.value = uid
//            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")
//
//            if (uid == -1L) {
//                _errorMessage.value = "로그인이 필요합니다."
//                _isGenerating.value = false
//                Log.w("CurationVM", "userId가 null 또는 -1L")
//                return@launch
//            }
//
//            try {
//                val response = repository.getMyRecentCuration(uid)
//                _recentCuration.value = response
//                // 현재 큐레이션 ID도 갱신 (CurationItem의 실제 필드명에 맞춰 수정)
//                _currentCurationId.value = response.id
//
//                // 현재 큐레이션 좋아요 상태도 로드
//                runCatching { repository.isCurationLiked(response.id, uid) }
//                    .onSuccess { _highlightLiked.value = it }
//                    .onFailure { _highlightLiked.value = false } // 실패 시 미선호로 기본
//
//                // 첫 진입에 바로 추천 Top2 로드
//                loadHomeRecommendedLinksTop2(uid, response.id)
//                loadLikedCurations()
//
//                Log.d("CurationVM", "큐레이션 불러오기 성공: $response")
//            } catch (e: Exception) {
//                _errorMessage.value = e.message ?: "큐레이션 조회 실패"
//                Log.e("CurationVM", "큐레이션 불러오기 실패", e)
//            } finally {
//                _isGenerating.value = false
//            }
//        }
//    }
//    init {
//        loadNickname()
//        loadMonthlyCuration()
//    }

    fun toggleLikeFor(curationId: Long) {
        setCurrentCurationId(curationId)
        toggleHighlightLike()
    }
//  하이라이트 하트 토글
fun toggleHighlightLike() {
    val uid = requireUserId()
    val cur = _recentCuration.value ?: return
    if (uid <= 0L || likeBusy.value) return

    val isLikedNow = highlightLiked.value == true

    viewModelScope.launch {
        _likeBusy.emit(true)
        val before = _likedCurations.value

        try {
            if (isLikedNow) {
                // 낙관적 제거
                _likedCurations.update { list -> list.filterNot { it.id == cur.id } }
                // 서버
                repository.unlikeCuration(cur.id, uid)
            } else {
                // 낙관적 추가 (동일 타입: CurationItem)
                _likedCurations.update { list -> list + cur }
                // 서버
                repository.likeCuration(cur.id, uid)
            }
        } catch (e: Throwable) {
            // 실패 시 롤백 후 안전 재동기화
            _likedCurations.value = before
            loadLikedCurations()
            _errorMessage.emit(e.message ?: "좋아요 처리 실패")
        } finally {
            _likeBusy.emit(false)
        }
    }
}

//    fun toggleHighlightLike() {
//        val cid = _currentCurationId.value
//        val uid = authPreference.userId ?: -1L
//        val current = _highlightLiked.value ?: false
//        if (cid <= 0 || uid <= 0 || _likeBusy.value) return
//
//        viewModelScope.launch {
//            _likeBusy.value = true
//            // 낙관적 업데이트
//            _highlightLiked.value = !current
//
//            val result = runCatching {
//                if (current) repository.unlikeCuration(cid, uid)
//                else repository.likeCuration(cid, uid)
//            }
//
//            result.onSuccess {
//                // 성공 시 목록 리프레시(선택: 비용 줄이려면 생략 가능)
//                loadLikedCurations()
//            }.onFailure { e ->
//                // 실패하면 롤백
//                _highlightLiked.value = current
//                _likedError.value = e.message ?: "좋아요 처리에 실패했어요"
//            }
//
//            _likeBusy.value = false
//        }
//    }

    fun refreshLikedCurations(userId: Long = requireUserId()) {
        viewModelScope.launch {
            try {
                _likedLoading.value = true
                val list = repository.getLikedCurations(userId) // <- 실제 API 호출명에 맞게
                _likedCurations.value = list                     // list: List<LikedCuration>
            } catch (e: Exception) {
                _likedError.value = "좋아요 목록을 불러오지 못했어요"
            } finally {
                _likedLoading.value = false
            }
        }
    }

    //큐레이션 추천(2개)
    private val _homeLinks = MutableStateFlow(CurationLinksUiState())
    val homeLinks: StateFlow<CurationLinksUiState> = _homeLinks

    fun loadHomeRecommendedLinksTop2(userId: Long, curationId: Long) {
        viewModelScope.launch {
            _homeLinks.value = _homeLinks.value.copy(loading = true, error = null)
            runCatching { repository.getHomeRecommendedLinksTop2(userId, curationId) }
                .onSuccess { list ->
                    _homeLinks.value = CurationLinksUiState(
                        loading = false,
                        items = list.take(2), // 방어
                        error = null
                    )
                }
                .onFailure { e ->
                    _homeLinks.value = CurationLinksUiState(
                        loading = false,
                        items = emptyList(),
                        error = e.message
                    )
                }
        }
    }

//    fun loadHomeRecommendedLinksTop2(userId: Long, curationId: Long) {
//        viewModelScope.launch {
//            _homeLinks.value = _homeLinks.value.copy(loading = true, error = null)
//            runCatching { repository.getHomeRecommendedLinksTop2(userId, curationId) }
//                .onSuccess { list ->
//                    _homeLinks.value = CurationLinksUiState(
//                        loading = false,
//                        items = list,   // 최대 2개
//                        error = null
//                    )
//                }
//                .onFailure { e ->
//                    _homeLinks.value = CurationLinksUiState(
//                        loading = false,
//                        items = emptyList(),
//                        error = e.message
//                    )
//                }
//        }
//    }

    fun loadLikedCurations() {
        viewModelScope.launch {
            val uid = authPreference.userId ?: -1L
            if (uid <= 0L) { _likedCurations.value = emptyList(); return@launch }
            _likedLoading.value = true
            _likedError.value = null
            runCatching { repository.getLikedCurations(uid) }
                .onSuccess { _likedCurations.value = it }
                .onFailure { _likedError.value = it.message }
            _likedLoading.value = false
        }
    }

    init {

        loadMonthlyCuration()

    }


    fun unlikeFromLikedList(curationId: Long) {
        if (likeBusy.value) return
        viewModelScope.launch {
            _likeBusy.emit(true)
            val before = _likedCurations.value
            // 낙관적 제거
            _likedCurations.update { list -> list.filterNot { it.id == curationId } }
            try {
                val uid = requireUserId()
                if (uid > 0L) repository.unlikeCuration(curationId, uid)
            } catch (e: Throwable) {
                _likedCurations.value = before
                _errorMessage.emit(e.message ?: "좋아요 취소 실패")
            } finally {
                _likeBusy.emit(false)
            }
        }
    }

    // (옵션) 상세 화면 등에서 좋아요 등록이 필요할 때 사용
    fun likeCuration(curationId: Long) {
        viewModelScope.launch {
            val uid = authPreference.userId ?: -1L
            if (uid <= 0) return@launch
            runCatching { repository.likeCuration(curationId, uid) }
                .onFailure { e -> _likedError.value = e.message ?: "좋아요에 실패했어요" }
        }
    }
    //새로고침 함수 추가.
    fun refreshHighlightLike(curationId: Long) {
//        viewModelScope.launch {
//            val uid = authPreference.userId ?: -1L
//            if (uid <= 0 || curationId <= 0) return@launch
//            runCatching { repository.isCurationLiked(curationId, uid) }
//                .onSuccess { _highlightLiked.value = it }
//                .onFailure { _highlightLiked.value = false }
//        }
        loadLikedCurations()
    }
    fun setCurrentCurationId(id: Long) { _currentCurationId.value = id }

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
        Log.d("CurationViewModel", "fastSearch")

        viewModelScope.launch{
            Log.d("CurationViewModel", "fastSearch launch")

            _errorMessage.value = null
            try{
                Log.d("CurationViewModel", "fastSearch try")

                _fastSearchItems.value = linkuRepository.fastSearch(keyword).map{
                    FastSearchItem(
                        id = it.linkuId,
                        title = it.title,
                        url = it.linkUrl
                    )
                }

                Log.d("CurationViewModel", "fastSearch try result: ${_fastSearchItems.value}")
            }catch (e: Exception){
                Log.d("CurationViewModel", "fastSearch catch: $e.message")

                _errorMessage.value = e.message
            }finally {
                Log.d("CurationViewModel", "fastSearch finally")
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
        Log.d("CurationViewModel", "addRecentQuery")

        viewModelScope.launch {
            Log.d("CurationViewModel", "addRecentQuery launch")

            try{
                Log.d("CurationViewModel", "addRecentQuery try")

                recentRepository.add(query)
            }catch (e: Exception){
                Log.d("CurationViewModel", "addRecentQuery catch: $e.message")
            }finally {
                Log.d("CurationViewModel", "addRecentQuery finally")
            }
        }
        Log.d("CurationViewModel", "addRecentQuery return")
    }

    // 최근 검색 기록 삭제
    fun removeRecentQuery(query: String) {
        Log.d("CurationViewModel", "removeRecentQuery")

        viewModelScope.launch {
            Log.d("CurationViewModel", "removeRecentQuery launch")

            try{
                Log.d("CurationViewModel", "removeRecentQuery try")

                recentRepository.remove(query)

            }catch (e: Exception){
                Log.d("CurationViewModel", "removeRecentQuery catch: $e.message")
            }finally {
                Log.d("CurationViewModel", "removeRecentQuery finally")
            }
        }
        Log.d("CurationViewModel", "removeRecentQuery return")
    }

    //403 빈 상태 오류!(미로그인)
    private fun setEmptyCurationState(markPrefetched: Boolean = true) {
        _recentCuration.value = null
        _currentCurationId.value = -1L
//        _highlightLiked.value = null
        _homeLinks.value = CurationLinksUiState(loading = false, items = emptyList(), error = null)
        _likedCurations.value = emptyList()
        _likedLoading.value = false
        _likedError.value = null
        _errorMessage.value = null           // ❗️사용자에겐 조용히
        _isGenerating.value = false
        if (markPrefetched) hasPrefetched = true  // 리트라이 루프 방
    }

    /** 외부에서 로그인/로그아웃 변화 시 호출 */
    fun invalidate() {
        hasPrefetched = false
    }


    // 최근 검색 기록 전체 삭제
    fun clearRecentQuery() {
        Log.d("CurationViewModel", "clearRecentQuery")

        viewModelScope.launch {
            Log.d("CurationViewModel", "clearRecentQuery launch")

            try{
                Log.d("CurationViewModel", "clearRecentQuery try")

                recentRepository.clear()

            }catch (e: Exception){
                Log.d("CurationViewModel", "clearRecentQuery catch: $e.message")
            }finally {
                Log.d("CurationViewModel", "clearRecentQuery finally")
            }
        }
        Log.d("CurationViewModel", "clearRecentQuery return")
    }
    // ---------- search method ----------
}




