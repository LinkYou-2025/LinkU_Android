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
import kotlinx.coroutines.delay
import android.util.Log

@HiltViewModel
class CurationViewModel @Inject constructor(
    private val repository: CurationRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference
) : ViewModel() {

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


    //닉네임 가져오기.
    fun loadNickname() {
        viewModelScope.launch {
            val userId = authPreference.userId ?: -1L
            _userId.value = userId
            if (userId != -1L) {
                val name = userRepository.getUserInfo(userId)
                _nickname.value = name
            }
        }
    }
    fun loadMonthlyCuration() {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null

            val uid = authPreference.userId ?: -1L
            _userId.value = uid
            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $uid")

            if (uid == -1L) {
                _errorMessage.value = "로그인이 필요합니다."
                _isGenerating.value = false
                Log.w("CurationVM", "userId가 null 또는 -1L")
                return@launch
            }

            try {
                val response = repository.getMyRecentCuration(uid)
                _recentCuration.value = response
                // 현재 큐레이션 ID도 갱신 (CurationItem의 실제 필드명에 맞춰 수정)
                _currentCurationId.value = response.id

                // 첫 진입에 바로 추천 Top2 로드
                loadHomeRecommendedLinksTop2(uid, response.id)
                loadLikedCurations()

                Log.d("CurationVM", "큐레이션 불러오기 성공: $response")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "큐레이션 조회 실패"
                Log.e("CurationVM", "큐레이션 불러오기 실패", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }
//    init {
//        loadNickname()
//        loadMonthlyCuration()
//    }

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
                        items = list,   // 최대 2개
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
}




