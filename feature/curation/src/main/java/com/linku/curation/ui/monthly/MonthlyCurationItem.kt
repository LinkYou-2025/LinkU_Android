package com.linku.curation.ui.monthly

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.SubcomposeAsyncImage
import com.linku.curation.ui.resolveMonthNumberIcon
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 월별 큐레이션 모아보기(#41-1) 그리드의 월별 카드.
 *
 * [imageUrl]이 있으면 coil로 배경 이미지를 깔고 그 위에 [month](1~12)에 해당하는 숫자 이미지
 * (ic_monthly_number_*)를 얹는다. [imageUrl]이 없으면("") 해당 달은 아직 큐레이션이 없다는
 * 뜻이라 이미지도 숫자도 그리지 않고 회색 빈 박스만 남긴다.
 *
 * 너비는 카드 자체가 정하지 않고 [modifier]로 받는다(그리드에서 weight(1f)로 fillMax 처리).
 *
 * @param month 1~12 사이의 월. 숫자 이미지 선택에 사용됨
 * @param imageUrl 카드 배경 이미지 URL. API 응답이 null일 수 있어 nullable로 받고, null이면
 * 빈 문자열("")로 취급한다 - 이 경우 숫자도 표시하지 않음
 */
@Composable
fun MonthlyCurationItem(
    modifier: Modifier = Modifier,
    month: Int,
    imageUrl: String? = "", // null일 수 있습니다. 월간 큐레이션이 일부 누락 되면 null이에요
) {
    val colorTheme = MaterialTheme.linkuColors

    val resolvedImageUrl = imageUrl ?: ""
    val hasContent = resolvedImageUrl.isNotEmpty()

    Box(
        modifier = modifier
            .height(147.32948f.scaler)
            .clip(RoundedCornerShape(size = 18.scaler))
            .background(color = colorTheme.gray[200]),
        contentAlignment = Alignment.Center
    ) {
        if (hasContent) {
            SubcomposeAsyncImage(
                model = resolvedImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Image(
                painter = painterResource(id = resolveMonthNumberIcon(month)),
                contentDescription = "${month}월",
                modifier = Modifier.height(31.11111f.scaler)
            )
        }
    }
}

@Preview(name = "데이터 있음", showBackground = true)
@Composable
private fun MonthlyCurationItemPreview() {
    LinkuPreview {
        MonthlyCurationItem(
            modifier = Modifier.width(118.00001f.scaler),
            month = 1,
            imageUrl = "https://example.com/preview.jpg"
        )
    }
}

@Preview(name = "데이터 없음(빈 칸)", showBackground = true)
@Composable
private fun MonthlyCurationItemEmptyPreview() {
    LinkuPreview {
        MonthlyCurationItem(
            modifier = Modifier.width(118.00001f.scaler),
            month = 8
        )
    }
}
