package com.linku.login.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.repository.AuthRepository
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.ResetPasswordEffect
import com.linku.login.viewmodel.state.ResetPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 비밀번호 재설정 화면의 ViewModel
 *
 * - 이메일 입력 및 유효성 검사
 * - 메일 발송 API 호출 (TODO)
 * - 성공 시 [ResetPasswordEffect.NavigateToEmailLogin] 발행
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), MviContainer<ResetPasswordState, ResetPasswordEffect>
by mviContainer(ResetPasswordState()) {

    /**
     * 이메일 입력값 변경 시 호출
     * 이메일 형식 유효성을 함께 갱신하고 기존 에러를 초기화함
     */
    fun onEmailChanged(email: String) {
        updateState {
            copy(
                email = email,
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                error = null
            )
        }
    }

    /**
     * 메일 보내기 버튼 클릭 시 호출
     * 성공 시 성공 다이얼로그 표시, 실패 시 에러 메시지 표시
     */
    fun onSendEmailClicked() {
        if (state.value.isLoading) return

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            authRepository.sendPasswordResetEmail(state.value.email)
                .onSuccess {
                    updateState { copy(isLoading = false, showSuccessDialog = true) }
                }
                .onFailure { e ->
                    // 이미 소셜 로그인으로 가입된 이메일은 발송 실패 문구 대신 별도의
                    // alert로 소셜 로그인을 안내해야 하므로 먼저 분기함.
                    if (e is ApiError.User.SocialAlreadyRegistered) {
                        updateState { copy(isLoading = false, socialAlertProviders = e.providers) }
                        return@onFailure
                    }
                    // 아예 가입되지 않은 이메일(404)도 발송 실패 문구 대신 회원가입 안내 alert로 분기함.
                    if (e is ApiError.User.NotFound) {
                        updateState { copy(isLoading = false, showNotRegisteredAlert = true) }
                        return@onFailure
                    }
                    updateState {
                        copy(
                            isLoading = false,
                            error = e.message ?: "전송에 실패했어요. 이메일을 확인하고 다시 시도해 주세요."
                        )
                    }
                }
        }
    }

    /**
     * 성공 다이얼로그 확인 버튼 클릭 시 호출
     * 로그인 화면으로 이동
     */
    fun onSuccessDialogConfirmed() {
        updateState { copy(showSuccessDialog = false) }
        postSideEffect(ResetPasswordEffect.NavigateToEmailLogin)
    }

    /**
     * 소셜 계정 안내 alert의 확인 버튼 클릭 시 호출
     * 소셜 로그인이 가능한 LoginScreen으로 이동
     */
    fun onSocialAccountAlertConfirmed() {
        updateState { copy(socialAlertProviders = emptyList()) }
        postSideEffect(ResetPasswordEffect.NavigateToLogin)
    }

    /**
     * 회원가입 안내 alert(가입되지 않은 이메일)의 확인 버튼 클릭 시 호출
     * 소셜/이메일 로그인을 모두 선택할 수 있는 LoginScreen으로 이동
     */
    fun onNotRegisteredAlertConfirmed() {
        updateState { copy(showNotRegisteredAlert = false) }
        postSideEffect(ResetPasswordEffect.NavigateToLogin)
    }
}