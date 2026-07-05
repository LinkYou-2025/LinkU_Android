package com.linku.login.ui.screen.social


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialNicknameScreen(
    onBackClick: () -> Unit,
    onNavigateToGender: () -> Unit,
    viewModel: SocialAuthViewModel
) {

    BackHandler { onBackClick() }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val nicknameForm = uiState.socialLoginForm.nickname
    val nicknameState = uiState.nicknameCheckState

    val isNicknameValid = nicknameForm.isNotBlank() &&
            nicknameForm.length in 1..6 &&
            nicknameForm.matches(Regex("^[가-힣a-zA-Z]+$"))

    val isButtonEnabled = isNicknameValid && nicknameState == NicknameCheckState.Available

    SignUpStepLayout(
        currentStep = 2,
        title = "사용하실 닉네임을\n입력해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            onNavigateToGender()
        }
    ) {
        Spacer(Modifier.height(8.scaler)) // layout 내부 32 + 여기 8 = 기존 40과 동일

        LoginTextField(
            value = nicknameForm,
            onValueChange = { viewModel.onNicknameChanged(it) },
            hint = "닉네임을 입력해주세요.",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.scaler))

        when (nicknameState) {
            is NicknameCheckState.Duplicated -> WrongRuleItem(
                text = "이미 사용 중인 닉네임입니다.",
                modifier = Modifier.padding(start = 12.scaler)
            )

            else -> PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = 12.scaler)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// 중복 닉네임 상태 프리뷰
@Preview(showBackground = true, name = "소셜 닉네임 - 중복")
@Composable
fun SocialNicknameScreenDuplicatedPreview() {
    val nickname = "LinkU"

    LinkuPreview {
        SignUpStepLayoutPreview(
            currentStep = 2,
            title = "사용하실 닉네임을\n입력해주세요",
            buttonEnabled = false,
            onNextClick = {}
        ) {
            Spacer(Modifier.height(8.scaler))

            LoginTextField(
                value = nickname,
                onValueChange = {},
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.scaler))

            WrongRuleItem(
                text = "이미 사용 중인 닉네임입니다.",
                modifier = Modifier.padding(start = 12.scaler)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}