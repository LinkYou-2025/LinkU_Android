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

    // 회원가입 API 성공(토큰 저장까지 완료) 시 홈으로 바로 이동
    object NavigateToHome : SignUpEffect

    // 회원가입 API 실패 시 에러 메시지 토스트로 노출
    data class ShowToast(val message: String) : SignUpEffect
}