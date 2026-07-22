package com.linku.curation.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.curation.R

// 월(1~12) -> 월간 큐레이션 카드 이미지(img_monthly_curation_*) 매핑
// month가 1~12를 벗어나면(서버 오응답 등) 크래시 대신 투명 Painter를 반환한다.
@Composable
fun resolveMonthlyCurationImage(month: Int): Painter {
    return when (month) {
        1 -> painterResource(R.drawable.img_monthly_curation_jan)
        2 -> painterResource(R.drawable.img_monthly_curation_feb)
        3 -> painterResource(R.drawable.img_monthly_curation_mar)
        4 -> painterResource(R.drawable.img_monthly_curation_apr)
        5 -> painterResource(R.drawable.img_monthly_curation_may)
        6 -> painterResource(R.drawable.img_monthly_curation_jun)
        7 -> painterResource(R.drawable.img_monthly_curation_jul)
        8 -> painterResource(R.drawable.img_monthly_curation_aug)
        9 -> painterResource(R.drawable.img_monthly_curation_sep)
        10 -> painterResource(R.drawable.img_monthly_curation_oct)
        11 -> painterResource(R.drawable.img_monthly_curation_nov)
        12 -> painterResource(R.drawable.img_monthly_curation_dec)
        else -> ColorPainter(Color.Transparent) // -> 이거 오늘 오목교 개발 데이트 때 다인 언니랑 정해주면 되겠따!
    }
}
