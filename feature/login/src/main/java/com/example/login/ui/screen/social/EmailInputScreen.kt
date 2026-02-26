package com.example.login.ui.screen.social

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.util.scaler
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.WrongRuleItem
import com.example.login.viewmodel.SignUpViewModel

/**
 * 소셜 로그인 후 이메일 입력 화면
 * - OTP 인증 없이 이메일 형식만 검증
 * - 형식이 맞으면 다음 단계로 진행
 */
@Composable
fun EmailInputScreen(
    navigator: NavHostController,
    parentEntry: NavBackStackEntry,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    BackHandler {
        parentEntry.savedStateHandle["from_email_input"] = true
        navigator.popBackStack()
    }

    var email by remember { mutableStateOf("") }

    val emailValid = remember(email) {
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    EmailInputScreenContent(
        email = email,
        onEmailChange = { email = it },
        emailValid = emailValid,
        onNextClick = {
            signUpViewModel.updateForm {
                it.copy(email = email.trim())
            }
            navigator.navigate("sign_up_password") // 또는 다음 화면 route
        },
        onBackClick = {
            parentEntry.savedStateHandle["from_email_input"] = true
            navigator.popBackStack()
        }
    )
}

@Composable
fun EmailInputScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailValid: Boolean,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colorTheme = LocalColorTheme.current

    // 이메일 에러 텍스트
    val emailErrorText: String? = when {
        email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "이메일 양식이 올바르지 않습니다!"
        else -> null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.scaler,
                    end = 20.scaler,
                    top = 60.scaler,
                    bottom = 72.scaler
                ),
            horizontalAlignment = Alignment.Start
        ) {
            // 1단계 인디케이터
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )

            Spacer(modifier = Modifier.height(36.scaler))

            // 타이틀
            Text(
                text = "이메일 주소를 입력해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy.font,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.scaler))

            // 서브 타이틀
            Text(
                text = "계정 복구 및 알림 수신에 사용됩니다",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Paperlogy.font,
                color = colorTheme.gray[500]!!
            )

            Spacer(modifier = Modifier.height(32.scaler))

            // 이메일 입력 필드
            LoginTextField(
                value = email,
                onValueChange = onEmailChange,
                hint = "이메일 주소를 입력해주세요",
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // 에러 문구
            emailErrorText?.let {
                Spacer(modifier = Modifier.height(10.scaler))
                WrongRuleItem(
                    text = it,
                    modifier = Modifier.padding(start = 12.scaler) // 20 + 12 = 32
                )
            }
        }

        // 하단 버튼
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BottomGradientButton(
                text = "다음",
                enabled = emailValid,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = onNextClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailInputScreenPreview_Empty() {
    EmailInputScreenContent(
        email = "",
        onEmailChange = {},
        emailValid = false,
        onNextClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmailInputScreenPreview_InvalidEmail() {
    EmailInputScreenContent(
        email = "linku",
        onEmailChange = {},
        emailValid = false,
        onNextClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmailInputScreenPreview_ValidEmail() {
    EmailInputScreenContent(
        email = "test@example.com",
        onEmailChange = {},
        emailValid = true,
        onNextClick = {},
        onBackClick = {}
    )
}