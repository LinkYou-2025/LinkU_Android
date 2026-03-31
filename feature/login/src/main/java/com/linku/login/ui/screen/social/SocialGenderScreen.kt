package com.linku.login.ui.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Gender
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.item.StepIndicator
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialGenderScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {
    // 디자인 테마
    val colorTheme = LocalColorTheme.current

    // SocialAuthViewModel 상태
    val selectedGender by viewModel.gender.collectAsStateWithLifecycle()
    val isButtonEnabled = selectedGender != Gender.NONE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white)
    ) {

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
                currentStep = 2,
                totalSteps = 6,
                label = "프로필 설정"
            )

            Spacer(Modifier.height(32.scaler))

            Text(
                text = "성별을\n선택해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(36.scaler))

            OptionButton(
                text = "남성",
                selected = selectedGender == Gender.MALE,
                onClick = {
                    viewModel.updateGender(Gender.MALE)
                }
            )

            Spacer(Modifier.height(10.scaler))

            OptionButton(
                text = "여성",
                selected = selectedGender == Gender.FEMALE,
                onClick = {
                    viewModel.updateGender(Gender.FEMALE)
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                navigator.navigate("social_job") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}