package com.linku.login.ui.screen.email

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.icon.iconPainter
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.SignUpEffect

@Composable
internal fun InterestPurposeScreen(
    onBackClick: () -> Unit,
    onNavigateToInterest: () -> Unit,
    signUpViewModel: SignUpViewModel
) {
    BackHandler { onBackClick() }

    val signUpState by signUpViewModel.state.collectAsStateWithLifecycle()
    val selectedPurposes = signUpState.signUpForm.purposeList

    LaunchedEffect(signUpViewModel.sideEffect) {
        signUpViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToInterest -> {
                    onNavigateToInterest()
                }

                else -> { /* 형식상 남겨용 */
                }
            }
        }
    }


    SignUpSelectionLayout(
        currentStep = 3,
        titleText = buildAnnotatedString {
            append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
        },
        subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
        items = Purpose.entries.toList(),
        iconPainter = { it.iconPainter },
        selectedItems = selectedPurposes,
        buttonText = "다음",
        canProceed = selectedPurposes.isNotEmpty(),
        onButtonClick = {
            signUpViewModel.onPurposeNextClicked()
        },
        onToggle = { purpose ->
            signUpViewModel.togglePurpose(purpose)
        }
    )
}

// 1. 미선택
@Preview
@Composable
private fun InterestPurposeScreenPreview() {
    val selectedPurposes = remember { mutableStateListOf<Purpose>() }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요? ")

            },
            subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
            items = Purpose.entries.toList(),
            iconPainter = { it.iconPainter },
            selectedItems = selectedPurposes,
            buttonText = "다음",
            canProceed = selectedPurposes.isNotEmpty(),
            onButtonClick = {},
            onToggle = { purpose ->
                if (selectedPurposes.contains(purpose)) selectedPurposes.remove(purpose)
                else selectedPurposes.add(purpose)
            }
        )
    }
}

// 2. 선택됨
@Preview
@Composable
private fun InterestPurposeScreenSelectedPreview() {
    val selectedPurposes = remember {
        mutableStateListOf(Purpose.CAREER, Purpose.SIDE_PROJECT, Purpose.STUDY)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요? ")
            },
            subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
            items = Purpose.entries.toList(),
            iconPainter = { it.iconPainter },
            selectedItems = selectedPurposes,
            buttonText = "다음",
            canProceed = selectedPurposes.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}





