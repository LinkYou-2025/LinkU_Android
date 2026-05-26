package com.linku.login.viewmodel.state

import com.linku.core.model.auth.SignUpForm

/**
 * SignUpViewModel이 UI 레이어로 내려보낼 최종 종합 UI 상태 가방
 */
internal data class SignUpUiState(
    val signUpForm: SignUpForm = SignUpForm(),

    val passwordStep: PasswordStepState = PasswordStepState(),
//    val nicknameStep: NicknameStepState = NicknameStepState()

    // val genderStep: GenderStepState = GenderStepState(),
)