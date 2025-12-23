package com.example.login.ui.item


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.login.Paperlogy

@Composable
fun BottomGradientButton(
    text: String,
    enabled: Boolean,
    activeGradient: List<Color>,
    inactiveGradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    //  시스템 인셋 처리 시스템 자체 바텀바 위로 간격을 가진 채 버튼이 위치하게 수정하기!
    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBottom = WindowInsets.navigationBars.getBottom(density)

    val bottomPadding = when {
        imeBottom > 0 -> 20.dp //키보드 
        navBottom > 0 -> 16.dp //버튼바
        else -> 24.dp//완전 풀스크린 -> 이건 추후 확인하기.
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = bottomPadding
            )
            .height(50.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) activeGradient else inactiveGradient
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomGradientButtonEnabledPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomGradientButton(
            text = "인증하기",
            enabled = true,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomGradientButtonDisabledPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomGradientButton(
            text = "인증메일 발송",
            enabled = false,
            activeGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF)),
            inactiveGradient = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF)),
            onClick = {}
        )
    }
}

