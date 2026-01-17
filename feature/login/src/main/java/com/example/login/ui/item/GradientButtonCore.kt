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
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens

//여백 없는 순수 로그인 버튼 코어
@Composable
fun GradientButtonCore(
    text: String,
    enabled: Boolean,
    activeGradient: Brush,
    inactiveGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens() // 반응형 적용
    val paperlogyFamily = Paperlogy.font
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(h(50f)) // 🔑 높이 반응형 (50.dp -> h(50f))
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
            fontFamily = paperlogyFamily,
            textAlign = TextAlign.Center
        )
    }
}

//프리뷰
@Preview(
    showBackground = true,
    name = "GradientButtonCore - Enabled"
)
@Preview(
    showBackground = true,
    name = "GradientButtonCore - Enabled"
)
@Composable
private fun GradientButtonCoreEnabledPreview() {
    // 실제 앱에서는 LocalColorTheme이 주입되지만,
    // 프리뷰에서는 수동으로 Brush를 생성하거나 테마로 감싸서 확인합니다.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
            //  List<Color> 대신 Brush를 직접 생성하여 전달
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
