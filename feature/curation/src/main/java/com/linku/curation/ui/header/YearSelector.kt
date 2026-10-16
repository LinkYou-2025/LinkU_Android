package com.linku.curation.ui.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
internal fun YearSelector(
    year: Int,
    isPrevEnabled: Boolean,
    isNextEnabled: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorTheme = MaterialTheme.linkuColors // 링큐 컬러 테마 설정

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(37.scaler))
    {
        YearArrowButton(YearArrowDirection.PREV, isPrevEnabled, onPrevClick)

        Text(
            text = year.toString(),
            style = LocalTextStyle.current.copy(
                brush = colorTheme.curationGradient,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight(600),
            ),
        )

        YearArrowButton(YearArrowDirection.NEXT, isNextEnabled, onNextClick)

    }
}

@Preview(name = "연도 선택", showBackground = true)
@Composable
private fun YearSelectorPreview() {
    LinkuPreview {
        YearSelector(
            year = 2027,
            isPrevEnabled = true,
            isNextEnabled = false,
            onPrevClick = {},
            onNextClick = {},
        )
    }
}