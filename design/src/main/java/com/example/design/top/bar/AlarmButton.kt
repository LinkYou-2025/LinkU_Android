package com.example.design.top.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.design.R
import com.example.design.theme.LocalColorTheme
import com.example.design.util.PixelScaler

// 알림 버튼
@Composable
fun AlarmButton(
    isNoticeExist: Boolean,  // 알림 존재 여부
    modifier: Modifier = Modifier
) {
    BoxWithConstraints() {
        var ps = PixelScaler(
            maxWidth = this.maxWidth,
            maxHeight = this.maxHeight,
            baseWidth = 24.06.dp, // 피그마에서 보이는 너비
            baseHeight = 28.56.dp // 피그마에서 보이는 높이
        )

        Box(modifier = modifier) {
            with(ps) {
                // 알림 아이콘
                Icon(
                    painter = painterResource(id = R.drawable.ic_alarm),
                    contentDescription = null,
                    tint = LocalColorTheme.current.gray[300],
                    modifier = Modifier
                        .size(29.dp.scaled())
                        .align(Alignment.Center)
                )

                // 만약 알림이 존재한다면 빨간 원 보여주기
                if (isNoticeExist) {
                    Box(
                        modifier = Modifier
                            .size(11.dp.scaled())
                            .align(Alignment.TopEnd)  // 빨간 원 오른쪽 위로 이동
                            .offset(  // 빨간 원 오른쪽 위에서 원하는 위치로 더 이동
                                x = 2.82.dp.scaled(),
                                y = (-1).dp.scaled()
                            )
                            .background(
                                LocalColorTheme.current.negative,
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = 3.dp.scaled(),
                                color = LocalColorTheme.current.white,
                                shape = RoundedCornerShape(50)
                            )
                            .zIndex(1f)  // 아이콘보다 위 레이어에 뱃지를 올려서 겹쳐 보이게끔 함
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmButton() {
    AlarmButton(
        isNoticeExist = true
    )
}