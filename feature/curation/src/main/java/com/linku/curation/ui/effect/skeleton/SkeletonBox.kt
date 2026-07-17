package com.linku.curation.ui.effect.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * 쉬머 애니메이션이 적용된 스켈레톤 로딩 플레이스홀더 Box.
 *
 * 현우 왕자님 사용법
 *
 * 카드 스켈레톤(라운드)
 * ``` kotlin
 * SkeletonBox(
 *     modifier = Modifier.size(346.scaler, 432.scaler),
 *     shape = RoundedCornerShape(24.scaler)
 * )
 * ```
 *
 * 원형 스켈레톤(프로필 등)
  * ``` kotlin
 * SkeletonBox(
 *     modifier = Modifier.size(48.scaler),
 *     shape = CircleShape
 * )
 * ```
 *
 * 핑크 쉬머(큐레이션 디테일 화면)
 * ``` kotlin
 * SkeletonBox(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .height(180.scaler),
 *     shape = RoundedCornerShape(20.scaler),
 *     colors = MaterialTheme.pinkShimmerColors
 * )
 * ```
 *
 * 텍스트 라인
 * ``` kotlin
 * SkeletonBox(
 *     modifier = Modifier
 *         .height(14.scaler)
 *         .fillMaxWidth(0.6f),
 *     shape = RoundedCornerShape(4.scaler)
 * )
 
 *
 * @param shape 스켈레톤 영역의 모양 (원형, 라운드, 사각형 등 자유롭게 지정)
 * @param colors 쉬머 그라데이션 색상 목록 (기본값: [MaterialTheme.grayShimmerColors])
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    colors: List<Color> = MaterialTheme.grayShimmerColors
) {
    Box(modifier = modifier.shimmer(shape = shape, colors = colors))
}
