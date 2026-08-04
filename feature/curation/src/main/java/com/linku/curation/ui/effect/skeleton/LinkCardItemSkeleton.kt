package com.linku.curation.ui.effect.skeleton

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.linku.design.theme.linkuColors

/** [com.linku.design.component.LinkCardItem] 레이아웃을 그대로 따라 만든 shimmer 스켈레톤 플레이스홀더 */
@Composable
internal fun LinkCardItemSkeleton() {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 썸네일
            SkeletonBox(
                modifier = Modifier.size(85.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 제목 1줄
                SkeletonBox(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.75f),
                    shape = RoundedCornerShape(4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 제목 2줄
                SkeletonBox(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 태그 칩
                SkeletonBox(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.35f),
                    shape = RoundedCornerShape(6.dp)
                )

                Spacer(modifier = Modifier.height(9.dp))

                // 도메인 행
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBox(
                        modifier = Modifier.size(22.dp),
                        shape = CircleShape
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    SkeletonBox(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
        }
    }
}
