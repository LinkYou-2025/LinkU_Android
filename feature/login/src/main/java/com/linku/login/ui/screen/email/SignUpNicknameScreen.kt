package com.linku.login.ui.screen.email


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.*
import com.linku.core.model.auth.NicknameCheckState
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.PasswordRuleItem
import com.linku.design.util.scaler
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel


@Composable
fun SignUpNicknameScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    // 뷰모델의 상태 확인.
    val nickname = signUpViewModel.signUpForm.nickname //form 상태를 읽음.
    val nicknameState by signUpViewModel.nicknameState.collectAsState()
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6 && nickname.matches(Regex("^[가-힣a-zA-Z]+$"))  // 국문/영문만 허용

    //  버튼 활성 조건 (EmailVerificationScreen의 isButtonEnabled와 동일한 느낌)
    val isButtonEnabled = isNicknameValid && nicknameState == NicknameCheckState.Available

    SignUpStepLayout(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
        title = "사용하실 닉네임을\n입력해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            navigator.navigate("sign_up_gender") { launchSingleTop = true }
        }
    ) {
        Spacer(Modifier.height(8.scaler)) // layout 내부 32 + 여기 8 = 기존 40과 동일

        LoginTextField(
            value = nickname,
            onValueChange = { signUpViewModel.onNicknameChanged(it) },
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

@Preview(showBackground = true)
@Composable
fun SignUpNicknameScreenPreview() {
    var nickname by remember { mutableStateOf("LinkU") }
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6

    SignUpStepLayoutPreview(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
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