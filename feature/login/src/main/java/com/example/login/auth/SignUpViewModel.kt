package com.example.login.auth


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 회원가입 흐름을 관리하는 ViewModel
 * - 회원가입 전체 데이터(이메일, 비밀번호, 닉네임, 성별, 직업, 목적, 관심사) 저장
 * - 닉네임 중복 확인 API 연동 포함
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 회원가입 전체 데이터
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var nickname by mutableStateOf("")
    var gender by mutableStateOf(0)
    var jobId by mutableStateOf(0)
    var purposeList by mutableStateOf<List<String>>(emptyList())
    var interestList by mutableStateOf<List<String>>(emptyList())

    // 상태 관리
    private val _isNicknameAvailable = MutableStateFlow<Boolean?>(null)
    val isNicknameAvailable: StateFlow<Boolean?> = _isNicknameAvailable

    private val _nicknameMessage = MutableStateFlow<String?>(null)
    val nicknameMessage: StateFlow<String?> = _nicknameMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _signUpSuccess = MutableStateFlow<Boolean?>(null)
    val signUpSuccess: StateFlow<Boolean?> = _signUpSuccess

    /**
     * 닉네임 중복 확인 (Boolean으로 단순화)
     */
    fun checkNickname() {
        if (nickname.isBlank()) {
            _isNicknameAvailable.value = null
            _nicknameMessage.value = "닉네임을 입력해주세요"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val available = userRepository.checkNickname(nickname)  // Boolean 반환
                _isNicknameAvailable.value = available
                _nicknameMessage.value = if (available) {
                    "사용 가능한 닉네임입니다."
                } else {
                    "이미 사용 중인 닉네임입니다."
                }
                Log.d("SignUpViewModel", "닉네임 확인 → $available")
            } catch (e: Exception) {
                _isNicknameAvailable.value = false
                _nicknameMessage.value = "닉네임 확인 실패"
                Log.e("SignUpViewModel", "닉네임 확인 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 회원가입 (Boolean으로 단순화)
     */
    fun signUp() {
        viewModelScope.launch {
            try {
                val success = userRepository.signUp(
                    nickname = nickname,
                    email = email,
                    password = password,
                    gender = gender,
                    jobId = jobId,
                    purposeList = purposeList,
                    interestList = interestList
                )
                _signUpSuccess.value = success
                Log.d("SignUpViewModel", "회원가입 성공 여부 → $success")
            } catch (e: Exception) {
                _signUpSuccess.value = false
                Log.e("SignUpViewModel", "회원가입 실패", e)
            }
        }
    }

    /**
     * Preview 용 더미 UserRepository
     */
    constructor() : this(object : UserRepository {
        override suspend fun checkNickname(nickname: String) = true

        override suspend fun login(email: String, password: String) =
            com.example.core.model.LoginResult(0, "dummy", "active")

        override suspend fun signUp(
            nickname: String,
            email: String,
            password: String,
            gender: Int,
            jobId: Int,
            purposeList: List<String>,
            interestList: List<String>
        ) = true

        override suspend fun sendEmailCode(email: String) = true
        override suspend fun verifyEmailCode(email: String, code: String) = true
        override suspend fun deleteUser(reason: String) = true
    })
}




//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//
//class SignUpViewModel : ViewModel() {
//    var email by mutableStateOf("")
//    var password by mutableStateOf("")
//    var nickname by mutableStateOf("")
//    var gender by mutableStateOf(0)
//    var jobId by mutableStateOf(0)
//
//    var purposeList by mutableStateOf<List<String>>(emptyList())
//    var interestList by mutableStateOf<List<String>>(emptyList())
//}