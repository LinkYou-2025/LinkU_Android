package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiSideEffect

internal sealed interface LoginUiEffect : UiSideEffect {
    object LoginSuccess : LoginUiEffect

    // 탈퇴 유예기간(INACTIVE) 계정으로 로그인 시도 시 복구 여부를 묻는 모달을 띄우라는 1회성 이벤트
    object ShowRecoverModal : LoginUiEffect
}