package com.example.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic

@Composable
fun AIArticleModal(
    modifier: Modifier = Modifier // ✅ 외부에서 전달받을 modifier
) {
    val progress by remember { mutableStateOf(0.6f) } // 60% 예시

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LocalColorTheme.current.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI 요약 중...",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            color = LocalColorTheme.current.black,
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
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
            color = LocalColorTheme.current.gray[600],
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = "잠시만 기다려주세요.",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
            color = LocalColorTheme.current.gray[600]
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
                    .background(brush = Basic.maincolor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "그만두기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(27.92.dp))
        }
    }
}

@Composable
fun SimpleProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(LocalColorTheme.current.gray[200])
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFD4E1FF), Color(0xFFF2CCFF))))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAIArticleModal() {
    AIArticleModal()
}