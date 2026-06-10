package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiSideEffect

/**
 * 비밀번호 입력 화면 전용 일회성 부수 효과 명세서
 */
internal sealed interface SignUpEffect : UiSideEffect {
    object NavigateToNickname : SignUpEffect
    object NavigateToGender : SignUpEffect
    object NavigateToJob : SignUpEffect
    object NavigateToPurpose : SignUpEffect
    object NavigateToInterest : SignUpEffect
    object NavigateToWelcome : SignUpEffect
}