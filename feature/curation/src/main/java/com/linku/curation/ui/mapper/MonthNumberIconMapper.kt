package com.linku.curation.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.curation.R

// 월(1~12) -> 숫자 이미지(ic_monthly_number_*) 매핑
// month가 1~12를 벗어나면(서버 오응답 등) 크래시 대신 투명 Painter를 반환한다.

@Composable
fun resolveMonthNumberIcon(month: Int): Painter {
    return when (month) {
        1 -> painterResource(R.drawable.ic_monthly_number_one)
        2 -> painterResource(R.drawable.ic_monthly_number_two)
        3 -> painterResource(R.drawable.ic_monthly_number_three)
        4 -> painterResource(R.drawable.ic_monthly_number_four)
        5 -> painterResource(R.drawable.ic_monthly_number_five)
        6 -> painterResource(R.drawable.ic_monthly_number_six)
        7 -> painterResource(R.drawable.ic_monthly_number_seven)
        8 -> painterResource(R.drawable.ic_monthly_number_eight)
        9 -> painterResource(R.drawable.ic_monthly_number_nine)
        10 -> painterResource(R.drawable.ic_monthly_number_ten)
        11 -> painterResource(R.drawable.ic_monthly_number_eleven)
        12 -> painterResource(R.drawable.ic_monthly_number_twelve)
        else -> ColorPainter(Color.Transparent)
    }
}
