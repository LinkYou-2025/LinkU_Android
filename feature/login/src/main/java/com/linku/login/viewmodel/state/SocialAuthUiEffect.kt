package com.linku.login.viewmodel.state

import com.linku.core.model.LoginResult
import com.linku.core.model.auth.LoginType
import com.linku.login.mvi.UiSideEffect

/**
 * 소셜 가입 온보딩 및 로그인 단계에서 발생하는 일회성 컴포저블 부수 효과(Side Effect)를 정의하는 인터페이스입니다.
 */
internal sealed interface SocialAuthUiEffect : UiSideEffect {

    /**
     * 가입 상태가 ACTIVE인 기존 회원이 로그인에 성공했을 때, 메인 홈 화면으로 직접 이동하도록 지시하는 부수 효과입니다.
     *
     * @property loginResult 서버로부터 반환받은 유저 ID 및 세션 토큰 정보 세트입니다.
     */
    data class NavigateToHome(val loginResult: LoginResult) : SocialAuthUiEffect

    /**
     * 가입 상태가 TEMP인 신규 회원이 인증되었을 때, 추가 성향 정보 입력 단계로 이동하도록 지시하는 부수 효과입니다.
     *
     * @property loginResult 서버로부터 반환받은 신규 임시 계정 번호 정보입니다.
     * @property loginType 인증에 사용한 소셜 공급자(카카오/구글). 온보딩 단계(`social_auth_graph`)는
     * 별도의 `SocialAuthViewModel` 인스턴스를 쓰므로, 이 값을 SavedStateHandle로 함께 넘겨줘야
     * `completeSocialProfile()` 호출 시 로그인 수단이 유실되지 않음.
     */
    data class NavigateToAdditionalInfo(val loginResult: LoginResult, val loginType: LoginType) :
        SocialAuthUiEffect

    /**
     * 소셜 필수 프로필 온보딩 단계 완료 후 최종 메인 프로세스로 진입함을 선언하는 부수 효과입니다.
     */
    object CompleteProfileSuccess : SocialAuthUiEffect

    /**
     * 화면 내 네트워크 통신 오류나 입력 유효성 실패 시 사용자에게 알림을 노출하도록 지시하는 부수 효과입니다.
     *
     * @property message 토스트 팝업 등으로 화면에 띄울 안내 텍스트 내용입니다.
     * TODO : 해당 내용은 pm과 회의 이후 달라질 수 있습니다.
     */
    data class ShowToast(val message: String) : SocialAuthUiEffect
}