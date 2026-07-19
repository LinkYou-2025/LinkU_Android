package com.linku.login.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.StepIndicator

/**
 * 회원가입 단계 화면에서 공통으로 사용하는 레이아웃.
 *
 * StepIndicator, Title, BottomGradientButton을 공통 처리하며
 * SignUpNicknameScreen, SocialNicknameScreen, SignUpGenderScreen, SocialGenderScreen,
 * SignUpJobScreen, SocialJobScreen, SignUpPasswordScreen, EmailInputScreen 에서 사용된다.
 *
 * @param currentStep 현재 진행 중인 회원가입 단계
 * @param title 화면 상단에 표시할 제목
 * @param buttonEnabled 하단 버튼(BottomGradientButton) 활성화 여부
 * @param onNextClick 하단 버튼 클릭 시 실행할 콜백
 * @param modifier 최상위 Box에 적용할 Modifier
 * @param bottomSlot 버튼 영역을 커스텀해야 할 때 사용 (ex. EmailVerificationScreen). null이면 기본 BottomGradientButton 사용
 * @param content 제목 아래에 표시할 화면별 콘텐츠
 */
@Composable
internal fun SignUpStepLayout(
    currentStep: Int,
    title: String,
    buttonEnabled: Boolean,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottomSlot: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorTheme.white)
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
            StepIndicator(currentStep)

            Spacer(Modifier.height(32.scaler))

            Text(
                text = title,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black // 다크모드 시 ThemeColorScheme에 darkText 추가하면 됨
            )

            Spacer(Modifier.height(32.scaler))

            content()
        }

        // bottomSlot 있으면 커스텀, 없으면 기본 버튼
        // BottomGradientButton은 자체적으로 fillMaxWidth+좌우 여백을 처리하지만,
        // bottomSlot은 그런 처리가 없어 여기서 좌우 20.scaler 여백 + 가로 꽉 채움을 직접 보장해준다.
        if (bottomSlot != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.scaler),
                content = bottomSlot
            )
        } else {
            BottomGradientButton(
                text = "다음",
                enabled = buttonEnabled,
                onClick = onNextClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/** 프리뷰 전용 래퍼. ThemeProvider(LinkuPreview)로 감싸 폰트/컬러가 정상 적용되도록 한다. */
@Composable
fun SignUpStepLayoutPreview(
    currentStep: Int,
    title: String,
    buttonEnabled: Boolean,
    onNextClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    bottomSlot: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    LinkuPreview {
        SignUpStepLayout(
            currentStep = currentStep,
            title = title,
            buttonEnabled = buttonEnabled,
            onNextClick = onNextClick,
            modifier = modifier,
            bottomSlot = bottomSlot,
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpStepLayoutSamplePreview() {
    SignUpStepLayoutPreview(
        currentStep = 2,
        title = "성별을\n선택해주세요",
        buttonEnabled = true
    ) {
        Text(
            text = "Preview Content",
            fontSize = 16.sp,
            color = MaterialTheme.linkuColors.black
        )
    }
}