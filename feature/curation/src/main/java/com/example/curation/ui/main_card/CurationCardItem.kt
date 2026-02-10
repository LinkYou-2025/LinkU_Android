package com.example.curation.ui.main_card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.SubcomposeAsyncImage
import com.example.curation.R
import com.example.design.util.scaler

// CurationMainCardPager.kt
// private 키워드 제거!
@Composable
fun CurationCardItem(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    @DrawableRes fallbackImage: Int = R.drawable.img_curation_example // 테스트용 기본값
) {
    val resolvedImageUrl = imageUrl
        ?.takeIf { it.isNotBlank() && it != "null" }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.scaler))
            .background(Color(0xFFF2F2F2))
    ) {
        if (resolvedImageUrl == null) {
            Image(
                painter = painterResource(id = fallbackImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            SubcomposeAsyncImage(
                model = resolvedImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}