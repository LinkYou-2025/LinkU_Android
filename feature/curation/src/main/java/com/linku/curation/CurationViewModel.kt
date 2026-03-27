package com.linku.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.linku.core.model.CurationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.design.top.search.FastSearchItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class CurationViewModel @Inject constructor(
    private val repository: CurationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val loginSessionStore: LoginSessionStore,
    private val recentRepository: RecentSearchRepository,
    private val linkuRepository: LinkuRepository,
) : ViewModel() {

    private var hasPrefetched = false //한 번만 실행.

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val session = loginSessionStore.session //닉네임, 직업 정보

    // 닉네임, 직업 session에서 직접
    val nickname = loginSessionStore.session
        .map { it.nickname ?: "세나" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "세나")

    val jobName = loginSessionStore.session
        .map { it.jobName ?: "직장인" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "직장인")

    private val _recentCuration = MutableStateFlow<CurationItem?>(null)
    val recentCuration: StateFlow<CurationItem?> = _recentCuration


    private val _currentCurationId = MutableStateFlow(-1L)
    val currentCurationId: StateFlow<Long> = _currentCurationId


    // 로딩/중복탭 방지 플래그(선택)
    private val _likeBusy = MutableStateFlow(false)
    val likeBusy: StateFlow<Boolean> = _likeBusy

    // --- 공통 uid 가드
    private fun requireUserId(): Long {
        val uid = authPreference.userId ?: -1L
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




