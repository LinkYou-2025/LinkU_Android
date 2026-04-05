package com.linku.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont

@Composable
fun AIArticleModal(
    progress: Float,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier // ✅ 외부에서 전달받을 modifier
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI 요약 중...",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = font),
            color = colors.black,
            modifier = Modifier.padding(top = 45.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 상태바
        SimpleProgressBar(
            progress = progress,
            modifier = Modifier.padding(horizontal = 86.dp)
        )

        Text(
            text = "AI가 링크 추출 후 본문 내용을 요약하고 있어요!",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = font),
            color = colors.gray[600],
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = "잠시만 기다려주세요.",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = font),
            color = colors.gray[600]
        )

        Column(
            modifier = Modifier
                .padding(top = 36.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 28.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = MaterialTheme.linkuColors.maincolor)
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "그만두기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                    color = colors.white
                )
            }

            Spacer(modifier = Modifier.height(27.92.dp))
        }
    }
}

@Composable
fun SimpleProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.linkuColors

    val animated = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "aiProgress"
    ).value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.gray[200])
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animated)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF))))
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewAIArticleModal() {
//    AIArticleModal()
//}