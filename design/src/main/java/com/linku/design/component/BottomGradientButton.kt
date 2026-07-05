package com.linku.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors

//GradientButtonCore에서 순수 버튼 ui를 받아온 뒤, 여기서는 회원가입 중
//바닥에 있는 패딩 위치, 패딩 조절을 담당함.
@Composable
fun BottomGradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    activeGradient: Brush,
    inactiveGradient: Brush,
    onClick: () -> Unit,
    label: String = "",
) {
    val colorTheme = MaterialTheme.linkuColors
    val density = LocalDensity.current

    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBottom = WindowInsets.navigationBars.getBottom(density)
    // 🔑 화면 높이 기준 (피그마 917)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val bottomPadding = when {
        imeBottom > 0 -> 20.dp   // 키보드 열림
        navBottom > 0 -> screenHeight * (16f / 917f)  // 네비게이션 바 없는 경우(반응형으로 수정)
        else -> screenHeight * (24f / 917f) //기본 안드로이드 바텀바 있는 경우(반응형으로 수정)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = bottomPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(400),
                color = colorTheme.gray[600],
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        GradientButtonCore(
            text = text,
            enabled = enabled,
            activeGradient = activeGradient,
            inactiveGradient = inactiveGradient,
            onClick = onClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomGradientButtonEnabledPreview() {
    // 🔑 디자인 모듈의 컬러를 직접 생성하거나 테마로 감싸서 확인
    val activeBrush = Brush.horizontalGradient(listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)))
    val inactiveBrush = Brush.horizontalGradient(listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomGradientButton(
            text = "인증하기",
            enabled = true,
            activeGradient = activeBrush,
            inactiveGradient = inactiveBrush,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "BottomGradientButton - With Label")
@Composable
private fun BottomGradientButtonWithLabelPreview() {
    val activeBrush = Brush.horizontalGradient(listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)))
    val inactiveBrush = Brush.horizontalGradient(listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomGradientButton(
            text = "링크 저장하러 가기",
            enabled = true,
            activeGradient = activeBrush,
            inactiveGradient = inactiveBrush,
            onClick = {},
            label = "이번 달은 선택하신 상황/감정이 없네요!"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomGradientButtonDisabledPreview() {
    val activeBrush = Brush.horizontalGradient(listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)))
    val inactiveBrush = Brush.horizontalGradient(listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomGradientButton(
            text = "인증메일 발송",
            enabled = false,
            activeGradient = activeBrush,
            inactiveGradient = inactiveBrush,
            onClick = {}
        )
    }
}