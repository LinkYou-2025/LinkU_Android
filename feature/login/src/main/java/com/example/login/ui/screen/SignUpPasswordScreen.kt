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
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font


    BackHandler { navigator.popBackStack() }


    var password by remember { mutableStateOf(signUpViewModel.password) }
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

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = w(20f),
                    end = w(20f),
                    top = h(52f),
                    bottom = h(48f + 24f)
                ),
            horizontalAlignment = Alignment.Start
        ) {

            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )

            Spacer(Modifier.height(h(32f)))

            Text(
                text = "사용하실 비밀번호를\n 입력해주세요",
                fontSize = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(h(32f)))

            // 비밀번호 입력  : 눈 가리개 있는 것으로 교체
            PasswordLoginTextField(
                value = password,
                onValueChange = {
                    password = it
                    signUpViewModel.password = it
                },
                hint = "비밀번호를 입력해주세요."
            )

            Spacer(Modifier.height(h(10f)))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = w(12f)),
                verticalArrangement = Arrangement.spacedBy(h(8f))
            ) {
                PasswordRuleItem(
                    text = "영문, 숫자, 특수기호 조합",
                    satisfied = isPasswordComplex
                )
                PasswordRuleItem(
                    text = "8~20자",
                    satisfied = isPasswordLengthValid
                )
            }

            if (showConfirmField) {
                Spacer(Modifier.height(h(20f)))

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
                        fontFamily = paperlogyFamily,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(
                            start = w(8f),
                            top = h(4f)
                        )
                    )
                }
            }
        }

        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                signUpViewModel.password = password
                navigator.navigate("sign_up_nickname")
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun SignUpPasswordScreenContent(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    canProceed: Boolean,
    isPasswordComplex: Boolean,
    isPasswordLengthValid: Boolean,
    doPasswordsMatch: Boolean,
    bottomPadding: Dp,
    onNext: () -> Unit
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = w(20f),
                    end = w(20f),
                    top = h(52f),
                    bottom = h(72f)
                )
        ) {
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )

            Spacer(Modifier.height(h(36f)))

            Text(
                text = "사용하실 비밀번호를\n 입력해주세요",
                fontSize = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height(h(32f)))

            PasswordLoginTextField(
                value = password,
                onValueChange = onPasswordChange,
                hint = "비밀번호를 입력해주세요."
            )

            Spacer(Modifier.height(h(12f)))

            // 조건 표시(체크박스 활성화/비활성화)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = w(12f)),
                horizontalArrangement = Arrangement.spacedBy(w(24f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PasswordRuleItem(
                    text = "영문, 숫자, 특수기호 조합",
                    satisfied = isPasswordComplex
                )

                PasswordRuleItem(
                    text = "8~20자",
                    satisfied = isPasswordLengthValid
                )
            }

            if (password.length >= 8) {
                Spacer(Modifier.height(h(20f)))
                PasswordLoginTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    hint = "비밀번호를 확인해주세요."
                )

                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                    Text(
                        text = "비밀번호가 일치하지 않습니다.",
                        fontSize = 13.sp,
                        fontFamily = paperlogyFamily,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = w(8f), top = h(4f))
                    )
                }
            }
        }

        // 하단 버튼
        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = onNext,
            modifier = Modifier.align(Alignment.BottomCenter)
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
        canProceed = true,
        isPasswordComplex = true,
        isPasswordLengthValid = true,
        doPasswordsMatch = true,
        bottomPadding = 16.dp,
        onNext = {}
    )
}
