package com.linku.login.ui.screen.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Interest
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialInterestScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel,
    onComplete: () -> Unit
) {
    val savedInterests by viewModel.interests.collectAsStateWithLifecycle()

    val selectedInterests = remember {
        mutableStateListOf<Interest>().apply {
            addAll(savedInterests)
        }
    }

    SignUpSelectionLayout(
        currentStep = 3,
        totalSteps = 3,
        stepLabel = "관심사 설정",
        titleText = buildAnnotatedString {
            append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
        },
        subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
        items = Interest.getAllInterests(),
        selectedItems = selectedInterests,
        buttonText = "완료",
        canProceed = selectedInterests.isNotEmpty(),
        onButtonClick = {
            if (selectedInterests.isEmpty()) return@SignUpSelectionLayout
            viewModel.updateInterests(selectedInterests.toList())
            onComplete()
        },
        onToggle = { interest ->
            if (selectedInterests.contains(interest)) selectedInterests.remove(interest)
            else selectedInterests.add(interest)
        }
    )
}

// 1. 미선택
@Preview(showBackground = true, name = "소셜 관심사 선택 - 미선택")
@Composable
private fun SocialInterestScreenPreview() {
    val selectedInterests = remember { mutableStateListOf<Interest>() }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            totalSteps = 3,
            stepLabel = "관심사 설정",
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.getAllInterests(),
            selectedItems = selectedInterests,
            buttonText = "완료",
            canProceed = selectedInterests.isNotEmpty(),
            onButtonClick = {},
            onToggle = { interest ->
                if (selectedInterests.contains(interest)) selectedInterests.remove(interest)
                else selectedInterests.add(interest)
            }
        )
    }
}

// 2. 선택됨
@Preview(showBackground = true, name = "소셜 관심사 선택 - 선택됨")
@Composable
private fun SocialInterestScreenSelectedPreview() {
    val selectedInterests = remember {
        mutableStateListOf(Interest.IT, Interest.DESIGN, Interest.STARTUP)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            totalSteps = 3,
            stepLabel = "관심사 설정",
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.getAllInterests(),
            selectedItems = selectedInterests,
            buttonText = "완료",
            canProceed = selectedInterests.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}