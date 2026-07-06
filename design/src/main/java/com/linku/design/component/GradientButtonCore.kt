package com.linku.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

//여백 없는 순수 로그인 버튼 코어
@Composable
fun GradientButtonCore(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    activeGradient: Brush,
    inactiveGradient: Brush,
    onClick: () -> Unit,
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((50.scaler))
            .background(
                brush = if (enabled) activeGradient else inactiveGradient,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorTheme.white,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy.font,
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
    // 실제 앱에서는 LocalColorTheme이 주입되지만,
    // 프리뷰에서는 수동으로 Brush를 생성하거나 테마로 감싸서 확인합니다.
    LinkuPreview {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) //프리뷰니까 그냥 절대 컬러로 놓겠습니당
                .padding(20.dp)
        ) {
            GradientButtonCore(
                text = "로그인하기",
                enabled = true,
                // List<Color> 대신 Brush를 직접 생성하여 전달
                activeGradient = Brush.horizontalGradient(
                    listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                ),
                inactiveGradient = Brush.horizontalGradient(
                    listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF))
                ),
                onClick = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GradientButtonCore - Disabled"
)
@Composable
private fun GradientButtonCoreDisabledPreview() {
    LinkuPreview {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            GradientButtonCore(
                text = "로그인하기",
                enabled = false,
                activeGradient = Brush.horizontalGradient(
                    listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                ),
                inactiveGradient = Brush.horizontalGradient(
                    listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF))
                ),
                onClick = {}
            )
        }
    }
}
