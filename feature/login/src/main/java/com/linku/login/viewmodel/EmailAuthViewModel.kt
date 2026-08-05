package com.linku.login.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
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
        // 이미 코드가 발송된 상태(=코드 입력 화면)에서 다시 호출되면 재요청/다시요청 버튼에 의한 재발송임.
        val isResend = state.value.isCodeSent
        Log.d("EmailAuthVM", "sendEmailCode() called. email=$email, isResend=$isResend")

        // 버튼에서 막고 있기는 한데 그래도 혹시 모르는 방어 코드
        if (email.isBlank()) {
            updateState { copy(emailError = "이메일을 입력해주세요.") }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateState { copy(emailError = "이메일 양식이 올바르지 않습니다!") }
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

                    val uiMessage = when (error) {
                        is ApiError.User.DuplicateEmail -> "이미 가입된 이메일입니다."
                        is NetworkError -> "네트워크 연결을 확인해주세요."
                        else -> "인증 코드 발송에 실패했습니다. 다시 시도해주세요." // 그 외 에러는 마스킹(로그에서만 찍을거예용)
                    }

                    // 재발송(isResend=true)은 코드 입력 화면이라 emailError 텍스트가 화면에 렌더링되지 않으므로
                    // (EmailVerificationScreen에서 emailError는 이메일 입력 단계에서만 표시됨) 토스트로 알림.
                    if (isResend) {
                        updateState { copy(isLoading = false) }
                        postSideEffect(EmailUiEffect.ShowToast(uiMessage))
                    } else {
                        updateState { copy(isLoading = false, emailError = uiMessage) }
                    }
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

                    if (error is ApiError.User.ExpiredVerificationCode) {
                        // 로컬 타이머보다 서버가 먼저 만료를 감지한 경우. 로컬 타이머 만료(startTimer())와
                        // 동일한 문구/상태로 처리함 - 틀린 코드로 인한 실패가 아니므로 실패 횟수는 안 늘리고
                        // 토스트도 안 띄움(로컬 타이머 만료 때도 토스트 없이 인라인+재요청 버튼 상태 전환만 함).
                        updateState {
                            copy(
                                isLoading = false,
                                codeError = "인증 시간이 만료되었어요. 인증번호를 다시 요청해주세요.",
                                isCodeExpired = true
                            )
                        }
                        return@foldApp
                    }

                    val emailFailCount = state.value.verificationFailCount + 1
                    val inlineMessage = when (error) {
                        // Figma 지정 문구를 그대로 씀 - 서버 원본 메시지에 의존하면 문구가 어긋날 수 있음.
                        is ApiError.User.VerificationFailed -> "인증번호가 올바르지 않습니다! 다시 시도해주세요."
                        else -> error.displayMessage
                    }
                    updateState {
                        copy(
                            isLoading = false,
                            codeError = inlineMessage,
                            isCodeExpired = false,
                            verificationFailCount = emailFailCount
                        )
                    }
                    val toastMessage = when (error) {
                        is ApiError.User.VerificationFailed -> {
                            "인증번호가 올바르지 않습니다. (인증 실패 횟수: 총 ${emailFailCount}/5번)"
                        }

                        is NetworkError -> "네트워크 연결을 확인해주세요."

                        else -> "오류가 발생했습니다. 다시 시도해주세요." // 이건 뭐 서버 오류라 내가 해줄 수 있는게 없음..
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