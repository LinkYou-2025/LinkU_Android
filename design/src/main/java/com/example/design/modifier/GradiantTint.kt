package com.example.design.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer

/*
* 그라데이션 색상을 적용하는 Modifier 확장 함수
* 기존의 이미지 위에 블렌드 모드에 따라 색을 덮는 방식
* */
fun Modifier.gradiantTint(
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