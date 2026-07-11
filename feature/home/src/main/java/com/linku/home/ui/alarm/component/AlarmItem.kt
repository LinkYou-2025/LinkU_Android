package com.linku.home.ui.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.home.ui.alarm.util.iconRes

@Composable
fun AlarmItem(
    alarm: AlarmSummary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .background(LocalColorTheme.current.white)
                .padding(top = 23.dp, bottom = 26.dp, start = 19.dp, end = 26.dp)
        ) {
            // 아이콘, 카테고리, 시간, 빨간 점
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 왼쪽: 아이콘 & 알람 타입
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = alarm.alarmType.iconRes,
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = alarm.alarmType.displayName,
                        fontSize = 13.sp,
                        color = LocalColorTheme.current.gray[600]
                    )
                }

                // 오른쪽: 시간 + 빨간 점
                Box {
                    Text(
                        text = alarm.whenSubmitted,
                        fontSize = 13.sp,
                        color = LocalColorTheme.current.gray[400],
                    )
                    if (!alarm.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp)
                                .background(
                                    color = LocalColorTheme.current.negative,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }

            // 본문
            Text(
                text = alarm.message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColorTheme.current.black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 33.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun AlarmItemPreview() {
    LinkuPreview {
        Box(
            modifier = Modifier
                .background(LocalColorTheme.current.white)
                .padding(16.dp)
        ) {
            AlarmItem(
                alarm = AlarmSummary(
                    id = 1L,
                    alarmType = AlarmType.LINK,
                    whenSubmitted = "2023-10-27 10:00:00",
                    message = "새로운 링크가 추가되었습니다.",
                    targetId = 101L,
                    isRead = false
                )
            )
        }
    }
}
