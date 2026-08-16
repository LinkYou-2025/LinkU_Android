package com.linku.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linku.design.component.SkeletonBox
import com.linku.design.theme.linkuColors

/**
 * 홈의 [com.linku.design.component.LinkCardItem] 배치를 본뜬 로딩용 카드입니다.
 *
 * 카드 외곽과 내부 간격은 실제 링크 카드와 동일하게 유지하고, 이미지·제목·태그·도메인 영역만
 * 스켈레톤으로 표시해 로딩 완료 시 레이아웃 변화가 작도록 구성합니다.
 *
 * @param modifier 카드의 배치와 외부 간격을 지정하는 modifier
 */
@Composable
internal fun HomeLinkCardSkeleton(
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.white,
                shape = RoundedCornerShape(18.dp),
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBox(
            modifier = Modifier.size(85.dp),
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(16.dp),
            )

            Spacer(modifier = Modifier.height(9.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonBox(
                    modifier = Modifier.size(width = 64.dp, height = 20.dp),
                    shape = RoundedCornerShape(6.dp),
                )

                Spacer(modifier = Modifier.width(6.dp))

                SkeletonBox(
                    modifier = Modifier.size(width = 48.dp, height = 20.dp),
                    shape = RoundedCornerShape(6.dp),
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonBox(
                    modifier = Modifier.size(22.dp),
                    shape = CircleShape,
                )

                Spacer(modifier = Modifier.width(6.dp))

                SkeletonBox(
                    modifier = Modifier.size(width = 64.dp, height = 12.dp),
                )
            }
        }

        Box(
            modifier = Modifier.size(width = 22.dp, height = 85.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            SkeletonBox(
                modifier = Modifier
                    .padding(top = 10.dp, end = 5.dp)
                    .size(width = 12.dp, height = 4.dp),
                shape = RoundedCornerShape(2.dp),
            )
        }
    }
}
