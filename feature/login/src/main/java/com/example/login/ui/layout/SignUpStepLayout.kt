package com.example.login.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.ThemeProvider
import com.example.design.theme.font.Paperlogy
import com.example.design.util.scaler
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.StepIndicator

/**
 * 회원가입에서 공통적으로 사용하는
 *  - StepIndicator, Title, BottomGradientButton 공통 처리
 *  - bottomSlot: 버튼 영역 커스텀 필요 시 사용 (ex. EmailVerificationScreen)
 *  - backgroundColor: 다크모드 확장 대비, 기본값은 ThemeColorScheme.white
 * 추후 기능 확장 혹은 ui 변경시 유연하게 대처가 가능합니다.
 * 다크모드 기능 확장까지 고려했습니다.
 * SignUpNicknameScreen, SocialNicknameScreen, SignUpGenderScreen, SocialGenderScreen
 * SignUpJobScreen, SocialJobScreen, SignUpPasswordScreen, EmailInputScreen
 * 수정 완.
 *
 * 흥미, 목적은 ui 나오는대로 이거 적용해서 수정할 예정.
 * */


@Composable
internal fun SignUpStepLayout(
    currentStep: Int,
    totalSteps: Int,
    label: String,
    title: String,
    buttonText: String = "다음",
    buttonEnabled: Boolean,
    onNextClick: () -> Unit,
    backgroundColor: Color = Color.Unspecified,          // null이면 colorTheme.white 자동 사용
    bottomSlot: (@Composable BoxScope.() -> Unit)? = null, // EmailVerification 예외 대응
    content: @Composable ColumnScope.() -> Unit
) {
    val colorTheme = LocalColorTheme.current

    val bgColor = if (backgroundColor == Color.Unspecified) {
        colorTheme.white
    } else {
        backgroundColor
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.scaler,
                    end = 20.scaler,
                    top = 60.scaler,
                    bottom = 72.scaler
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(currentStep, totalSteps, label)

            Spacer(Modifier.height(32.scaler))

            Text(
                text = title,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black // 다크모드 시 ThemeColorScheme에 darkText 추가하면 됨
            )

            Spacer(Modifier.height(32.scaler))

            content()
        }

        // bottomSlot 있으면 커스텀, 없으면 기본 버튼
        if (bottomSlot != null) {
            bottomSlot()
        } else {
            BottomGradientButton(
                text = buttonText,
                enabled = buttonEnabled,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = onNextClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// 프리뷰 전용 래퍼 - ThemeProvider 감싸서 폰트/컬러 정상 적용
@Composable
fun SignUpStepLayoutPreview(
    currentStep: Int,
    totalSteps: Int,
    label: String,
    title: String,
    buttonText: String = "다음",
    buttonEnabled: Boolean,
    onNextClick: () -> Unit = {},
    bottomSlot: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ThemeProvider {  // 폰트/컬러 CompositionLocal 주입 : 이거 없으면 프리뷰시 글자 폰트 깨짐.
        SignUpStepLayout(
            currentStep = currentStep,
            totalSteps = totalSteps,
            label = label,
            title = title,
            buttonText = buttonText,
            buttonEnabled = buttonEnabled,
            onNextClick = onNextClick,
            bottomSlot = bottomSlot,
            content = content
        )
    }
}