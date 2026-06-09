package com.linku.curation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.CurationItem
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.design.top.search.FastSearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 이거 싹 다 밀어 버릴 예정..

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

    val session = authPreference.sessionState

    // TODO: 추후 UserRepository 단일 진실 공급원(SSOT) 캐싱 구조로 프로필 리팩토링 예정
    /**
     * UserRepository가 캐싱 흐름을 관리:
     * 뷰모델들이 각각 API를 치는 게 아니라, UserRepository.userInfoState: StateFlow<UserInfo?> 같은 공용 스트림을 열어둡니다.
     *
     * 뷰모델들은 그 스트림을 구독만 하고, 최초 진입점(예: MainApp 부팅 시점)이나 새로고침이 필요할 때 딱 한 번만 userRepository.fetchUserInfo()를 호출해 칠판을 업데이트하는 구조가 가장 이상적입니다.
     *
     * */
    val nickname: StateFlow<String> = MutableStateFlow("세나").asStateFlow()
    val jobName: StateFlow<String> = MutableStateFlow("직장인").asStateFlow()

    private val _recentCuration = MutableStateFlow<CurationItem?>(null)
    val recentCuration: StateFlow<CurationItem?> = _recentCuration


    private val _currentCurationId = MutableStateFlow(-1L)
    val currentCurationId: StateFlow<Long> = _currentCurationId


    // 로딩/중복탭 방지 플래그(선택)
    private val _likeBusy = MutableStateFlow(false)
    val likeBusy: StateFlow<Boolean> = _likeBusy

    // --- 공통 uid 가드
    // 아예 싹 수정될 코드로 임시로 에러 방지 위해서만 수정했습니다....
    private suspend fun requireUserId(): Long {
        val uid = authPreference.getUserId() ?: -1L
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




