package com.linku.login.ui.screen.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.theme.font.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.linku.design.theme.LocalColorTheme
import com.linku.design.util.scaler
import com.linku.login.ui.item.PasswordRuleItem
import com.linku.login.ui.item.PasswordLoginTextField
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel
import androidx.compose.material3.MaterialTheme
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

@Composable
fun SignUpPasswordScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    val colorTheme = MaterialTheme.linkuColors
    BackHandler { navigator.popBackStack() }

    //뷰 모델에서 password 가져오기
    val password = signUpViewModel.signUpForm.password
    var confirmPassword by remember { mutableStateOf("") }

    val isPasswordLengthValid = password.length in 8..20
    val isPasswordComplex =
        password.any { it.isDigit() } &&
                password.any { it.isLetter() } &&
                password.any { !it.isLetterOrDigit() }

    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
    val doPasswordsMatch = password == confirmPassword
    val showConfirmField = isPasswordValid
    val canProceed = isPasswordValid && doPasswordsMatch

    SignUpStepLayout(
        currentStep = 1,
        totalSteps = 3,
        label = "계정 정보",
        title = "사용하실 비밀번호를\n입력해주세요",
        buttonEnabled = canProceed,
        onNextClick = {
            navigator.navigate("sign_up_nickname") { launchSingleTop = true }
        }
    ) {
        PasswordLoginTextField(
            value = password,
            onValueChange = { newPassword ->
                signUpViewModel.updateForm { form ->
                    form.copy(password = newPassword)
                }
            },
            hint = "비밀번호를 입력해주세요."
        )

        Spacer(Modifier.height(10.scaler))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.scaler),
            horizontalArrangement = Arrangement.spacedBy(8.scaler)
        ) {
            PasswordRuleItem(
                text = "영문, 숫자, 특수기호 조합",
                satisfied = isPasswordComplex,
                modifier = Modifier.weight(1f)
            )
            PasswordRuleItem(
                text = "8~20자",
                satisfied = isPasswordLengthValid,
                modifier = Modifier.weight(1f)
            )
        }

        if (isPasswordValid) {
            Spacer(Modifier.height(20.scaler))

            PasswordLoginTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                hint = "비밀번호를 확인해주세요."
            )

            if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                Text(
                    text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
                    fontSize = 13.sp,
                    color = colorTheme.negative,
                    modifier = Modifier.padding(start = 8.scaler, top = 4.scaler)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpPasswordScreenPreview() {
    LinkuPreview {
        SignUpStepLayoutPreview(
            currentStep = 1,
            totalSteps = 3,
            label = "계정 정보",
            title = "사용하실 비밀번호를\n입력해주세요",
            buttonEnabled = true,
        ) {
            PasswordLoginTextField(
                value = "Test@1234",
                onValueChange = {},
                hint = "비밀번호를 입력해주세요."
            )

            Spacer(Modifier.height(10.scaler))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.scaler)
            ) {
                PasswordRuleItem(
                    text = "영문, 숫자, 특수기호 조합",
                    satisfied = true,
                    modifier = Modifier.weight(1f)
                )
                PasswordRuleItem(text = "8~20자", satisfied = true, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.scaler))

            PasswordLoginTextField(
                value = "Test@1234",
                onValueChange = {},
                hint = "비밀번호를 확인해주세요."
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}