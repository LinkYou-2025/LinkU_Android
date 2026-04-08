package com.linku.login.ui.screen.email

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.linku.design.theme.font.Paperlogy
import com.linku.login.R
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.ResetPasswordTopHeader
import com.linku.login.viewmodel.ResetPasswordViewModel
import com.linku.design.theme.LocalColorTheme
import com.linku.design.util.scaler

// =======================
// 실제 Screen (ViewModel 사용)
// =======================
@Composable
fun ResetPasswordScreen(
    navigator: NavHostController,
    viewModel: ResetPasswordViewModel? = hiltViewModel()
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current


    // 🔑 Preview면 viewModel == null
    val ui = viewModel?.ui?.collectAsState()?.value

    var email by remember { mutableStateOf("test@email.com") }

    val isEmailValid =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {
        ResetPasswordTopHeader(
            onBack = {
                if (viewModel != null) {
                    navigator.navigate("email_login") {
                        popUpTo("resetPassword") { inclusive = true }
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (20.scaler)),
            horizontalAlignment = Alignment.Start
        ) {

            // 헤더 밀기
            Spacer(modifier = Modifier.height((59.scaler)))

            // 로고 위 여백 (38 / 917)
            Spacer(modifier = Modifier.height((38.scaler)))

            Image(
                painter = painterResource(id = R.drawable.ic_logo_color),
                contentDescription = null,
                modifier = Modifier
                    .width((56.scaler)) // 반응형 너비
                    .height((40.scaler)), // 반응형 높이
                contentScale = ContentScale.Fit

            )

            // 로고-제목 간격 (18 / 917)
            Spacer(modifier = Modifier.height((18.scaler)))

            Text(
                text = "비밀번호 재설정",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy.font,
                color = colorTheme.black
            )

            // 제목-설명 간격 (22 / 917)
            Spacer(modifier = Modifier.height((22.scaler)))

            Text(
                text = "링큐에 가입했던 이메일을 입력해주세요. \n비밀번호를 다시 설정할 수 있는 메일을 보내드릴게요.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight(400),
                color = colorTheme.gray[600]!!
            )

            // 설명-입력창 간격 (45 / 917)
            Spacer(modifier = Modifier.height((45.scaler)))

            LoginTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (ui?.error != null) viewModel?.consumeError()
                },
                hint = "이메일 주소를 입력해주세요"
            )

            if (ui?.error != null) {
                Spacer(Modifier.height((8f.scaler)))
                Text(
                    text = ui.error,
                    color = colorTheme.negative,
                    fontSize = 12.sp,
                    fontFamily = Paperlogy.font,
                    modifier = Modifier.padding(
                        start = (8.scaler)
                    )

                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "메일 보내기",
            enabled = isEmailValid && (ui?.loading != true),
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
//                viewModel?.request(email)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// =======================
// Preview (UI 확인 전용)
// =======================
@Preview(showBackground = true, name = "ResetPassword UI Preview")
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen(
        navigator = rememberNavController(),
        viewModel = null
    )
}


