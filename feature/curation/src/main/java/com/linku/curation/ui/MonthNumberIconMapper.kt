package com.linku.curation.ui

import androidx.annotation.DrawableRes
import com.linku.curation.R

// 월(1~12) -> 숫자 이미지(ic_monthly_number_*) 매핑

@DrawableRes
fun resolveMonthNumberIcon(month: Int): Int {
    return when (month) {
        1 -> R.drawable.ic_monthly_number_one
        2 -> R.drawable.ic_monthly_number_two
        3 -> R.drawable.ic_monthly_number_three
        4 -> R.drawable.ic_monthly_number_four
        5 -> R.drawable.ic_monthly_number_five
        6 -> R.drawable.ic_monthly_number_six
        7 -> R.drawable.ic_monthly_number_seven
        8 -> R.drawable.ic_monthly_number_eight
        9 -> R.drawable.ic_monthly_number_nine
        10 -> R.drawable.ic_monthly_number_ten
        11 -> R.drawable.ic_monthly_number_eleven
        12 -> R.drawable.ic_monthly_number_twelve
        else -> throw IllegalArgumentException("month는 1~12 사이여야 합니다: $month")
    }
}
