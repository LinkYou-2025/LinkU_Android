package com.linku.curation.ui.monthly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

private const val COLUMN_COUNT = 3
private const val MONTH_COUNT = 12

/**
 * 월별 큐레이션 모아보기(#41-1)의 12개월 그리드 (3열 x 4행).
 * 좌우 20, 칸 사이 간격 9는 [scaler]로 고정. 카드 너비는 [MonthlyCurationItem]에 고정값을
 * 주지 않고 weight(1f)로 나눠 fillMaxWidth 되게 해서 화면 너비가 달라져도 20/9 여백을
 * 뺀 나머지가 3등분으로 꽉 차게 맞춰진다.
 *
 * @param onMonthClick 월 카드 클릭 콜백. 클릭된 월(1~12)을 전달함
 * @param imageUrlOf 월(1~12)에 해당하는 카드 배경 이미지 URL을 반환하는 함수. API 응답이 null일 수
 * 있어 nullable로 받으며, [MonthlyCurationItem]에서 null을 빈 문자열("")로 취급한다
 */
@Composable
fun MonthlyCurationGrid(
    modifier: Modifier = Modifier,
    onMonthClick: (Int) -> Unit = {},
    imageUrlOf: (Int) -> String? = { "" },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.scaler),
        verticalArrangement = Arrangement.spacedBy(9.scaler)
    ) {
        (1..MONTH_COUNT step COLUMN_COUNT).forEach { rowStart ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.scaler)
            ) {
                (rowStart until rowStart + COLUMN_COUNT).forEach { month ->
                    MonthlyCurationItem(
                        month = month,
                        imageUrl = imageUrlOf(month),
                        modifier = Modifier
                            .weight(1f)
                            .noRippleClickable { onMonthClick(month) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthlyCurationGridPreview() {
    LinkuPreview {
        MonthlyCurationGrid()
    }
}
