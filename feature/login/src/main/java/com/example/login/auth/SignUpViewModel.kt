package com.example.login.auth


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.UserInfo
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

    // ===== 동의 상태 추가 (여기만 새로 추가) =====
    private val _agreeTerms    = MutableStateFlow(false) // 이용약관(필수)
    val agreeTerms: StateFlow<Boolean> = _agreeTerms

    private val _agreePrivacy  = MutableStateFlow(false) // 개인정보(필수)
    val agreePrivacy: StateFlow<Boolean> = _agreePrivacy

    private val _agreeMarketing= MutableStateFlow(false) // 마케팅(선택)
    val agreeMarketing: StateFlow<Boolean> = _agreeMarketing

    fun setAgreeTerms(v: Boolean)     { _agreeTerms.value = v }
    fun setAgreePrivacy(v: Boolean)   { _agreePrivacy.value = v }
    fun setAgreeMarketing(v: Boolean) { _agreeMarketing.value = v }
    fun setAgreeAll(v: Boolean) {
        _agreeTerms.value = v
        _agreePrivacy.value = v
        _agreeMarketing.value = v
    }



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
                // 내가 보낸 닉네임 확인
                Log.d("SignUpViewModel", " [닉네임 중복 확인 요청] nickname = $nickname")

                val available = userRepository.checkNickname(nickname)  // Boolean 반환

                //  서버 응답 로그
                Log.d("SignUpViewModel", " [닉네임 중복 확인 응답] available = $available")

                _isNicknameAvailable.value = available
                _nicknameMessage.value = if (available) {
                    "사용 가능한 닉네임입니다."
                } else {
                    "이미 사용 중인 닉네임입니다."
                }
            } catch (e: Exception) {
                _isNicknameAvailable.value = false
                _nicknameMessage.value = "닉네임 확인 실패"
                Log.e("SignUpViewModel", " [닉네임 확인 실패]", e)
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
                //내가 서버로 보낼 데이터 전체 확인
                Log.d("SignUpViewModel", """
                [회원가입 요청 데이터]
                email = $email
                password = $password
                nickname = $nickname
                gender = $gender
                jobId = $jobId
                purposeList = $purposeList
                interestList = $interestList
            """.trimIndent())

                val success = userRepository.signUp(
                    nickname = nickname,
                    email = email,
                    password = password,
                    gender = gender,
                    jobId = jobId,
                    purposeList = purposeList,
                    interestList = interestList
                )

                Log.d("SignUpViewModel", " [회원가입 응답] success = $success")

                _signUpSuccess.value = success
            } catch (e: Exception) {
                _signUpSuccess.value = false
                Log.e("SignUpViewModel", " [회원가입 실패]", e)
            }
        }
    }



}

