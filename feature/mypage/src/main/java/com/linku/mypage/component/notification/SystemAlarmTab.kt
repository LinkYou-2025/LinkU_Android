package com.linku.mypage.component.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.mypage.R

@Composable
fun SystemAlarmTab(
    onClick: () -> Unit,
    isSystemAlarmAllowed: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 17.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        if (isSystemAlarmAllowed) R.drawable.ic_info_blue
                        else R.drawable.ic_info_red
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 2.dp, top = 2.dp)
                )
            }

            // 분기 처리 및 특정 텍스트에만 스타일을 따로 지정
            val text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight(400),
                        color = LocalColorTheme.current.black
                    )
                ) {
                    append(if (isSystemAlarmAllowed) "알림이 허용되어 있어요." else "알림이 허용되지 않았어요.")
                }
                append(if (isSystemAlarmAllowed) "\n기기 설정에서 알림을 변경할 수 있어요." else "\n기기 설정에서 알림을 허용해주세요.")
            }

            Text(
                text = text,
                color = LocalColorTheme.current.gray[600],
                modifier = Modifier
                    .padding(start = 12.dp),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier.size(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_long_right),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SystemAlarmTabPreview() {
    LinkuPreview {
        SystemAlarmTab(
            onClick = {},
            isSystemAlarmAllowed = false
        )
    }
}