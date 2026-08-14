package com.linku.home.ui.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
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
import com.linku.design.util.scaler
import com.linku.home.ui.alarm.util.iconRes

@Composable
fun AlarmItem(
    alarm: AlarmSummary,
    isRead: Boolean = alarm.isRead,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
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
                        style = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = LocalColorTheme.current.gray[600],
                            fontWeight = FontWeight(400),
                            lineHeight = 15.sp

                        )
                    )
                }

                // 오른쪽: 시간 + 빨간 점
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = alarm.whenSubmitted,
                        style = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = LocalColorTheme.current.gray[400],
                            fontWeight = FontWeight(400),
                            lineHeight = 15.sp
                        )
                    )
                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .layout { measurable, constraints ->
                                    // 빨간 점의 실제 크기를 측정
                                    val placeable = measurable.measure(
                                        constraints.copy(minWidth = 0, maxWidth = 7.dp.roundToPx())
                                    )
                                    // 부모에게는 0x0 크기로 보고하여 Row의 공간을 차지하지 않도록 함
                                    // 측정된 빨간 점은 실제 위치에 따로 배치
                                    layout(width = 0, height = 0) {
                                        placeable.place(
                                            x = 4.dp.roundToPx(),
                                            y = (-4.5).dp.roundToPx() // -1(피그마 값) + (-3.5)원의 반지름
                                        )
                                    }
                                }
                                .size(7.dp)
                                .background(
                                    color = LocalColorTheme.current.negative,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(11.dp))

            // 본문
            Text(
                text = alarm.message,
                style = LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(500),
                    color = LocalColorTheme.current.black,
                    lineHeight = 22.sp
                ),
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
