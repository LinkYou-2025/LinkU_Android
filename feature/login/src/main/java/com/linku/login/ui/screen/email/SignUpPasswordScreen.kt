package com.linku.login.ui.screen.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.PasswordLoginTextField
import com.linku.login.ui.item.PasswordRuleItem
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel

@Composable
internal fun SignUpPasswordScreen(
    onBackClick: () -> Unit,
    onNavigateToNickname: () -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    BackHandler { onBackClick() }

    val colorTheme = MaterialTheme.linkuColors

    val passwordUiState by signUpViewModel.uiState.collectAsStateWithLifecycle()
    val passwordState = passwordUiState.passwordStep

    var confirmPassword by remember { mutableStateOf("") }

    // 화면 재진입시 리셋.
    LaunchedEffect(Unit) {
        confirmPassword = ""
        signUpViewModel.onPasswordChanged(password = "", confirmPassword = "")
    }

    LaunchedEffect(passwordState.isNavigateTrigger) {
        if (passwordState.isNavigateTrigger) {
            onNavigateToNickname()
            signUpViewModel.clearPasswordNavigationTrigger()
        }
    }

    SignUpStepLayout(
        currentStep = 1,
        title = "사용하실 비밀번호를\n입력해주세요",
        buttonEnabled = passwordState.canProceed,
        onNextClick = {
            signUpViewModel.onPasswordNextClicked()
        }
    ) {
        PasswordLoginTextField(
            value = passwordUiState.signUpForm.password,
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
                satisfied = passwordState.isComplex,
                modifier = Modifier.weight(1f)
            )
            PasswordRuleItem(
                text = "8~20자",
                satisfied = passwordState.isLengthValid,
                modifier = Modifier.weight(1f)
            )
        }

        if (passwordState.isPasswordValid) {
            Spacer(Modifier.height(20.scaler))

            PasswordLoginTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                hint = "비밀번호를 확인해주세요."
            )

            if (confirmPassword.isNotEmpty() && !passwordState.doPasswordsMatch) {
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