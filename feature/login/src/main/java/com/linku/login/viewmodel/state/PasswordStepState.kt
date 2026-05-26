package com.linku.login.viewmodel.state

/**
 * 회원가입 - 비밀번호 설정 단계의 UI 화면을 통제하는 독점 상태 가방입니다.
 *
 * @property isLengthValid 최소/최대 길이 규칙(8자~20자) 만족 여부. (가이드라인 불 켜기용)
 * @property isComplex 영문, 숫자, 특수기호 조합 복잡도 조건 만족 여부. (가이드라인 불 켜기용)
 * @property doPasswordsMatch 기본 비밀번호와 비밀번호 확인창의 입력값 일치 여부. (불일치 경고 노출용)
 * @property isPasswordValid 길이와 복잡도 조건을 모두 통과했는지 여부. (true일 때 확인창 슬롯 노출)
 * @property canProceed 모든 조건을 만족하여 다음 단계로 진행 가능한지 여부. (버튼 활성화 제어)
 * @property isNavigateTrigger 다음 가입 단계(닉네임 설정)로 이동하기 위한 일회성 화면 전환 트리거.
 */

internal data class PasswordStepState(
    val isLengthValid: Boolean = false,
    val isComplex: Boolean = false,
    val doPasswordsMatch: Boolean = false,
    val isPasswordValid: Boolean = false,
    val canProceed: Boolean = false,
    val isNavigateTrigger: Boolean = false
)