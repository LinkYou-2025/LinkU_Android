package com.linku.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.home.R

@Composable
fun DeleteLinkModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LocalColorTheme.current.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_linku_blur),
                contentDescription = null,
                modifier = Modifier
                    .height(25.dp)
            )
        }

        Text(
            text = "해당 링크를 삭제하시겠습니까?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = LocalColorTheme.current.black,
            modifier = Modifier.padding(top = 15.dp)
        )

        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontFamily = LocalFontTheme.current.font,
            color = LocalColorTheme.current.gray[600],
            modifier = Modifier.padding(top = 13.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 20.dp, start = 27.dp, end = 27.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, brush = Basic.maincolor), RoundedCornerShape(14.dp))
                    .background(LocalColorTheme.current.white)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "취소하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        brush = Basic.maincolor,  // 그라데이션 Brush 사용
                        fontFamily = LocalFontTheme.current.font
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
                    .background(brush = Basic.maincolor)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "삭제하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font
                    ),
                    color = LocalColorTheme.current.white
                )
            }
        }

        Spacer(modifier = Modifier.height(23.dp))
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewDeleteLinkModal() {
    ThemeProvider {
        DeleteLinkModal(
            onDismiss = {},
            onConfirm = {}
        )
    }
}