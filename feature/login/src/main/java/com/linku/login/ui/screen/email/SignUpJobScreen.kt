package com.linku.login.ui.screen.email


import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Job
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.item.StepIndicator
import com.linku.design.util.scaler
import com.linku.login.viewmodel.SignUpViewModel

@Composable
fun SignUpJobScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current

    // 뷰모델 상태 확인.
    val selectedJobId = signUpViewModel.signUpForm.jobId
    val jobs = Job.getAllJobs()
    val isButtonEnabled = selectedJobId > 0

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorTheme.white)) {


        // 본문 (버튼과 겹치지 않게 하단 여유 48+24)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = (20.scaler),
                    end = (20.scaler),
                    top = (60.scaler),
                    bottom = (72.scaler) // 48 + 24
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )
            Spacer(Modifier.height((36.scaler)))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height((32.scaler)))

            jobs.forEach { job ->
                OptionButton(
                    text = job.displayName,
                    selected = selectedJobId == job.id,
                    onClick = {
                        signUpViewModel.onJobSelected(job.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height((12.scaler)))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 고정 버튼 (닉네임 화면과 동일)
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                navigator.navigate("sign_up_purpose") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}




//ui 확인용. 철저히 프리뷰용.
@Preview(showBackground = true, name = "직업 선택 - 프리뷰")
@Composable
fun SignUpJobScreenPreview() {
    val colorTheme = LocalColorTheme.current


    var selectedJobId by remember { mutableStateOf(3) } // 직장인 선택
    val jobs = Job.getAllJobs()

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = (20.scaler),
                    end = (20.scaler),
                    top = (52.scaler),
                    bottom = (72.scaler)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )

            Spacer(modifier = Modifier.height((32.scaler)))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(modifier = Modifier.height((40.scaler)))

            jobs.forEachIndexed { index, job ->
                OptionButton(
                    text = job.displayName,
                    selected = selectedJobId == job.id,
                    onClick = { selectedJobId = job.id },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height((12.scaler)))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "다음",
            enabled = selectedJobId > 0,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}