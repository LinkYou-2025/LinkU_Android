package com.example.login.ui.screen.email


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.core.model.auth.Gender
import com.example.login.ui.item.OptionButton
import com.example.login.viewmodel.SignUpViewModel
import com.example.design.util.scaler
import com.example.login.ui.layout.SignUpStepLayout
import com.example.login.ui.layout.SignUpStepLayoutPreview


@Composable
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    // 뷰모델 상태 직접 가져오기.
    val selectedGender = signUpViewModel.signUpForm.gender
    val isButtonEnabled = selectedGender != Gender.NONE


    SignUpStepLayout(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
        title = "성별을\n선택해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            navigator.navigate("sign_up_job") { launchSingleTop = true }
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        OptionButton(
            text = "남성",
            selected = selectedGender == Gender.MALE,
            onClick = { signUpViewModel.updateForm { it.copy(gender = Gender.MALE) } }
        )

        Spacer(Modifier.height(10.scaler))

        OptionButton(
            text = "여성",
            selected = selectedGender == Gender.FEMALE,
            onClick = { signUpViewModel.updateForm { it.copy(gender = Gender.FEMALE) } }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "성별 선택 - 프리뷰")
@Composable
fun SignUpGenderScreenPreview() {
    var selectedGender by remember { mutableStateOf(Gender.FEMALE) }
    val isButtonEnabled = selectedGender != Gender.NONE

    SignUpStepLayoutPreview(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
        title = "성별을\n선택해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {}
    ) {
        Spacer(Modifier.height(4.scaler))

        OptionButton(
            text = "남성",
            selected = selectedGender == Gender.MALE,
            onClick = { selectedGender = Gender.MALE }
        )

        Spacer(Modifier.height(10.scaler))

        OptionButton(
            text = "여성",
            selected = selectedGender == Gender.FEMALE,
            onClick = { selectedGender = Gender.FEMALE }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}