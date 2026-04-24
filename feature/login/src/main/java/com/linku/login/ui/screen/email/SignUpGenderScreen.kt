package com.linku.login.ui.screen.email


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Gender
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel


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

    LinkuPreview {
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
}