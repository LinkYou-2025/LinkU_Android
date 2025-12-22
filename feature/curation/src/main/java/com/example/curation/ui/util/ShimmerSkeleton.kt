package com.example.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.curation.ui.recommend_list.SkeletonEnd
import com.example.curation.ui.recommend_list.SkeletonStart

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