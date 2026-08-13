package com.linku.curation.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.curation.R
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Laundrygothic
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import java.time.LocalDate

private val MONTH_LABELS = listOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
)

@Composable
internal fun CalendarIconBox(
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

    val prevMonth = remember {
        LocalDate.now().minusMonths(1)
    }

    val year = prevMonth.year.toString()
    val monthLabel = MONTH_LABELS[prevMonth.monthValue - 1]

    Box(
        modifier = modifier
            .width(46.scaler)
            .height(49.scaler)
    ) {

        // 달력 배경
        Image(
            painter = painterResource(
                id = R.drawable.img_curation_calendar
            ),
            contentDescription = null,
            modifier = Modifier
                .width(46.scaler)
                .height(49.scaler)
                .align(Alignment.TopCenter)
        )

        // 연도 영역
        // Figma 디자인 스펙
        // 상단 여백: 4dp
        // 좌우 여백: 13dp
        // 하단 여백: 2.44dp
        //
        // 실제 Text를 16dp로 잘라내지 않고
        // 42dp 전체 영역에서 중앙 정렬
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.scaler)
                .align(Alignment.TopCenter)
                .offset(y = 6.scaler),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = year,
                style = TextStyle(
                    fontSize = 5.8.sp,
                    lineHeight = 8.sp,
                    fontFamily = Laundrygothic.font,
                    fontWeight = FontWeight(400),
                    color = colorTheme.white,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 월 약어
        // Figma 디자인 스펙
        // width: 40dp
        // height: 25dp
        // fontSize: 14sp
        // lineHeight: 30sp
        // fontWeight: 700
        //
        // 파란색 헤더 아래 흰색 영역 중앙
        Box(
            modifier = Modifier
                .width(40.scaler)
                .height(25.scaler)
                .align(Alignment.TopCenter)
                .offset(y = 19.scaler),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monthLabel,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 30.sp,
                    fontFamily = Laundrygothic.font,
                    fontWeight = FontWeight(700),
                    color = colorTheme.black,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreviewCalendarIconBox() {
    LinkuPreview {
        CalendarIconBox()
    }
}