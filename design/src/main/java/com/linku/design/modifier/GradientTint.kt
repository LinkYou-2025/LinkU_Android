package com.linku.design.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Modifier에 그라데이션 효과를 적용하는 확장 함수입니다.
 *
 * 이 함수는 [graphicsLayer]와 [drawWithCache]를 사용하여 콘텐츠 위에 지정된 [brush]를 덧그립니다.
 * 기본적으로 [BlendMode.SrcIn]을 사용하여 이미지나 아이콘의 형태에 맞춰 그라데이션 색상을 입히는 데 유용합니다.
 *
 * @param brush 콘텐츠에 적용할 그라데이션 [Brush].
 * @param blendMode 그라데이션을 콘텐츠와 결합할 때 사용할 [BlendMode]. 기본값은 [BlendMode.SrcIn]입니다.
 *
 * @return 그라데이션 틴트가 적용된 [Modifier].
 */
fun Modifier.gradientTint(
    /*
    * brush: 그라데이션 색상을 적용할 브러시
    * blendMode: 그라데이션 색상을 적용할 때 사용할 블렌드 모드
    * */
    brush: Brush,
    blendMode: BlendMode = BlendMode.SrcIn
): Modifier = this.graphicsLayer(alpha = 0.99f)
    .drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = brush,
                blendMode = blendMode
            )
        }
    }