package com.example.login.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.font.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.Dp
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.PasswordRuleItem
import com.example.login.ui.item.PasswordLoginTextField
import com.example.login.viewmodel.SignUpViewModel


@Composable
fun SignUpPasswordScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    //디자인 모듈
    val colorTheme = LocalColorTheme.current

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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white)
            .padding(
                start = (20.scaler),
                end = (20.scaler),
                top = (60.scaler),
                bottom = (72.scaler)
            ),
        horizontalAlignment = Alignment.Start
    ) {

        StepIndicator(
            currentStep = 1,
            totalSteps = 3,
            label = "계정 정보"
        )

        Spacer(Modifier.height((32.scaler)))

        Text(
            text = "사용하실 비밀번호를\n입력해주세요",
            fontSize = 22.sp,
            fontFamily = Paperlogy.font,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height((32.scaler)))

        // 비밀번호 입력  : 눈 가리개 있는 것으로 교체
        PasswordLoginTextField(
            value = password,
            onValueChange = { newPassword ->
                signUpViewModel.updateForm { it.copy(password = newPassword) }
            },
            hint = "비밀번호를 입력해주세요."
        )

        Spacer(Modifier.height((10.scaler)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (12.scaler)),
            horizontalArrangement = Arrangement.spacedBy((8.scaler))
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

        if (showConfirmField) {
            Spacer(Modifier.height((20.scaler)))

            // 비밀번호 확인 눈 가리개 있는 거로 교체
            PasswordLoginTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                hint = "비밀번호를 확인해주세요."
            )

            if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                Text(
                    text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
                    fontSize = 13.sp,
                    fontFamily = Paperlogy.font,
                    color = Color(0xFFFF5E5E),
                    modifier = Modifier.padding(
                        start = (8.scaler),
                        top = (4.scaler)
                    )
                )
            }
        }
        Spacer(Modifier.weight(1f))

        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                // 클릭 시점에 최종 확정 저장 지금 굳이 onValueChange에서 계속 저장하고 있기에 필요X.
                //signUpViewModel.updateForm { it.copy(password = password) }
                navigator.navigate("sign_up_nickname") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

}


@Composable
fun SignUpPasswordScreenContent(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    showConfirmField: Boolean,
    canProceed: Boolean,
    isPasswordComplex: Boolean,
    isPasswordLengthValid: Boolean,
    doPasswordsMatch: Boolean,
    onNext: () -> Unit
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white)
            .padding(
                start = (20.scaler),
                end = (20.scaler),
                top = (60.scaler),
                bottom = (72.scaler)
            )
    ) {
        StepIndicator(
            currentStep = 1,
            totalSteps = 3,
            label = "계정 정보"
        )

        Spacer(Modifier.height((36.scaler)))

        Text(
            text = "사용하실 비밀번호를\n입력해주세요",
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontFamily = Paperlogy.font,
            fontWeight = FontWeight.Bold,
            color = colorTheme.black
        )

        Spacer(Modifier.height((32.scaler)))

        PasswordLoginTextField(
            value = password,
            onValueChange = onPasswordChange,
            hint = "비밀번호를 입력해주세요."
        )

        Spacer(Modifier.height((12.scaler)))

        // 조건 표시(체크박스 활성화/비활성화)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (12.scaler)),
            horizontalArrangement = Arrangement.spacedBy((8.scaler)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PasswordRuleItem(
                text = "영문, 특수기호 조합",
                satisfied = isPasswordComplex,
                modifier = Modifier.weight(1f)
            )
            PasswordRuleItem(
                text = "8~20자",
                satisfied = isPasswordLengthValid,
                modifier = Modifier.weight(1f)
            )
        }


        if (showConfirmField) {
            Spacer(Modifier.height((20.scaler)))
            PasswordLoginTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                hint = "비밀번호를 확인해주세요."
            )

            if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                Text(
                    text = "비밀번호가 일치하지 않습니다.",
                    fontSize = 13.sp,
                    fontFamily = Paperlogy.font,
                    color = Color(0xFFFF5E5E),
                    modifier = Modifier.padding(start = (8.scaler), top = (4.scaler))
                )
            }
        }

        Spacer(Modifier.weight(1f))
        // 하단 버튼
        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )

    }
}





@Preview(showBackground = true)
@Composable
fun SignUpPasswordScreenContentPreview() {
    SignUpPasswordScreenContent(
        password = "Test@1234",
        confirmPassword = "Test@1234",
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        showConfirmField = true,
        canProceed = true,
        isPasswordComplex = true,
        isPasswordLengthValid = true,
        doPasswordsMatch = true,
        onNext = {}
    )
}
