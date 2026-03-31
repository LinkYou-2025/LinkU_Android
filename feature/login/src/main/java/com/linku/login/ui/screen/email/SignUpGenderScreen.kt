package com.linku.login.ui.screen.email


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Gender
import com.linku.design.theme.font.Paperlogy
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.StepIndicator
import com.linku.login.ui.item.OptionButton
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.design.theme.LocalColorTheme
import com.linku.design.util.scaler



@Composable
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈 불러오기.
    val colorTheme = LocalColorTheme.current


    // 뷰모델 상태 직접 가져오기.
    val selectedGender = signUpViewModel.signUpForm.gender
    val isButtonEnabled = selectedGender != Gender.NONE


    Box(modifier = Modifier.fillMaxSize()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = (20.scaler),
                end = (20.scaler),
                top = (60.scaler),
                bottom = (72.scaler) // 48 + 24
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

        Spacer(Modifier.height((32.scaler)))

        Text(
            text = "성별을\n선택해주세요",
            fontSize = 22.sp,
            fontFamily = Paperlogy.font,
            fontWeight = FontWeight.Bold,
            color = colorTheme.black,
            textAlign = TextAlign.Start
        )

        Spacer(Modifier.height((36.scaler)))

        // 선택 옵션: 남성
        OptionButton(
            text = "남성",
            selected = selectedGender == Gender.MALE, //1
            onClick = {
                signUpViewModel.updateForm { it.copy(gender = Gender.MALE) }
            }
        )

        Spacer(Modifier.height((10.scaler)))

        // 선택 옵션: 여성
        OptionButton(
            text = "여성",
            selected = selectedGender == Gender.FEMALE, //2
            onClick = {
                signUpViewModel.updateForm { it.copy(gender = Gender.FEMALE )}
            }
        )

        Spacer(modifier = Modifier.weight(1f))}

        BottomGradientButton(
            text = "다음",
            enabled = isButtonEnabled,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                // 이미 뷰모델에 데이터가 들어있으므로 바로 이동
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
    var selectedGender by remember { mutableStateOf(Gender.FEMALE) } // 테스트용 여성 선택
    val isButtonEnabled = selectedGender != Gender.NONE  //버튼 활성화 조건입니다.

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

            Spacer(modifier = Modifier.height((36.scaler)))

            Text(
                text = "성별을\n선택해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(modifier = Modifier.height((40.scaler)))

            OptionButton(
                text = "남성",
                selected = selectedGender == Gender.MALE,
                onClick = { selectedGender = Gender.MALE}
            )

            Spacer(modifier = Modifier.height((12.scaler)))

            OptionButton(
                text = "여성",
                selected = selectedGender == Gender.FEMALE,
                onClick = { selectedGender = Gender.FEMALE }
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