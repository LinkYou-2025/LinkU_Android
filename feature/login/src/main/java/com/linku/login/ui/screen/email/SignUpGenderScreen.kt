package com.linku.login.ui.screen.email


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Gender
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.SignUpEffect


@Composable
internal fun SignUpGenderScreen(
    onBackClick: () -> Unit,
    onNavigateToJob: () -> Unit,
    signUpViewModel: SignUpViewModel
) {
    BackHandler { onBackClick() }

    val uiState by signUpViewModel.state.collectAsStateWithLifecycle()
    val selectedGender = uiState.signUpForm.gender
    val isButtonEnabled = selectedGender != Gender.NONE

    LaunchedEffect(signUpViewModel.sideEffect) {
        signUpViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToGender -> {
                }

                is SignUpEffect.NavigateToJob -> { // 성별 화면에서 완료 신호 수령
                    onNavigateToJob()
                }

                else -> { /* 다른 화면의 이펙트는 고스란히 패스 */
                }
            }
        }
    }

    SignUpStepLayout(
        currentStep = 2,
        title = "성별을\n선택해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            signUpViewModel.onGenderNextClicked()
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        OptionButton(
            text = "남성",
            selected = selectedGender == Gender.MALE,
            onClick = { signUpViewModel.onGenderChanged(Gender.MALE) }
        )

        Spacer(Modifier.height(10.scaler))

        OptionButton(
            text = "여성",
            selected = selectedGender == Gender.FEMALE,
            onClick = { signUpViewModel.onGenderChanged(Gender.FEMALE) }
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