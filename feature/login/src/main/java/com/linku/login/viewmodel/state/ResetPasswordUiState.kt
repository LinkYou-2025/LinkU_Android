package com.linku.login.viewmodel.state

/**
 * 비밀번호 재설정 화면의 UI 상태를 나타냅니다.
 *
 * @property email 사용자가 입력한 이메일 주소.
 * @property status 비밀번호 재설정 프로세스의 현재 작업 상태.
 * UI 전환, 로딩 인디케이터, 에러 피드백 관리에 사용됩니다.
 */
data class ResetPasswordUiState(
    val email: String = "",
    val status: ResetPasswordStatus = ResetPasswordStatus.Idle,
)

sealed class ResetPasswordStatus {
    object Idle : ResetPasswordStatus()
    object Loading : ResetPasswordStatus()
    object Success : ResetPasswordStatus()
    sealed class Fail : ResetPasswordStatus() {
        object NotRegistered : Fail()               // 가입되지 않은 이메일
        object SocialAccount : Fail()               // SNS 로그인 계정
        data class Unknown(val message: String) : Fail()
    }
}

/**
 * 비밀번호 재설정 화면에서 사용자의 액션에 의해 발생할 수 있는 UI 이벤트 명세서입니다.
 *
 * @property EmailChanged 이메일 입력창의 텍스트가 변경될 때 발생.
 * @property SendResetEmailClicked [메일 보내기] 버튼을 눌렀을 때 발생.
 * @property ConsumeStatus 에러/성공 상태 소비 완료 후 Idle로 되돌릴 때 발생.
 */
sealed class ResetPasswordUiEvent {
    data class EmailChanged(val email: String) : ResetPasswordUiEvent()
    object SendResetEmailClicked : ResetPasswordUiEvent()
    object ConsumeStatus : ResetPasswordUiEvent()   // 에러 토스트 등 소비 후 Idle로
}