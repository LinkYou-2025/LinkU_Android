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
            Log.d("CurationVM", "큐레이션 불러오기 시작 - userId: $userId")

            if (userId == null || userId == -1L) {
                _errorMessage.value = "로그인이 필요합니다."
                _isGenerating.value = false
                Log.w("CurationVM", "userId가 null 또는 -1L")
                return@launch
            }

            try {
                val response = repository.getMyRecentCuration(userId)
                _recentCuration.value = response
                Log.d("CurationVM", "큐레이션 불러오기 성공: $response")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "큐레이션 조회 실패"
                Log.e("CurationVM", "큐레이션 불러오기 실패", e)
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




