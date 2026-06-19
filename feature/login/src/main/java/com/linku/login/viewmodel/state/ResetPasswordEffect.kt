package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiSideEffect

/**
 * 비밀번호 재설정 화면의 일회성 부수 효과
 *
 * - [NavigateToEmailLogin] 메일 발송 성공 또는 뒤로가기 시 이메일 로그인 화면으로 이동
 * - [ShowError] 메일 발송 실패 시 에러 메시지 표시 (토스트 등)
 */
internal sealed interface ResetPasswordEffect : UiSideEffect {
    data object NavigateToEmailLogin : ResetPasswordEffect
    data class ShowError(val message: String) : ResetPasswordEffect
}