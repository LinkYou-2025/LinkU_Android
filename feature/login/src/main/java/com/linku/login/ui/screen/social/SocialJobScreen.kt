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
import com.linku.core.model.auth.Job
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.item.StepIndicator
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialJobScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {
    // 디자인 테마
    val colorTheme = LocalColorTheme.current

    // SocialAuthViewModel 상태
    val selectedJob by viewModel.job.collectAsStateWithLifecycle()
    val jobs = Job.getAllJobs()

    val isButtonEnabled = selectedJob != Job.NONE

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
                currentStep = 3,
                totalSteps = 6,
                label = "프로필 설정"
            )

            Spacer(Modifier.height(36.scaler))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(32.scaler))

            jobs.forEach { job ->
                OptionButton(
                    text = job.displayName,
                    selected = selectedJob == job,
                    onClick = {
                        viewModel.updateJob(job)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.scaler))
            }

            Spacer(modifier = Modifier.weight(1f))
        }


        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                navigator.navigate("social_purpose") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
