package com.linku.login.ui.screen.social

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
import com.linku.login.viewmodel.SocialAuthViewModel
import com.linku.login.viewmodel.state.SocialAuthUiEffect

@Composable
fun SocialGenderScreen(
    onBackClick: () -> Unit,
    onNavigateToJob: () -> Unit,
    viewModel: SocialAuthViewModel
) {

    BackHandler { onBackClick() }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val selectedGender = uiState.socialLoginForm.gender
    val isButtonEnabled = selectedGender != Gender.NONE

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SocialAuthUiEffect.NavigateToAdditionalInfo -> {
                    // 성별 입력 이후, 공통 분기에 따라 다음 댑스로 이동하는 트리거 확정 수령
                    onNavigateToJob()
                }

                else -> { /* 다른 화면 관할 이펙트는 패스 */
                }
            }
        }
    }

    SignUpStepLayout(
        currentStep = 2,
        title = "성별을\n선택해주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            viewModel.onGenderChanged(selectedGender)
            onNavigateToJob()
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        OptionButton(
            text = "남성",
            selected = selectedGender == Gender.MALE,
            onClick = { viewModel.onGenderChanged(Gender.MALE) }
        )

        Spacer(Modifier.height(10.scaler))

        OptionButton(
            text = "여성",
            selected = selectedGender == Gender.FEMALE,
            onClick = { viewModel.onGenderChanged(Gender.FEMALE) }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "소셜 성별 선택 - 프리뷰")
@Composable
fun SocialGenderScreenPreview() {
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