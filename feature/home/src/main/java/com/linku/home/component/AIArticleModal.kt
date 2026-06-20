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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

@Composable
fun AIArticleModal(
    progress: Float,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI 요약 중...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.black,
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 상태바
        SimpleProgressBar(
            progress = progress,
            modifier = Modifier.width(200.dp)
        )

        Text(
            text = "AI가 링크 추출 후 본문 내용을 요약하고 있어요!\n나중에 돌아와서 확인할 수 있어요.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 21.dp)
        )

        Spacer(modifier = Modifier.height(27.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.blue[200])
                .noRippleClickable { onQuit() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나가기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.white
            )
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
                .background(colors.maincolor)
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun PreviewAIArticleModal() {
    LinkuPreview {
        AIArticleModal(
            progress = 0.5f,
            onQuit = { }
        )
    }
}
