package com.linku.login.ui.screen.email

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
import com.linku.core.model.auth.SignUpState
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.icon.iconRes
import com.linku.login.ui.layout.SignUpSelectionLayout
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.SignUpEffect

@Composable
internal fun InterestContentScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    signUpViewModel: SignUpViewModel
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val signUpState by signUpViewModel.state.collectAsStateWithLifecycle()
    val selectedInterests = signUpState.signUpForm.interestList

    // "다음"을 누르면 회원가입 API 호출 → 토큰 저장(자동 로그인)까지 리포지토리에서 처리됨.
    // 요청 진행 중에는 버튼을 비활성화해서 중복 제출을 막음.
    val signUpApiState by signUpViewModel.signUpState.collectAsStateWithLifecycle()

    LaunchedEffect(signUpViewModel.sideEffect) {
        signUpViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignUpEffect.NavigateToHome -> {
                    onSignUpSuccess()
                }

                is SignUpEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                else -> { /* 여기도 형식상 남겨용 */
                }
            }
        }
    }

    SignUpSelectionLayout(
        currentStep = 3,
        titleText = buildAnnotatedString {
            append("어떤 분야의 콘텐츠를\n관심 있으신가요? ")
        },
        subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
        items = Interest.entries.toList(),
        iconRes = { it.iconRes },
        selectedItems = selectedInterests,
        buttonText = "다음",
        canProceed = selectedInterests.isNotEmpty() && signUpApiState !is SignUpState.Loading,
        onButtonClick = {
            signUpViewModel.onInterestNextClicked()
        },
        onToggle = { interest ->
            signUpViewModel.toggleInterest(interest)
        }
    )
}

// 1. 미선택
@Preview(showBackground = true, name = "관심사 선택 - 미선택")
@Composable
private fun InterestContentScreenPreview() {
    val selectedInterests = remember { mutableStateListOf<Interest>() }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요? ")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedInterests,
            buttonText = "다음",
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
@Preview(showBackground = true, name = "관심사 선택 - 선택됨")
@Composable
private fun InterestContentScreenSelectedPreview() {
    val selectedInterests = remember {
        mutableStateListOf(Interest.IT, Interest.DESIGN, Interest.STARTUP)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요? ")
            },
            subText = "선택해주신 관심사에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedInterests,
            buttonText = "다음",
            canProceed = selectedInterests.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}

