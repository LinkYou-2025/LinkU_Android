package com.linku.login.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.repository.AuthRepository
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.EmailUiEffect
import com.linku.login.viewmodel.state.EmailUiEvent
import com.linku.login.viewmodel.state.EmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class EmailAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), MviContainer<EmailUiState, EmailUiEffect> by mviContainer(EmailUiState()) {

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
        updateState { copy(failureToastMessage = null) }
    }

    private fun handleEmailChanged(newEmail: String) {
        updateState { copy(email = newEmail, emailError = null) }
    }

    private fun handleCodeChanged(newCode: String) {
        if (newCode.length <= 6) {
            updateState { copy(code = newCode, codeError = null, isCodeExpired = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var currentTimer = 180
            updateState {
                copy(
                    timer = currentTimer,
                    isCodeSent = true,
                    verificationFailCount = 0,
                    codeError = null,
                    isCodeExpired = false
                )
            }

            while (currentTimer > 0) {
                delay(1000)
                currentTimer--
                updateState { copy(timer = currentTimer) }
            }
            updateState {
                copy(
                    codeError = "인증 시간이 만료되었어요. 인증번호를 다시 요청해주세요.",
                    isCodeExpired = true
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        updateState { copy(timer = 0) }
    }

    /** 이메일 인증 코드 전송 및 재발송 */
    private fun sendEmailCode() {
        val email = state.value.email.trim()
        Log.d("EmailAuthVM", "sendEmailCode() called. email=$email")

        // 버튼에서 막고 있기는 한데 그래도 혹시 모르는 방어 코드
        if (email.isBlank()) {
            updateState { copy(emailError = "이메일을 입력해주세요.") }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateState { copy(emailError = "이메일 양식이 올바르지 않습니다.") }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    emailError = null,
                    code = "",
                    codeError = null,
                    isCodeExpired = false,
                    verificationFailCount = 0
                )
            }

            authRepository.sendEmailCode(email).foldApp(
                onSuccess = {
                    updateState { copy(isLoading = false) }
                    startTimer()
                },
                onFailure = { error ->
                    Log.e("EmailAuthVM", "sendEmailCode 실패", error)

                    val uiEmailError = when (error) {
                        is ApiError.User.DuplicateEmail -> "이미 가입된 이메일입니다."
                        else -> "인증 코드 발송에 실패했습니다. 다시 시도해주세요." // 그 외 에러는 마스킹(로그에서만 찍을거예용)
                    }

                    updateState { copy(isLoading = false, emailError = uiEmailError) }
                }
            )
        }
    }


    /** 이메일 인증 코드 검증 */
    private fun verifyEmailCode() {
        val email = state.value.email.trim()
        val code = state.value.code.trim()
        Log.d("EmailAuthVM", "verifyEmailCode() called. email=$email, code=$code")

        // 인증 코드 누락 방어
        if (code.isBlank()) {
            updateState { copy(codeError = "코드를 입력해주세요.", isCodeExpired = false) }
            return
        }

        // 타이머 만료시 서버 요청 차단
        if (state.value.timer <= 0) {
            updateState {
                copy(
                    codeError = "인증 시간이 만료되었어요. 인증번호를 다시 요청해주세요.",
                    isCodeExpired = true
                )
            }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, codeError = null, isCodeExpired = false) }

            authRepository.verifyEmailCode(email, code).foldApp(
                onSuccess = {
                    stopTimer()
                    updateState { copy(isLoading = false, isVerifySuccess = true) }
                    postSideEffect(EmailUiEffect.NavigateToPassword(email))
                },
                onFailure = { error ->
                    Log.e("EmailAuthVM", "verifyEmailCode 실패", error)
                    val emailFailCount = state.value.verificationFailCount + 1
                    updateState {
                        copy(
                            isLoading = false,
                            codeError = error.displayMessage,
                            isCodeExpired = false,
                            verificationFailCount = emailFailCount
                        )
                    }
                    val toastMessage = when (error) {
                        is ApiError.User.VerificationFailed -> {
                            "인증번호가 올바르지 않습니다. (인증 실패 횟수: 총 ${emailFailCount}/5번)"
                        }

                        else -> "오류가 발생했습니다. 다시 시도해주세요." // TODO : 수정하기 -> 서버가 갑자기 중단되거나 할 때, pm 멘트 정해주면 수정하기. 프론트가 제어할 수 없는 에러에 대해
                    }
                    postSideEffect(EmailUiEffect.ShowToast(toastMessage))
                }
            )
        }
    }

    fun resetAll() {
        stopTimer()
        updateState { EmailUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}