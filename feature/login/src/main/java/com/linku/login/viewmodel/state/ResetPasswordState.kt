package com.linku.login.viewmodel.state

import com.linku.login.mvi.UiState

/**
 * 비밀번호 재설정 화면의 UI 상태
 *
 * @property email 사용자가 입력한 이메일 주소
 * @property isEmailValid 이메일 형식 유효성 여부 (버튼 활성화 조건)
 * @property isLoading API 요청 진행 중 여부 (버튼 비활성화 + 딤처리)
 * @property showSuccessDialog 메일 발송 성공 시 다이얼로그 표시 여부
 * @property error 서버 응답 실패 시 표시할 에러 메시지
 */
internal data class ResetPasswordState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val isLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val error: String? = null
) : UiState