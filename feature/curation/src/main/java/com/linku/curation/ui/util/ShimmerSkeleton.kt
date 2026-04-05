package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.linku.curation.ui.recommend_list.SkeletonEnd
import com.linku.curation.ui.recommend_list.SkeletonStart

@Composable
fun ShimmerSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmer()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SkeletonStart,
                        SkeletonEnd
                    )
                )
            )
    )
}