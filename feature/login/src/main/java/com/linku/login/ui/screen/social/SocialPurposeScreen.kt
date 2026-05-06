package com.linku.login.ui.screen.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Purpose
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialPurposeScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {
    val savedPurposes by viewModel.purposes.collectAsStateWithLifecycle()

    SignUpSelectionLayout(
        currentStep = 3,
        totalSteps = 3,
        stepLabel = "관심사 설정",
        titleText = buildAnnotatedString {
            append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
        },
        subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
        items = Purpose.getAllPurposes(),
        selectedItems = savedPurposes,
        buttonText = "다음",
        canProceed = savedPurposes.isNotEmpty(),
        onButtonClick = {
            if (savedPurposes.isEmpty()) return@SignUpSelectionLayout
            navigator.navigate("social_interest")
        },
        onToggle = { purpose ->
            val nextPurposes =
                if (savedPurposes.contains(purpose)) savedPurposes - purpose
                else savedPurposes + purpose
            viewModel.updatePurposes(nextPurposes)
        }
    )
}

// 1. 미선택
@Preview(showBackground = true, name = "소셜 목적 선택 - 미선택")
@Composable
private fun SocialPurposeScreenPreview() {
    val selectedPurposes = remember { mutableStateListOf<Purpose>() }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            totalSteps = 3,
            stepLabel = "관심사 설정",
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
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
@Preview(showBackground = true, name = "소셜 목적 선택 - 선택됨")
@Composable
private fun SocialPurposeScreenSelectedPreview() {
    val selectedPurposes = remember {
        mutableStateListOf(Purpose.CAREER, Purpose.SIDE_PROJECT, Purpose.STUDY)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            totalSteps = 3,
            stepLabel = "관심사 설정",
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
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