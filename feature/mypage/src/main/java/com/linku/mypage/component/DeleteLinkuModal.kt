package com.linku.mypage.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.BrushText
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.mypage.R

@Composable
fun DeleteLinkuModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 23.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_linku_blur),
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
            )
        }

        Text(
            text = "해당 링크를 삭제하시겠습니까?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.black,
            modifier = Modifier.padding(top = 22.dp)
        )

        Text(
            text = "AI 요약이 포함된 링크입니다.\n삭제 시 링크와 요약 내용이 영구적으로 삭제됩니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            modifier = Modifier.padding(top = 15.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 25.dp, start = 27.dp, end = 27.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, brush = Basic.maincolor), RoundedCornerShape(14.dp))
                    .background(colors.white)
                    .noRippleClickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                BrushText(
                    text = "취소하기",
                    brush = Basic.maincolor,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = Basic.maincolor)
                    .noRippleClickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "삭제하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.white
                )
            }
        }

        Spacer(modifier = Modifier.height(27.92.dp))
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewDeleteLinkuModal() {
    ThemeProvider {
        DeleteLinkuModal(
            onDismiss = {},
            onConfirm = {}
        )
    }
}