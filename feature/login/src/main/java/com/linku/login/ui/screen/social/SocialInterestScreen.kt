package com.linku.login.ui.screen.social

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.icon.iconRes
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SocialAuthViewModel
import com.linku.login.viewmodel.state.SocialAuthUiEffect

@Composable
fun SocialInterestScreen(
    onBackClick: () -> Unit,
    viewModel: SocialAuthViewModel,
    socialToken: String,
    socialLoginType: LoginType,
    onComplete: () -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val selectedInterests = uiState.socialLoginForm.interests

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SocialAuthUiEffect.CompleteProfileSuccess -> {
                    // 응답 토큰으로 자동 로그인 세션 저장까지 리포지토리에서 이미 끝난 상태 -> 바로 홈으로.
                    onComplete()
                }

                is SocialAuthUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                else -> { /* 음 딱히 할게 없네용 */
                }
            }
        }
    }

    SignUpSelectionLayout(
        currentStep = 3,
        titleText = buildAnnotatedString {
            append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
        },
        subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
        items = Interest.entries.toList(),
        iconRes = { it.iconRes },
        selectedItems = selectedInterests,
        buttonText = "완료",
        canProceed = selectedInterests.isNotEmpty() && !uiState.isLoading,
        onButtonClick = {
            viewModel.completeSocialProfile(socialToken, socialLoginType)
        },
        onToggle = { interest ->
            viewModel.toggleInterest(interest)
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
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.entries.toList(),
            iconRes = { it.iconRes },
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
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedInterests,
            buttonText = "완료",
            canProceed = selectedInterests.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}