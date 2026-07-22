package com.linku.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

//여백 없는 순수 로그인 버튼 코어
@Composable
fun GradientButtonCore(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = if (enabled) colorTheme.maincolor else colorTheme.inactiveColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            // 높이를 고정하지 않고 텍스트 위아래 패딩으로 보장 (글씨 크기 반응형)
            .padding(vertical = (19.scaler)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorTheme.white,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
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
                onClick = {}
            )
        }
    }
}
