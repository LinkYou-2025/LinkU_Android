package com.example.login.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.design.theme.font.Paperlogy
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.OptionButton
import com.example.design.util.rememberFigmaDimens
import com.example.login.viewmodel.SignUpViewModel
import com.example.design.theme.LocalColorTheme

@Composable
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()    //  Figma 412×917 기준 반응형
    val paperlogyFamily = Paperlogy.font

    // 성별 선택 상태: 1 = 남성, 2 = 여성
    var selectedGender by remember { mutableStateOf(signUpViewModel.gender) }

    //var selectedGender by remember { mutableStateOf<Int?>(null) }
    val isButtonEnabled = selectedGender != null


    Box(modifier = Modifier.fillMaxSize()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = w(20f),
                end = w(20f),
                top = h(60f),
                bottom = h(72f) // 48 + 24
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

        Spacer(Modifier.height(h(32f)))

        Text(
            text = "성별을\n선택해주세요",
            fontSize = 22.sp,
            fontFamily = paperlogyFamily,
            fontWeight = FontWeight.Bold,
            color = colorTheme.black,
            textAlign = TextAlign.Start
        )

        Spacer(Modifier.height(h(36f)))

        // 선택 옵션: 남성
        OptionButton(
            text = "남성",
            selected = selectedGender == 1,
            onClick = {
                selectedGender = 1
                signUpViewModel.gender = 1
            }
        )

        Spacer(Modifier.height(h(10f)))

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
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
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

@Preview(showBackground = true, name = "성별 선택 - 프리뷰")
@Composable
fun SignUpGenderScreenPreview() {
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font
    var selectedGender by remember { mutableStateOf<Int?>(2) } // 테스트용 여성 선택
    val isButtonEnabled = selectedGender != null

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = w(20f),
                    end = w(20f),
                    top = h(52f),
                    bottom = h(72f)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )

            Spacer(modifier = Modifier.height(h(36f)))

            Text(
                text = "성별을\n선택해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(modifier = Modifier.height(h(40f)))

            OptionButton(
                text = "남성",
                selected = selectedGender == 1,
                onClick = { selectedGender = 1 }
            )

            Spacer(modifier = Modifier.height(h(12f)))

            OptionButton(
                text = "여성",
                selected = selectedGender == 2,
                onClick = { selectedGender = 2 }
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}