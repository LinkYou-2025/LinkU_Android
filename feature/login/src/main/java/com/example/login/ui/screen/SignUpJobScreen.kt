package com.example.login.ui.screen


import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.font.Paperlogy
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.OptionButton
import com.example.login.ui.item.StepIndicator
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler
import com.example.login.viewmodel.SignUpViewModel

@Composable
fun SignUpJobScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current


    var selectedJobIndex by remember { mutableStateOf(
        if (signUpViewModel.jobId > 0) signUpViewModel.jobId - 1 else null
    ) }
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    val isButtonEnabled = selectedJobIndex != null

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

            jobs.forEachIndexed { index, job ->
                OptionButton(
                    text = job,
                    selected = selectedJobIndex == index,
                    onClick = {
                        selectedJobIndex = index
                        signUpViewModel.jobId = index + 1
                    },
                    modifier = Modifier.fillMaxWidth() // 반응형 유지
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
                signUpViewModel.jobId = (selectedJobIndex ?: 0) + 1
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


    var selectedJobIndex by remember { mutableStateOf<Int?>(2) }
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")

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
                    text = job,
                    selected = selectedJobIndex == index,
                    onClick = { selectedJobIndex = index },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height((12.scaler)))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "다음",
            enabled = selectedJobIndex != null,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}