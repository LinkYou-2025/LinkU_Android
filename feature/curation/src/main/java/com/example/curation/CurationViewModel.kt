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
import com.example.design.FastSearchItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class CurationViewModel @Inject constructor(
    private val repository: CurationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,

    private val recentRepository: RecentSearchRepository,
    private val linkuRepository: LinkuRepository,
) : ViewModel() {

    private var hasPrefetched = false //한 번만 실행.

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname

    private val _recentCuration = MutableStateFlow<CurationItem?>(null)
    val recentCuration: StateFlow<CurationItem?> = _recentCuration

    // 추가: 네비게이션용 ID들
    private val _userId = MutableStateFlow(-1L)
    val userId: StateFlow<Long> = _userId

    private val _currentCurationId = MutableStateFlow(-1L)
    val currentCurationId: StateFlow<Long> = _currentCurationId

    // 하이라이트(현재 큐레이션) 좋아요 상태
    private val _highlightLiked = MutableStateFlow<Boolean?>(null)
    val highlightLiked: StateFlow<Boolean?> = _highlightLiked

    // 로딩/중복탭 방지 플래그(선택)
    private val _likeBusy = MutableStateFlow(false)
    val likeBusy: StateFlow<Boolean> = _likeBusy

    // --- 공통 uid 가드
    private fun requireUserId(): Long {
        val uid = authPreference.userId ?: -1L
        _userId.value = uid
        return uid
    }


    // 닉네임 가져오기
    fun loadNickname() {
        viewModelScope.launch {
            val uid = requireUserId()
            if (uid <= 0L) {
                _nickname.value = "세나"
                return@launch
            }

            runCatching { userRepository.getNickname(uid) }
                .onSuccess { nick ->
                    _nickname.value = nick ?: "세나"
                }
                .onFailure { e ->
                    Log.e("UserRepository", "닉네임 가져오기 실패", e)
                    _nickname.value = "세나"
                }
        }
    }
    // retrofit2에 의존하지 않고, 예외에 code() 메서드가 있으면 꺼내오는 유틸
    private fun httpStatusCodeOrMinus1(throwable: Throwable): Int {
        return runCatching {
            val m = throwable::class.java.methods
                .firstOrNull { it.name == "code" && it.parameterCount == 0 }
            (m?.invoke(throwable) as? Int) ?: -1
        }.getOrDefault(-1)
    }

    fun loadMonthlyCuration() {
        viewModelScope.launch {
            if (hasPrefetched) return@launch

            _isGenerating.value = true
            _errorMessage.value = null

            val uid = requireUserId()
            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")

            // ⬇️ 로그인 안돼 있으면 ‘빈 상태’로 조용히 표시하고 종료
            if (uid <= 0L) {
                setEmptyCurationState(markPrefetched = true)
                return@launch
            }

            try {
                val item = repository.getMyRecentCuration(uid)
                if (item == null || item.id <= 0L) {
                    setEmptyCurationState(markPrefetched = true)
                    return@launch
                }

                _recentCuration.value = item
                _currentCurationId.value = item.id

                // 현재 큐레이션 좋아요 상태
                runCatching { repository.isCurationLiked(item.id, uid) }
                    .onSuccess { _highlightLiked.value = it }
                    .onFailure { _highlightLiked.value = false }

                // 추천 2개 + 좋아요 리스트
                loadHomeRecommendedLinksTop2(uid, item.id)
                loadLikedCurations()

                Log.d("CurationVM", "큐레이션 불러오기 성공: $item")
                hasPrefetched = true
            } catch (e: Exception) {
                // ⬇️ retrofit2 없이도 403/404만 골라냄
                val code = httpStatusCodeOrMinus1(e)
                if (code == 403 || code == 404) {
                    Log.w("CurationVM", "큐레이션 없음/권한 없음(HTTP $code) → 빈 상태 표시")
                    setEmptyCurationState(markPrefetched = true)
                } else {
                    val msg = e.message.orEmpty()
                    _errorMessage.value = when {
                        msg.contains("Token", true) && msg.contains("expired", true) ->
                            "세션이 만료됐어요. 다시 로그인해 주세요."
                        else -> "큐레이션 조회에 실패했어요. 잠시 후 다시 시도해 주세요."
                    }
                    Log.e("CurationVM", "큐레이션 불러오기 실패(code=$code)", e)
                    hasPrefetched = false
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }
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
    val cid = _currentCurationId.value
    val uid = requireUserId()
    val current = _highlightLiked.value ?: false
    if (cid <= 0 || uid <= 0 || _likeBusy.value) return

    viewModelScope.launch {
        _likeBusy.value = true
        _highlightLiked.value = !current

        val result = runCatching {
            if (current) repository.unlikeCuration(cid, uid)
            else repository.likeCuration(cid, uid)
        }

        result.onSuccess {
            // ✅ 좋아요 상태에 맞춰 홈의 liked 리스트도 즉시 반영
            if (current) {
                // 해지 → 리스트에서 제거
                _likedCurations.value = _likedCurations.value.filterNot { it.id == cid }
            } else {
                // 등록 → 새로고침(간단) 또는 즉시 추가(고급)
                refreshLikedCurations(uid) // 간단: 서버 리스트 재조회
                // 또는 즉시 추가하려면 recentCuration 스냅샷으로 add:
                // recentCuration.value?.let { cur ->
                //     val item = LikedCuration(id = cid, month = cur.month, thumbnailUrl = cur.thumbnailUrl)
                //     _likedCurations.value = (_likedCurations.value + item).distinctBy { it.id }
                // }
            }
        }.onFailure { e ->
            // 롤백
            _highlightLiked.value = current
            _likedError.value = if (e.message?.contains("Token", true) == true &&
                e.message?.contains("expired", true) == true
            ) "세션이 만료됐어요. 다시 로그인해 주세요." else "좋아요 처리에 실패했어요"
        }

        _likeBusy.value = false
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

    //큐레이션 추천
    private val _likedCurations = MutableStateFlow<List<CurationItem>>(emptyList())
    val likedCurations: StateFlow<List<CurationItem>> = _likedCurations

    private val _likedLoading = MutableStateFlow(false)
    val likedLoading: StateFlow<Boolean> = _likedLoading

    private val _likedError = MutableStateFlow<String?>(null)
    val likedError: StateFlow<String?> = _likedError

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
        loadNickname()
        loadMonthlyCuration()

    }


    fun unlikeFromLikedList(curationId: Long) {
        viewModelScope.launch {
            val uid = authPreference.userId ?: -1L
            if (uid <= 0) return@launch

            val before = _likedCurations.value
            _likedCurations.value = before.filterNot { it.id == curationId } // 낙관적 제거

            runCatching { repository.unlikeCuration(curationId, uid) }
                .onFailure { e ->
                    _likedCurations.value = before // 롤백
                    _likedError.value = e.message ?: "좋아요 취소에 실패했어요"
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
        viewModelScope.launch {
            val uid = authPreference.userId ?: -1L
            if (uid <= 0 || curationId <= 0) return@launch
            runCatching { repository.isCurationLiked(curationId, uid) }
                .onSuccess { _highlightLiked.value = it }
                .onFailure { _highlightLiked.value = false }
        }
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
        _highlightLiked.value = null
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




