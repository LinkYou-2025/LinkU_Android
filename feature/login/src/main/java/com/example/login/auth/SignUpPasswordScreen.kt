package com.example.login.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.StepIndicator

@Composable
fun SignUpPasswordScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    BackHandler {
        navigator.popBackStack()
    }

    // 항상 이메일 인증으로 되돌리는 로직
    //TODO :여기서 되돌아가면, 기존 인증한 이메일 정보가 보이는게 맞는지? 다인언니한테 물어보기!
    //TODO : 이메일 정보로 돌아가면, 기존 입력한 이메일 상태로 나오는게 맞는지 아니면 다시 재입력? -> 이건 언니에게 물어보고 추후 수정하기!
    //TODO : 비밀번호의 경우, 눈 가리개? 로  입력을 보이거나, 안보이게 해야 하는건 아닌지 다인 언니에게 물어보기!
//    fun goBackToEmailVerification() {
//        val popped = navigator.popBackStack("email_verification", inclusive = false)
//        if (!popped) {
//            navigator.navigate("email_verification") {
//                launchSingleTop = true
//                // 이미 존재하면 그 지점까지 스택 정리 (없으면 no-op)
//                popUpTo("email_verification") {
//                    inclusive = false
//                    saveState = true
//                }
//            }
//        }
//    }
//
//    BackHandler(enabled = true) {
//        // 이메일 화면으로 돌아가기 전에 '리셋 신호' 전달
//        navigator.previousBackStackEntry
//            ?.savedStateHandle
//            ?.set("reset_email_screen", true)
//
//        val popped = navigator.popBackStack("email_verification", inclusive = false)
//        if (!popped) {
//            navigator.navigate("email_verification") {
//                launchSingleTop = true
//                popUpTo("email_verification") {
//                    inclusive = false
//                    saveState = true
//                }
//            }
//        }
//    }

    var password by remember { mutableStateOf(signUpViewModel.password) }
    var confirmPassword by remember { mutableStateOf("") }

    val isPasswordLengthValid = password.length in 8..20
    val isPasswordComplex =
        password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() }

    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
    val doPasswordsMatch = password == confirmPassword
    val showConfirmField = isPasswordValid
    val canProceed = isPasswordValid && doPasswordsMatch

    Box(modifier = Modifier.fillMaxSize()) {


        // ✅ 추가: 이메일 화면과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0

        val bottomGapWhenIme = 4.dp     // ← 키보드 보일 때 간격(더 붙이고 싶으면 0.dp)
        val bottomGapDefault = 16.dp    // ← 기존 코드의 42dp 유지
        val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val extraNavPadding = if (isImeVisible) 0.dp else navBottomDp
        val bottomPadding = (if (isImeVisible) bottomGapWhenIme else bottomGapDefault)

        // 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    // 버튼 영역만큼 여유 (48dp 높이 + 32dp 바텀 패딩 + 약간의 버퍼)
                    bottom = 48.dp + 24.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )
            Spacer(Modifier.height(32.dp))

            Text(
                text = "사용하실 비밀번호를\n 입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(32.dp))

            // 비밀번호 입력
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                LoginTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        signUpViewModel.password = it
                    },
                    hint = "비밀번호를 입력해주세요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth() //기쥰을 화면으로
                    .padding(start = 12.dp), // 전체 오른쪽 이동
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp)) // 체크박스 ↔ 텍스트
                    Text(
                        "영문, 숫자, 특수기호 조합",
                        fontFamily = Paperlogy,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(Modifier.width(24.dp)) // 조건 간 간격

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp)) // 체크박스 ↔ 텍스트
                    Text(
                        "8~20자",
                        fontFamily = Paperlogy,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            if (showConfirmField) {
                Spacer(Modifier.height(20.dp))
                LoginTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    hint = "비밀번호를 확인해주세요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                    Text(
                        text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // 하단 고정 버튼 (이메일 화면과 동일 위치)
        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
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
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    bottom = 48.dp + 24.dp
                )
        ) {
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = "사용하실 비밀번호를\n 입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            LoginTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                hint = "비밀번호를 확인해주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            Spacer(Modifier.height(12.dp))

            // 조건 표시
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                PasswordRule("영문, 숫자, 특수기호 조합", isPasswordComplex)
                Spacer(Modifier.width(12.dp))
                PasswordRule("8~20자", isPasswordLengthValid)
            }

            if (password.length >= 8) {
                Spacer(Modifier.height(20.dp))
                LoginTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    hint = "비밀번호를 입력해주세요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                    Text(
                        text = "비밀번호가 일치하지 않습니다.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // 하단 버튼
        BottomGradientButton(
            text = "다음",
            enabled = canProceed,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = onNext,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}

@Composable
private fun PasswordRule(text: String, satisfied: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    if (satisfied) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontFamily = Paperlogy, color = Color(0xFF757575))
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
