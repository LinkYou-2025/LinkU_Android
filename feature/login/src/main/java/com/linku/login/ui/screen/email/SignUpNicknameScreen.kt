package com.linku.login.ui.screen.email


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.NicknameCheckState
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.PasswordRuleItem
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.SignUpEffect


@Composable
internal fun SignUpNicknameScreen(
    onBackClick: () -> Unit,
    onNavigateToGender: () -> Unit,
    signUpViewModel: SignUpViewModel
) {
    BackHandler { onBackClick() }

    val signUpState by signUpViewModel.state.collectAsStateWithLifecycle()
    val nicknameForm = signUpState.signUpForm.nickname
    val nicknameState = signUpState.nicknameStep

    val isButtonEnabled = nicknameState.isNicknameValid &&
            nicknameState.nicknameCheckState == NicknameCheckState.Available

    LaunchedEffect(signUpViewModel.sideEffect) {
        signUpViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToGender -> {
                    onNavigateToGender()
                }

                else -> { /* 딱히 할게 없네용 */
                }
            }
        }
    }

    SignUpStepLayout(
        currentStep = 2,
        title = "사용하실 닉네임을\n입력해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            signUpViewModel.onNicknameNextClicked()
        }
    ) {
        Spacer(Modifier.height(8.scaler)) // layout 내부 32 + 여기 8 = 기존 40과 동일

        LoginTextField(
            value = nicknameForm,
            onValueChange = { signUpViewModel.onNicknameChanged(it) },
            hint = "닉네임을 입력해주세요.",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.scaler))

        when (val checkState = nicknameState.nicknameCheckState) {
            is NicknameCheckState.Duplicated -> WrongRuleItem(
                text = "이미 사용 중인 닉네임입니다.",
                modifier = Modifier.padding(start = 12.scaler)
            )
            is NicknameCheckState.Error -> WrongRuleItem(
                text = checkState.message,
                modifier = Modifier.padding(start = 12.scaler)
            )
            else -> PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = nicknameState.isNicknameValid,
                modifier = Modifier.padding(start = 12.scaler)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpNicknameScreenPreview() {
    var nickname by remember { mutableStateOf("LinkU") }
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6

    LinkuPreview {
        SignUpStepLayoutPreview(
            currentStep = 2,
            title = "사용하실 닉네임을\n입력해주세요",
            buttonEnabled = isNicknameValid,
            onNextClick = {}
        ) {
            Spacer(Modifier.height(8.scaler))

            LoginTextField(
                value = nickname,
                onValueChange = { nickname = it },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.scaler))

            PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = 12.scaler)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}