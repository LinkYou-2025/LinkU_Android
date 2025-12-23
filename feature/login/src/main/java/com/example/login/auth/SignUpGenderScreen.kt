package com.example.login.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.OptionButton

@Composable
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    // 성별 선택 상태: 1 = 남성, 2 = 여성
    var selectedGender by remember { mutableStateOf(signUpViewModel.gender) }

    //var selectedGender by remember { mutableStateOf<Int?>(null) }
    val isButtonEnabled = selectedGender != null

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ 닉네임 화면과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0
        val bottomGapWhenIme = 4.dp     // 키보드 보일 때
        val bottomGapDefault = 16.dp    // 키보드 없을 때(시작 지점)
        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 52.dp,   // ⬆️ 위쪽만 52
                bottom = 48.dp + 24.dp // ⬇️ 아래는 40 유지
            ),
            //.padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 상단 프로필 단계 표시
        StepIndicator(
            currentStep = 2,
            totalSteps = 3,
            label = "프로필 설정"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "성별을\n선택해주세요",
            fontSize = 22.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 선택 옵션: 남성
        OptionButton(
            text = "남성",
            selected = selectedGender == 1,
            onClick = {
                selectedGender = 1
                signUpViewModel.gender = 1
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 선택 옵션: 여성
        OptionButton(
            text = "여성",
            selected = selectedGender == 2,
            onClick = {
                selectedGender = 2
                signUpViewModel.gender = 2
            }
        )

        Spacer(modifier = Modifier.weight(1f))}

        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = {
                signUpViewModel.gender = selectedGender ?: 1
                navigator.navigate("sign_up_job") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
//shape = RoundedCornerShape(18.dp)


@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F6F9,
    name = "성별 선택 - 선택된 버튼만"
)
@Composable
fun SignUpGenderScreenPreview() {
    val fakeNavController = rememberNavController()
    SignUpGenderScreenPreviewOnly(navigator = fakeNavController)
}


//철저히 프리뷰용. ui 확인용.
@Composable
private fun SignUpGenderScreenPreviewOnly(navigator: NavHostController) {
    var selectedGender by remember { mutableStateOf<Int?>(2) } // 테스트용, "여성" 선택 상태
    val isButtonEnabled = selectedGender != null

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

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "성별을\n선택해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 남성 버튼
            OptionButton(
                text = "남성",
                selected = selectedGender == 1,
                onClick = { selectedGender = 1 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 여성 버튼
            OptionButton(
                text = "여성",
                selected = selectedGender == 2,
                onClick = { selectedGender = 2 }
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 버튼
        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = {}, // 프리뷰용: 동작 필요 없음
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}