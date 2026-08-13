package com.linku.curation.ui.effect.skeleton

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linku.curation.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.util.scaler

/**
 * [com.linku.curation.ui.header.CurationTopHeader] 레이아웃을 그대로 따라 만든 shimmer 스켈레톤 플레이스홀더.
 *
 * 로딩 중에도 백버튼은 실제 아이콘으로 유지해 뒤로가기가 가능하게 한다.
 *
 * @param onBack 백버튼 클릭 콜백
 * @param contentTopOffset 트윙클+텍스트 영역의 상단 여백 ([CurationTopHeader]와 동일한 값을 전달)
 */
@Composable
internal fun CurationTopHeaderSkeleton(
    onBack: () -> Unit,
    contentTopOffset: Dp,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // 백버튼 — 로딩 중에도 실제 아이콘으로 유지
        Image(
            painter = painterResource(id = R.drawable.ic_curation_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 59.scaler, start = 20.scaler)
                .noRippleClickable(onClick = onBack)
                .width(10.scaler)
                .height(16.25f.scaler)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = contentTopOffset, start = 20.scaler, end = 20.scaler)
        ) {
            Spacer(modifier = Modifier.height(18.49f.scaler))

            // 제목 1줄
            SkeletonBox(
                modifier = Modifier
                    .height(22.scaler)
                    .fillMaxWidth(0.55f),
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(6.scaler))

            // 제목 2줄
            SkeletonBox(
                modifier = Modifier
                    .height(22.scaler)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(12.scaler))

            // 설명
            SkeletonBox(
                modifier = Modifier
                    .height(16.scaler)
                    .fillMaxWidth(0.45f),
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}
