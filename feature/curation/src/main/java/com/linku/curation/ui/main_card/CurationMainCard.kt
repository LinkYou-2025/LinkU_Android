package com.linku.curation.ui.main_card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.SubcomposeAsyncImage
import com.linku.curation.R
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 큐레이션 메인 카드
 *
 * - Backend 이미지 URL 표시
 * - imageUrl == null / blank → fallback 이미지 표시
 * - 사이즈: 346 x 432 (scaler)
 *
 * @param modifier 외부에서 전달하는 Modifier (기본값: Modifier)
 * @param imageUrl 백엔드에서 받아오는 카드 이미지 URL. null이거나 빈 문자열이면 fallbackImage로 대체됨
 * @param fallbackImage imageUrl이 없을 때 표시할 기본 이미지 리소스 (기본값: img_curation_example)
 */
@Composable
fun CurationMainCard(
    modifier: Modifier = Modifier,
    imageUrl: String? = "",
    @DrawableRes fallbackImage: Int = R.drawable.img_curation_example, //이건 프리뷰 확인 용이라 나중에 지워주세요:)
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .size(
                width = 346.scaler,
                height = 432.scaler
            )
            .clip(RoundedCornerShape(24.scaler))
            .background(colorTheme.curationCardBackground)
    ) {
        if (imageUrl.isNullOrBlank()) {
            Image(
                painter = painterResource(id = fallbackImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            // 실제 백엔드에서 받아온 이미지
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationMainCard() {
    CurationMainCard()
}

