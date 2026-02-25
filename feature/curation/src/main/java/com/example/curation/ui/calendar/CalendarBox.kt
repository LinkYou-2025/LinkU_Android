package com.example.curation.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.font.Paperlogy
import com.example.design.util.scaler

@Composable
fun CalendarBox(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(372.scaler)
            .height(80.scaler)
            .background(
                color = Color(0xFFEFF4FF),
                shape = RoundedCornerShape(size = 22.scaler)
            )
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
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF43454B)
                ),
                modifier = Modifier.padding(top = 2.scaler)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "월간 큐레이션 모아보기",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000208)
                )
            )
        }
    }
}



@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewCalendarBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CalendarBox()
    }
}