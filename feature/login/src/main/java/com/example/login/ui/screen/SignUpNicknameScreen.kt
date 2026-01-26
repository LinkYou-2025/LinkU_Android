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
import com.example.design.util.scaler
import com.example.login.viewmodel.SignUpViewModel
import com.example.login.viewmodel.NicknameCheckState

@Composable
fun SignUpNicknameScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //디자인 모듈
    val colorTheme = LocalColorTheme.current


    // 뷰모델의 상태 확인.
    val nickname = signUpViewModel.signUpForm.nickname //form 상태를 읽음.
    val nicknameState by signUpViewModel.nicknameState.collectAsState()
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6 //국문/영문 닉네임 글자수 6글자 이하로 제안

    //  버튼 활성 조건 (EmailVerificationScreen의 isButtonEnabled와 동일한 느낌)
    val isButtonEnabled = isNicknameValid && nicknameState == NicknameCheckState.Available

    Box(modifier = Modifier.fillMaxSize()) {

        // 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = (20.scaler),
                    end = (20.scaler),
                    top = (60.scaler),
                    bottom = (72.scaler)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )
            Spacer(Modifier.height((32.scaler)))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height((40.scaler)))

            LoginTextField(
                value = nickname,
                onValueChange = { input ->
                    // 뷰모델의 함수가 내부적으로 signUpForm을 업데이트하고 중복체크를 실행함
                    signUpViewModel.onNicknameChanged(input)
                },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height((10.scaler)))

            PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = (12.scaler))
            )

            when (nicknameState) {
                  // 혹시 닉네임 유효성 검사 중 닉네임 확인 중이 필요하다면...
//                is NicknameCheckState.Checking -> {
//                    Spacer(Modifier.height((6.scaler)))
//                    Text(
//                        text = "닉네임 확인 중...",
//                        fontSize = 13.sp,
//                        lineHeight = 15.sp,
//                        fontWeight = FontWeight(400),
//                        fontFamily = Paperlogy.font,
//                        color = colorTheme.black.copy(alpha = 0.5f)
//                    )
//                }


                is NicknameCheckState.Duplicated -> {
                    Spacer(Modifier.height((12.scaler)))
                    Text(
                        text = "이미 사용 중인 닉네임입니다.",
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = Paperlogy.font,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = (12.scaler))

                    )
                }


                is NicknameCheckState.Error -> {
                    Spacer(Modifier.height((12.scaler)))
                    Text(
                        text = (nicknameState as NicknameCheckState.Error).message, //뷰모델 에러 메시지 사용.
                        //text = "서버 요청에 실패했습니다.",
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = Paperlogy.font,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = (12.scaler))
                    )
                }

                else -> Unit
            }




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


    var nickname by remember { mutableStateOf("LinkU") }
    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 6

    Box(modifier = Modifier.fillMaxSize().background(colorTheme.white)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = (20.scaler), end = (20.scaler), top = (52.scaler), bottom = (72.scaler)),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                label = "프로필 설정"
            )

            Spacer(Modifier.height((32.scaler)))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )

            Spacer(Modifier.height((32.scaler)))

            LoginTextField(
                value = nickname,
                onValueChange = { nickname = it },
                hint = "닉네임을 입력해주세요.",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height((15.scaler)))

            PasswordRuleItem(
                text = "국문/영문 6자 이하",
                satisfied = isNicknameValid,
                modifier = Modifier.padding(start = (12.scaler))
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