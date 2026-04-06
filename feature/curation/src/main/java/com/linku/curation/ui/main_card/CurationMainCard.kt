package com.linku.curation.ui.main_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.linku.curation.R
import coil3.compose.SubcomposeAsyncImage
import com.linku.design.util.scaler
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
/**
 * 큐레이션 메인 카드
 *
 * - Backend 이미지 URL 표시
 * - imageUrl == null → 예시 이미지 표시
 * - 사이즈: 346 x 432 (scaler)
 */
@Composable
fun CurationMainCard(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(
                width = 346.scaler,
                height = 432.scaler
            )
            .clip(RoundedCornerShape(24.scaler))
            .background(Color(0xFFF2F2F2))
    ) {
        if (imageUrl.isNullOrBlank()) {
            // null / preview / fallback //ui 확인을 위해 일단 큐레이션 기본 이미지 보여주도록 수정함. 이후 기본 이미지 확정시 수정함.
            Image(
                painter = painterResource(id = R.drawable.img_curation_example),
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
    CurationMainCard(imageUrl = null)
}

