package com.example.login.auth
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.LoginRepository
import com.example.core.api.NicknameResponse
import com.example.core.api.SignUpRequest
import com.example.core.api.SignUpResponse
import com.example.core.api.SignUpResult
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
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpResponse?>(null)
    val signUpState: StateFlow<SignUpResponse?> = _signUpState

    // Preview에서 사용할 보조 생성자
    constructor() : this(object : LoginRepository {
        override suspend fun checkNickname(nickname: String) = NicknameResponse(
            isSuccess = true,
            code = "200",
            message = "사용 가능한 닉네임",
            result = "사용 가능"
        )

        override suspend fun login(email: String, password: String) =
            throw NotImplementedError("Preview 용 login 미구현")

        override suspend fun sendEmailCode(email: String) =
            throw NotImplementedError("Preview 용 sendEmailCode 미구현")

        override suspend fun verifyEmailCode(email: String, code: String) =
            throw NotImplementedError("Preview 용 verifyEmailCode 미구현")

        override suspend fun signUp(request: SignUpRequest) = SignUpResponse(   // ✅ 추가!
            isSuccess = true,
            code = "COMMON200",
            message = "성공입니다.",
            result = SignUpResult(userId = 0, createdAt = "2025-07-18T16:07:12")
        )
    })


    // 회원가입 전체 데이터
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var nickname by mutableStateOf("")
    var gender by mutableStateOf(0)
    var jobId by mutableStateOf(0)
    var purposeList by mutableStateOf<List<String>>(emptyList())
    var interestList by mutableStateOf<List<String>>(emptyList())

    // 닉네임 중복 여부 상태 (null → 아직 확인 안함 / true → 사용 가능 / false → 중복됨)
    private val _isNicknameAvailable = MutableStateFlow<Boolean?>(null)
    val isNicknameAvailable: StateFlow<Boolean?> = _isNicknameAvailable


    // API 요청 중 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 서버 응답 메시지 (예: 사용 가능, 중복됨, 서버 요청 실패)
    private val _nicknameMessage = MutableStateFlow<String?>(null)
    val nicknameMessage: StateFlow<String?> = _nicknameMessage

    /**
     * 닉네임 중복 확인 API 호출
     * - loginRepository를 사용하여 서버에 요청
     */
    fun checkNickname() {
        if (nickname.isBlank()) {
            _nicknameMessage.value = "닉네임을 입력해주세요"
            _isNicknameAvailable.value = null
            return
        }

        viewModelScope.launch {
            try {
                Log.d("SignUpViewModel", "API 요청 → nickname=$nickname")
                _isLoading.value = true

                val response: NicknameResponse = loginRepository.checkNickname(nickname)
                Log.d(
                    "SignUpViewModel",
                    "서버 응답 → success=${response.isSuccess}, message=${response.message}, result=${response.result}"
                )

                if (response.isSuccess) {
                    _isNicknameAvailable.value = true
                    _nicknameMessage.value = response.result ?: "사용 가능한 닉네임입니다."
                } else {
                    _isNicknameAvailable.value = false
                    _nicknameMessage.value = response.result ?: "중복된 닉네임입니다."
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("SignUpViewModel", "API 호출 중 예외 발생: HTTP ${e.code()} ", e)
                _isNicknameAvailable.value = false
                _nicknameMessage.value = "서버 요청 실패 (HTTP ${e.code()})"
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "API 호출 중 알 수 없는 오류", e)
                _isNicknameAvailable.value = false
                _nicknameMessage.value = "서버 요청 실패"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            val request = SignUpRequest(
                nickName = nickname,
                email = email,
                password = password,
                gender = gender,
                jobId = jobId,
                purposeList = purposeList,
                interestList = interestList
            )

            try {
                val response = loginRepository.signUp(request)
                Log.d("SignUpViewModel", "회원가입 성공 → ${response.message}")
                _signUpState.value = response   // UI에서 감지 가능
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "회원가입 실패", e)
                _signUpState.value = null       // 실패 시 null 전달
            }
        }
    }

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