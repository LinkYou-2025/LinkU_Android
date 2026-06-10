package com.linku.login.viewmodel.state

import com.linku.core.model.auth.NicknameCheckState

/**
 * 회원가입 - 닉네임 설정 단계의 UI 독점 상태 가방
 *
 * @property nicknameCheckState 서버에서 검증한 닉네임 상태 (Idle, Checking, Available, Duplicated 등)
 * @property isNicknameValid 국문/영문 6자 이하 규칙 통과 여부 (가이드라인 불 켜기용)
 */
internal data class NicknameStepState(
    val nicknameCheckState: NicknameCheckState = NicknameCheckState.Idle,
    val isNicknameValid: Boolean = false,
)