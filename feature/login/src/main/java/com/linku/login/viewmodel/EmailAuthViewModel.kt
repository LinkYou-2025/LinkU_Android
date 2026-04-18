package com.linku.login.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.auth.AuthErrorMessages
import com.linku.core.model.auth.EmailAuthState
import com.linku.core.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlinx.coroutines.Job


@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
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

    /** 이메일 인증 코드 전송 */
    fun sendEmailCode(email: String) {
        Log.d("EmailAuthVM", "sendEmailCode() called. email=$email")
        viewModelScope.launch {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _authState.value = EmailAuthState.SendError(AuthErrorMessages.INVALID_EMAIL_FORMAT)
                return@launch
            }

            _authState.value = EmailAuthState.Sending

            try {
                authRepository.sendEmailCode(email)  // Unit, ok 받지 않음
                _authState.value = EmailAuthState.SendSuccess("인증 코드 전송 성공")
                startTimer()
            } catch (e: HttpException) {
                Log.e("EmailAuthVM", "HttpException in sendEmailCode", e)
                _authState.value = when (e.code()) {
                    409 -> EmailAuthState.SendError(AuthErrorMessages.EMAIL_ALREADY_EXISTS)
                    else -> EmailAuthState.SendError(AuthErrorMessages.SERVER_ERROR)
                }
            } catch (e: Exception) {
                Log.e("EmailAuthVM", "Exception in sendEmailCode", e)
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
                val ok = authRepository.verifyEmailCode(email, code)
                if (ok) {
                    stopTimer()
                    _authState.value = EmailAuthState.VerifySuccess
                } else {
                    _authState.value = EmailAuthState.VerifyError(AuthErrorMessages.VERIFY_FAILED)
                }
            } catch (e: Exception) {
                Log.e("EmailAuthVM", "Exception in verifyEmailCode", e)
                _authState.value = EmailAuthState.VerifyError(AuthErrorMessages.NETWORK_ERROR)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}