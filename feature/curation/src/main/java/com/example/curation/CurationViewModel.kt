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

    fun generateMonthlyCuration(userId: Long) {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                // Step 1. 큐레이션 생성
                repository.generateMonthlyCuration(userId)

                // Step 2. 생성된 큐레이션 조회
                val response = repository.getMyRecentCuration(userId)

                // Step 3. 상태 업데이트
                _recentCuration.value = response

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "큐레이션 생성 실패"
            } finally {
                _isGenerating.value = false
            }


        }
    }
    //닉네임 가져오기.
    fun loadNickname() {
        viewModelScope.launch {
            val userId = authPreference.userId ?: -1L
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

            val userId = authPreference.userId
            val rawToken = authPreference.accessToken

            Log.d("CurationDebug", "[전송 전] userId=$userId, token=$rawToken")

            if (userId == null || userId == -1L || rawToken.isNullOrBlank()) {
                _errorMessage.value = "로그인이 필요합니다."
                _isGenerating.value = false
                Log.e("CurationViewModel", "[ERROR] 유효하지 않은 userId($userId) 또는 token(null)")
                return@launch
            }

            try {
                Log.d("CurationViewModel", "[요청] generateMonthlyCuration(userId=$userId)")
                repository.generateMonthlyCuration(userId) // userId는 이 시점에서 절대 null 아님
                Log.d("CurationViewModel", "[성공] generateMonthlyCuration 완료")

                delay(500L)

                Log.d("CurationViewModel", "[요청] getMyRecentCuration(userId=$userId)")
                val response = repository.getMyRecentCuration(userId)

                Log.d("CurationViewModel", "[응답] curationId=${response.id}, month=${response.month}, thumbnailUrl=${response.thumbnailUrl}")

                _recentCuration.value = response

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "큐레이션 생성 실패"
                Log.e("CurationViewModel", "[ERROR] 큐레이션 생성 실패", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }
    init {
        loadNickname()
        loadMonthlyCuration()
    }
}




