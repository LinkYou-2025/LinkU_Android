package com.linku.login.viewmodel.state

/**
 * 이메일 인증 화면의 UI 상태를 통제하는 데이터 가방입니다.
 *
 * @property email 입력된 이메일 주소. (이메일 텍스트 필드 바인딩)
 * @property code 입력된 6자리 인증 코드. (인증번호 텍스트 필드 바인딩)
 * @property isCodeSent 인증번호 발송 완료 여부. (true일 때 인증번호 입력 슬롯 노출)
 * @property timer 인증 만료까지 남은 시간(초). (타이머 텍스트 뷰 표시용)
 * @property isLoading 네트워크 통신 중 여부. (프로그레스 바 노출 및 버튼 중복 클릭 방지)
 * @property emailError 이메일 유효성 및 전송 실패 에러 문구. (이메일 필드 하단 에러 텍스트)
 * @property codeError 인증 코드 검증 실패 및 시간 만료 에러 문구. (인증 필드 하단 에러 텍스트)
 * @property isVerifySuccess 이메일 최종 인증 성공 여부. (true일 때 다음 단계 화면 전환 트리거)
 * @property failureToastMessage 인증 실패 안내 및 횟수 초과 경고 메시지. (스낵바/토스트 노출용)
 * @property verificationFailCount 인증 코드 누적 실패 횟수. (5회 제한 조건 검증용)
 */
internal data class EmailUiState(
    val email: String = "",
    val code: String = "",
    val isCodeSent: Boolean = false,
    val timer: Int = 0,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val codeError: String? = null,
    val isVerifySuccess: Boolean = false,
    val failureToastMessage: String? = null,
    val verificationFailCount: Int = 0
)

/**
 * 이메일 인증 화면에서 사용자의 액션에 의해 발생할 수 있는 UI 이벤트 명세서입니다.
 *
 * @property EmailChanged 이메일 입력창의 텍스트가 변경될 때 발생.
 * @property CodeChanged 인증번호 입력창의 텍스트가 변경될 때 발생. (최대 6자리)
 * @property SendCodeClicked [인증번호 발송] 또는 [재발송] 버튼을 눌렀을 때 발생.
 * @property VerifyCodeClicked 인증하기 버튼을 눌렀을 때 발생.
 * @property ClearStatus 화면 진입/이탈 시 기존 입력 및 타이머 상태를 초기화할 때 발생.
 * @property ToastShown 실패 토스트/스낵바 노출이 완료되어 메시지를 비울 때 발생.
 */
internal sealed interface EmailUiEvent {
    data class EmailChanged(val email: String) : EmailUiEvent
    data class CodeChanged(val code: String) : EmailUiEvent
    object SendCodeClicked : EmailUiEvent
    object VerifyCodeClicked : EmailUiEvent
    object ClearStatus : EmailUiEvent
    object ToastShown : EmailUiEvent
}