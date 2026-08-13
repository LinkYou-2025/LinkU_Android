package com.linku.curation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
internal fun CalendarBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colorTheme = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 84.scaler) // 피그마 대로 하면 때려죽여도 안 맞춰지기 때문에 임의 삽입
            .background(
                color = colorTheme.curationCalendarBoxColor,
                shape = RoundedCornerShape(size = 22.scaler)
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 23.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        // 캘린더 아이콘 영역
        CalendarIconBox(
            modifier = Modifier.padding(top = 4.scaler)
        )

        Spacer(modifier = Modifier.width(15.scaler))

        // 텍스트 영역
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "지난 월간 큐레이션을 다시 볼 수 있어요",
                style = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = colorTheme.gray[800]
                )
            )

            Text(
                text = "월간 큐레이션 모아보기",
                style = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(700),
                    color = colorTheme.black
                )
            )
        }
    }
}


@Preview
@Composable
fun PreviewCalendarBox() {
    LinkuPreview {
        CalendarBox()
    }
}