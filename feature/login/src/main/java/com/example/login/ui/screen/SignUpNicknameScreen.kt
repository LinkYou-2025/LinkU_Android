package com.example.login.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.font.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import com.example.design.theme.LocalColorTheme
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.PasswordRuleItem
import com.example.login.ui.item.StepIndicator
import com.example.design.util.rememberFigmaDimens
import com.example.login.viewmodel.SignUpViewModel

@Composable
fun SignUpNicknameScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font

    var nickname by remember { mutableStateOf(signUpViewModel.nickname) }

    val isNicknameAvailable by signUpViewModel.isNicknameAvailable.collectAsState()
    val nicknameMessage by signUpViewModel.nicknameMessage.collectAsState()
    val isLoading by signUpViewModel.isLoading.collectAsState()


    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6 //국문/영문 닉네임 글자수 6글자 이하로 제안

    //  버튼 활성 조건 (EmailVerificationScreen의 isButtonEnabled와 동일한 느낌)
    val isButtonEnabled = isNicknameValid &&
            (isNicknameAvailable == true) && // 중복확인 완료만 허용
            !isLoading

    Box(modifier = Modifier.fillMaxSize()) {

        // 본문
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
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )
            Spacer(Modifier.height(h(32f)))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height(h(32f)))

            //입력값 기준으로 즉시 판단, 삭제 시 불필요한 호출을 방지하도록 수정함.
            LoginTextField(
                value = nickname,
                onValueChange = { input ->
                    nickname = input
                    signUpViewModel.nickname = input

                    // 입력 변경시 이전 결과 초기화
                    signUpViewModel.resetNicknameAvailability()

                    val isValid =
                        input.isNotBlank() && input.length <= 6

                    if (isValid) {
                        signUpViewModel.checkNickname()
                    }
                },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            if (isNicknameAvailable == false) {
                Spacer(Modifier.height(h(6f)))
                Text(
                    "중복된 닉네임 입니다.",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    fontFamily = paperlogyFamily,
                    color = Color(0xFFFF5E5E)
                )
            }
            if (nicknameMessage == "서버 요청 실패") {
                Spacer(Modifier.height(h(6f)))
                Text(
                    "서버 요청 실패",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = paperlogyFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFF5E5E)
                )
            }

            Spacer(Modifier.height(h(12f)))

            PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = w(32f))
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 고정 버튼 (EmailVerificationScreen과 동일한 방식)
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                navigator.navigate("sign_up_gender") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpNicknameScreenPreview() {
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font

    var nickname by remember { mutableStateOf("LinkU") }
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = w(20f), end = w(20f), top = h(52f), bottom = h(72f)),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )

            Spacer(Modifier.height(h(32f)))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height(h(32f)))

            LoginTextField(
                value = nickname,
                onValueChange = { nickname = it },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(h(15f)))

            PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = w(12f))
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "다음",
            enabled = isNicknameValid,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}