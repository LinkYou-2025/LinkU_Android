package com.example.login.auth


import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.OptionButton
import com.example.login.ui.item.StepIndicator

@Composable
fun SignUpJobScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    var selectedJobIndex by remember { mutableStateOf(
        if (signUpViewModel.jobId > 0) signUpViewModel.jobId - 1 else null
    ) }
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    val isButtonEnabled = selectedJobIndex != null

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        // ✅ 닉네임/성별과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0
        val bottomGapWhenIme = 4.dp
        val bottomGapDefault = 16.dp
        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

        // 본문 (버튼과 겹치지 않게 하단 여유 48+24)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    bottom = 48.dp + 24.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 고정 버튼 (닉네임 화면과 동일)
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
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


@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F6F9,
    name = "직업 선택 전체 화면"
)
@Composable
fun SignUpJobScreenPreview() {
    val fakeNavController = rememberNavController()
    SignUpJobScreenPreviewOnly(navigator = fakeNavController)
}


//ui 확인용. 철저히 프리뷰용.
@Composable
private fun SignUpJobScreenPreviewOnly(navigator: NavHostController) {
    var selectedJobIndex by remember { mutableStateOf(2) } // "직장인" 선택 예시
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    val isButtonEnabled = selectedJobIndex != null

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 72.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            jobs.forEachIndexed { index, job ->
                OptionButton(
                    text = job,
                    selected = selectedJobIndex == index,
                    onClick = { selectedJobIndex = index },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        //하단 버튼
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = {}, // 프리뷰에서는 동작 필요 없음
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

