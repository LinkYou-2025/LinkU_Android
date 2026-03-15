package com.example.login.ui.screen.social


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.core.model.auth.NicknameCheckState
import com.example.design.theme.LocalColorTheme
import com.example.design.util.scaler
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.PasswordRuleItem
import com.example.login.ui.item.WrongRuleItem
import com.example.login.viewmodel.SocialAuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.login.ui.layout.SignUpStepLayout
import com.example.login.ui.layout.SignUpStepLayoutPreview

@Composable
fun SocialNicknameScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {

    // 🔹 SocialAuthViewModel 상태 수집
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val nicknameState by viewModel.nicknameCheckState.collectAsStateWithLifecycle()


    // 닉네임 유효성 (기존 로직 그대로)
    val isNicknameValid =
        nickname.isNotBlank() && nickname.length <= 6 && nickname.matches(Regex("^[가-힣a-zA-Z]+$"))  // 국문/영문만 허용

    val isButtonEnabled =
        isNicknameValid && nicknameState == NicknameCheckState.Available

    SignUpStepLayout(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
        title = "사용하실 닉네임을\n입력해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            navigator.navigate("social_gender") { launchSingleTop = true }
        }
    ) {
        Spacer(Modifier.height(8.scaler)) // layout 내부 32 + 여기 8 = 기존 40과 동일

        LoginTextField(
            value = nickname,
            onValueChange = { viewModel.updateNickname(it) },
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

    SignUpStepLayoutPreview(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
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