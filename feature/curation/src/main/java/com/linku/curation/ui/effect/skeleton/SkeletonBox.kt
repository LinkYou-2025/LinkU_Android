package com.linku.curation.ui.effect.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    shimmerBrush: Brush
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush)
    )
}

/**
 * 사용법
 * 카드 스켈레톤(라운드가 있다면?) + 일반 스켈레톤
 * SkeletonBox(
 *     modifier = Modifier
 *         .size(346.scaler, 432.scaler),
 *     shape = RoundedCornerShape(24.scaler),
 *     shimmerBrush = grayShimmerBrush()
 * )
 *
 *
 * 핑크 쉬머(큐레이션 디테일 화면에 사용?)
 * SkeletonBox(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .height(180.scaler),
 *     shape = RoundedCornerShape(20.scaler),
 *     shimmerBrush = pinkShimmerBrush()
 * )
 *
 *
 * //텍스트 라인
 * SkeletonBox(
 *     modifier = Modifier
 *         .height(14.scaler)
 *         .fillMaxWidth(0.6f),
 *     shape = RoundedCornerShape(4.scaler),
 *     shimmerBrush = grayShimmerBrush()
 * )
 *
 *
 *
 *
 * */