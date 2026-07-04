package com.linku.curation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CalendarBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colorTheme = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .width(372.scaler)
            .height(80.scaler)
            .background(
                color = colorTheme.curationCalendarBoxColor,
                shape = RoundedCornerShape(size = 22.scaler)
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 20.scaler),

        verticalAlignment = Alignment.CenterVertically
    ) {
        // 캘린더 아이콘 영역 TODO : 피그마 대비 좀 사이즈가 작아보임. 디자이너와 협의 후 사이즈 조정 필요.
        CalendarIconBox(
            modifier = Modifier.padding(top = 7.scaler)
        )

        Spacer(modifier = Modifier.width(15.scaler))

        // 텍스트 영역
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "지난 월간 큐레이션을 다시 볼 수 있어요",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400),
                color = colorTheme.gray[800],
                modifier = Modifier.padding(top = 2.scaler)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "월간 큐레이션 모아보기",
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(700),
                color = colorTheme.black
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