package com.example.login.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Job

sealed class EmailAuthState {
    object Idle : EmailAuthState()
    object Sending : EmailAuthState()
    data class SendSuccess(val message: String) : EmailAuthState()
    data class SendError(val message: String) : EmailAuthState()
    object Verifying : EmailAuthState()
    object VerifySuccess : EmailAuthState()
    data class VerifyError(val message: String) : EmailAuthState()
}

// 에러 메시지 상수
object AuthErrorMessages {
    const val INVALID_EMAIL_FORMAT = "잘못된 이메일 형식"
    const val EMAIL_ALREADY_EXISTS = "이미 가입된 이메일입니다."
    const val SERVER_ERROR = "서버 오류"
    const val VERIFY_FAILED = "인증 실패"
    const val NETWORK_ERROR = "네트워크 오류"
    const val INVALID_CODE = "이메일 인증 코드가 잘못 입력 되었습니다."
}

//여기 api 전면 수정 예정. 실제 api 연동은 1월 말~ 2월 초
// TODO : 하진 언니에게 otp 번호 생성은 백에서 할 수 있도록 수정 요청하기!
@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TIMER_DURATION = 180 // 3분
    }

    private val _authState = MutableStateFlow<EmailAuthState>(EmailAuthState.Idle)
    val authState: StateFlow<EmailAuthState> = _authState

    // 타이머 추가
    private val _timer = MutableStateFlow(0)
    val timer: StateFlow<Int> = _timer

    private var timerJob: Job? = null

    // 타이머 시작
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timer.value = TIMER_DURATION
            while (_timer.value > 0) {
                delay(1000)
                _timer.value -= 1
            }
        }
    }

    // 타이머 중지
    private fun stopTimer() {
        timerJob?.cancel()
        _timer.value = 0
    }

    // 전체 리셋 - 타이머 호출
    fun resetAll() {
        stopTimer()
        _authState.value = EmailAuthState.Idle
    }


    //상태 초기화 (화면 재진입/재시도 시 호출)
    fun reset() {
        _authState.value = EmailAuthState.Idle
    }


    // 6자리 랜덤 코드 생성 함수
    private fun generateRandomSixDigitCode(): String {
        return Random.Default.nextInt(0, 1_000_000)
            .toString()
            .padStart(6, '0')
    }

    // ResponseBody → 서버에서 내려주는 JSON 중 "message" 키값만 안전하게 추출
    private fun ResponseBody.safeStringMessage(): String? = try {
        val s = string() // 전체 response body 문자열
        val msg = JSONObject(s).optString("message", "")
        if (msg.isBlank()) null else msg  // 빈 문자열이면 null로 처리
    } catch (_: Throwable) {
        null
    }

    /** 이메일 인증 코드 전송 */
    fun sendEmailCode(email: String) {
        Log.d("EmailAuthVM", "sendEmailCode() called. email=$email")
        viewModelScope.launch {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Log.w("EmailAuthVM", "Invalid email format: $email")
                _authState.value = EmailAuthState.SendError(AuthErrorMessages.INVALID_EMAIL_FORMAT)
                return@launch
            }

            _authState.value = EmailAuthState.Sending
            val code = generateRandomSixDigitCode()

            Log.d("EmailAuthVM", "Generated code = $code")
            try {
                val ok = userRepository.sendEmailCode(email, code)
                Log.d("EmailAuthVM", "Server sendEmailCode result = $ok")

                if (ok) {
                    _authState.value = EmailAuthState.SendSuccess("인증 코드 전송 성공")
                    startTimer() // 성공 시 타이머 시작
                } else {
                    _authState.value = EmailAuthState.SendError(AuthErrorMessages.SERVER_ERROR)
                }
            } catch (e: HttpException) {
                Log.e("EmailAuthVM", "HttpException in sendEmailCode", e)
                _authState.value = when (e.code()) {
                    409 -> EmailAuthState.SendError(AuthErrorMessages.EMAIL_ALREADY_EXISTS)
                    else -> EmailAuthState.SendError(AuthErrorMessages.SERVER_ERROR)
                }
            } catch (e: Exception) {
                _authState.value = EmailAuthState.SendError(AuthErrorMessages.SERVER_ERROR)
            }
        }
    }

    // 이메일 인증 코드 전송
    fun verifyEmailCode(email: String, code: String) {
        Log.d("EmailAuthVM", "verifyEmailCode() called. email=$email, code=$code")
        viewModelScope.launch {
            _authState.value = EmailAuthState.Verifying
            try {
                val ok = userRepository.verifyEmailCode(email, code)
                if (ok) {
                    stopTimer() // 성공 시 타이머 중지
                    _authState.value = EmailAuthState.VerifySuccess
                } else {
                    _authState.value = EmailAuthState.VerifyError(AuthErrorMessages.VERIFY_FAILED)
                }
            } catch (e: Exception) {
                _authState.value = EmailAuthState.VerifyError(AuthErrorMessages.NETWORK_ERROR)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}