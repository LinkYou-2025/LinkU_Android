package com.example.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.Paperlogy

//여백 없는 순수 로그인 버튼 코어
@Composable
fun GradientButtonCore(
    text: String,
    enabled: Boolean,
    activeGradient: List<Color>,
    inactiveGradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
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

//프리뷰
@Preview(
    showBackground = true,
    name = "GradientButtonCore - Enabled"
)
@Composable
private fun GradientButtonCoreEnabledPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        GradientButtonCore(
            text = "로그인하기",
            enabled = true,
            activeGradient = listOf(
                Color(0xFF2C6FFF),
                Color(0xFFC800FF)
            ),
            inactiveGradient = listOf(
                Color(0xFF9BCBFF),
                Color(0xFFF4AFFF)
            ),
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "GradientButtonCore - Disabled"
)
@Composable
private fun GradientButtonCoreDisabledPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        GradientButtonCore(
            text = "로그인하기",
            enabled = false,
            activeGradient = listOf(
                Color(0xFF2C6FFF),
                Color(0xFFC800FF)
            ),
            inactiveGradient = listOf(
                Color(0xFF9BCBFF),
                Color(0xFFF4AFFF)
            ),
            onClick = {}
        )
    }
}

