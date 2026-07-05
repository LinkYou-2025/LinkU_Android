package com.linku.curation.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

// 월 약어 리스트
private val MONTH_LABELS = listOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
)


@Composable
internal fun CalendarIconBox(
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors

    // 전달 계산 +  연도 넘김도 자동으로 처리됨.
    val prevMonth = remember {
        LocalDate.now().minusMonths(1)
    }
    val year = prevMonth.year.toString()
    val monthLabel = MONTH_LABELS[prevMonth.monthValue - 1]

    Box(
        modifier = modifier
            .width(45.scaler)
            .height(48.scaler),
        contentAlignment = Alignment.TopCenter
    ) {
        // 배경 달력 이미지
        Image(
            painter = painterResource(id = R.drawable.img_curation_calendar),
            contentDescription = null,
            modifier = Modifier
                .width(42.scaler)
                .height(45.scaler)
                .align(Alignment.TopCenter)
        )

        // 텍스트 Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.scaler)
        ) {
            // 연도
            Text(
                text = year,
                fontSize = 6.sp,
                lineHeight = 6.sp,
                fontFamily = Laundrygothic.font, // PaperLogy 아님!
                fontWeight = FontWeight(400),
                color = colorTheme.white,
                textAlign = TextAlign.Center,
            )


            // 월
            Text(
                text = monthLabel,
                fontSize = 14.sp,
                lineHeight = 30.sp,
                fontFamily = Laundrygothic.font, // PaperLogy 아님!
                fontWeight = FontWeight(700),
                color = colorTheme.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 0.scaler)
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