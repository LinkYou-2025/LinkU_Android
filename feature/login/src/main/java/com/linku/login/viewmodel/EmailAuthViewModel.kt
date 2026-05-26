package com.linku.login.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.AppError
import com.linku.core.repository.AuthRepository
import com.linku.login.viewmodel.state.EmailUiEvent
import com.linku.login.viewmodel.state.EmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class EmailAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _emailUiState = MutableStateFlow(EmailUiState())
    val emailUiState: StateFlow<EmailUiState> = _emailUiState.asStateFlow()

    private var timerJob: Job? = null

    fun onEvent(event: EmailUiEvent) {
        when (event) {
            is EmailUiEvent.EmailChanged -> handleEmailChanged(event.email)
            is EmailUiEvent.CodeChanged -> handleCodeChanged(event.code)
            is EmailUiEvent.SendCodeClicked -> sendEmailCode()
            is EmailUiEvent.VerifyCodeClicked -> verifyEmailCode()
            is EmailUiEvent.ClearStatus -> resetAll()
            is EmailUiEvent.ToastShown -> handleToastShown()
        }
    }

    private fun handleToastShown() {
        _emailUiState.update { it.copy(failureToastMessage = null) } // 중복 실행 방지
    }

    private fun handleEmailChanged(newEmail: String) {
        _emailUiState.update { it.copy(email = newEmail, emailError = null) }
    }

    private fun handleCodeChanged(newCode: String) {
        if (newCode.length <= 6) {
            _emailUiState.update { it.copy(code = newCode, codeError = null) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var currentTimer = 180
            _emailUiState.update {
                it.copy(timer = currentTimer, isCodeSent = true, verificationFailCount = 0)
            }

            while (currentTimer > 0) {
                delay(1000)
                currentTimer--
                _emailUiState.update { it.copy(timer = currentTimer) }
            }
            _emailUiState.update { it.copy(isCodeSent = false, codeError = "인증 시간이 만료되었습니다.") }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _emailUiState.update { it.copy(timer = 0) }
    }

    /** 이메일 인증 코드 전송 및 재발송 */
    private fun sendEmailCode() {
        val email = _emailUiState.value.email.trim()
        Log.d("EmailAuthVM", "sendEmailCode() called. email=$email")

        // 이메일 api 전송 전 사용자 입력 검즘(빈 값, 형식)
        if (email.isBlank()) {
            _emailUiState.update { it.copy(emailError = "이메일을 입력해주세요.") }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailUiState.update { it.copy(emailError = "이메일 양식이 올바르지 않습니다.") }
            return
        }

        viewModelScope.launch {
            _emailUiState.update {
                it.copy(
                    isLoading = true,
                    emailError = null,
                    code = "",
                    codeError = null,
                    verificationFailCount = 0
                )
            }

            authRepository.sendEmailCode(email)
                .fold(
                    onSuccess = {
                        _emailUiState.update { it.copy(isLoading = false) }
                        startTimer()
                    },
                    onFailure = { exception ->
                        Log.e("EmailAuthVM", "sendEmailCode 실패", exception)

                        val message = (exception as? AppError)?.displayMessage ?: "서버 오류가 발생했습니다."
                        _emailUiState.update { it.copy(isLoading = false, emailError = message) }
                    }
                )
        }
    }


    /** 이메일 인증 코드 검증 */
    private fun verifyEmailCode() {

        val email = _emailUiState.value.email.trim()
        val code = _emailUiState.value.code.trim()
        Log.d("EmailAuthVM", "verifyEmailCode() called. email=$email, code=$code")

        // 인증 코드 누락 방어
        if (code.isBlank()) {
            _emailUiState.update { it.copy(codeError = "코드를 입력해주세요.") }
            return
        }

        // 타이머 만료시 서버 요청 차단
        if (_emailUiState.value.timer <= 0) {
            _emailUiState.update { it.copy(codeError = "인증 시간이 초과되었습니다. 다시 발송해주세요.") }
            return
        }

        viewModelScope.launch {
            _emailUiState.update { it.copy(isLoading = true, codeError = null) }

            authRepository.verifyEmailCode(email, code)
                .fold(
                    onSuccess = {
                        stopTimer()
                        _emailUiState.update { it.copy(isLoading = false, isVerifySuccess = true) }

                    },
                    onFailure = { exception ->
                        Log.e("EmailAuthVM", "verifyEmailCode 실패", exception)
                        val message = (exception as? AppError)?.displayMessage ?: "서버 오류가 발생했습니다."

                        val emailFailCount = _emailUiState.value.verificationFailCount + 1

                        _emailUiState.update {
                            it.copy(
                                isLoading = false,
                                codeError = message,
                                verificationFailCount = emailFailCount,
                                failureToastMessage = "인증번호가 올바르지 않습니다. (인증 실패 횟수: 총 $emailFailCount/5번)"
                            )
                        }
                    }
                )
        }
    }

    fun resetAll() {
        stopTimer()
        _emailUiState.value = EmailUiState()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}