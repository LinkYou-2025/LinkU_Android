package com.linku.login.ui.screen.social

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.linku.core.model.auth.NicknameCheckState
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.PasswordRuleItem
import com.linku.login.ui.item.StepIndicator
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.viewmodel.SocialAuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SocialNicknameScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {
    // 디자인 테마
    val colorTheme = LocalColorTheme.current

    // 🔹 SocialAuthViewModel 상태 수집
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val nicknameState by viewModel.nicknameCheckState.collectAsStateWithLifecycle()


    // 닉네임 유효성 (기존 로직 그대로)
    val isNicknameValid =
        nickname.isNotBlank() && nickname.length <= 6

    val isButtonEnabled =
        isNicknameValid && nicknameState == NicknameCheckState.Available

    Box(modifier = Modifier.fillMaxSize()) {

        /* =======================
         * 본문 영역
         * ======================= */
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

            StepIndicator(
                currentStep = 1,
                totalSteps = 4,
                label = "프로필 설정"
            )

            Spacer(Modifier.height(32.scaler))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height(40.scaler))

            LoginTextField(
                value = nickname,
                onValueChange = { input ->
                    // 소셜 뷰모델에 닉네임 전달
                    viewModel.updateNickname(input)

                },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.scaler))

            when (nicknameState) {
                is NicknameCheckState.Duplicated -> {
                    WrongRuleItem(
                        text = "이미 사용 중인 닉네임입니다.",
                        modifier = Modifier.padding(start = 12.scaler)
                    )
                }

                else -> {
                    PasswordRuleItem(
                        text = "국문/영문 6자 이하",
                        satisfied = isNicknameValid,
                        modifier = Modifier.padding(start = 12.scaler)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        /* =======================
         * 하단 버튼
         * ======================= */
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                navigator.navigate("social_gender") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
