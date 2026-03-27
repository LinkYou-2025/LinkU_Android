package com.linku.curation.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.font.Laundrygothic
import com.linku.design.util.scaler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.remember
import java.time.LocalDate
import com.linku.curation.R
// 월 약어 리스트
private val MONTH_LABELS = listOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarIconBox(
    modifier: Modifier = Modifier
) {
    // 전달 계산 +  연도 넘김도 자동으로 처리됨.
    val prevMonth = remember {
        LocalDate.now().minusMonths(1)
    }
    val year = prevMonth.year.toString()
    val monthLabel = MONTH_LABELS[prevMonth.monthValue - 1]

    Box(
        modifier = modifier
            .width(45.dp)
            .height(48.dp),
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
                style = TextStyle(
                    fontSize = 6.sp,
                    fontFamily = Laundrygothic.font,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(horizontal = 13.scaler)
            )

            Spacer(modifier = Modifier.height(7.scaler))

            // 월
            Text(
                text = monthLabel,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 30.sp,
                    fontFamily = Laundrygothic.font,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(horizontal = 1.scaler)
            )
        }


    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewCalendarIconBox() {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CalendarIconBox()
    }
}