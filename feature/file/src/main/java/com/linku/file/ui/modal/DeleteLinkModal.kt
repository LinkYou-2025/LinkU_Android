package com.linku.file.ui.modal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont

@Composable
fun DeleteLinkModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val font = MaterialTheme.linkuFont.font
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "해당 링크를 삭제하시겠습니까?",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium), fontFamily = font,
            color = colors.black,
            modifier = Modifier.padding(top = 45.dp)
        )

        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp, textAlign = TextAlign.Center, fontFamily = font),
            color = colors.gray[600],
            modifier = Modifier.padding(top = 35.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 36.dp, start = 27.dp, end = 27.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, brush = colors.maincolor), RoundedCornerShape(14.dp))
                    .background(colors.white)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "취소하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        brush = colors.maincolor,  // 그라데이션 Brush 사용
                        fontFamily = font
                    ),
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f) // brush 적용 시 필수
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = colors.maincolor)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "삭제하기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                    color = colors.white
                )
            }
        }

        Spacer(modifier = Modifier.height(27.92.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDeleteLinkModal() {
    DeleteLinkModal(
        onDismiss = {},
        onConfirm = {}
    )
}