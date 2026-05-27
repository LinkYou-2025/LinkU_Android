package com.linku.login.ui.screen.email

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Purpose
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SignUpViewModel

@Composable
internal fun InterestPurposeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel
) {

    val selectedPurposes = remember {
        mutableStateListOf<Purpose>().apply {
            addAll(signUpViewModel.uiState.value.signUpForm.purposeList)
        }
    }

    SignUpSelectionLayout(
        currentStep = 3,
        titleText = buildAnnotatedString {
            append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
        },
        subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
        items = Purpose.getAllPurposes(),
        selectedItems = selectedPurposes,
        buttonText = "다음",
        canProceed = selectedPurposes.isNotEmpty(),
        onButtonClick = {
            if (selectedPurposes.isEmpty()) return@SignUpSelectionLayout
            signUpViewModel.onPurposeListChanged(selectedPurposes.toList())
            navigator.navigate("sign_up_interest")
        },
        onToggle = { purpose ->
            if (selectedPurposes.contains(purpose)) selectedPurposes.remove(purpose)
            else selectedPurposes.add(purpose)
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
            items = Purpose.getAllPurposes(),
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
            items = Purpose.getAllPurposes(),
            selectedItems = selectedPurposes,
            buttonText = "다음",
            canProceed = selectedPurposes.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}





