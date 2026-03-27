package com.linku.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
* 피그마 레이아웃 기준으로 반응형 ui를 작업할 수 있는 공통 유틸 파일입니다.
* 유지민 팀장 확인 받음.
* */

private const val FIGMA_WIDTH = 412f // private 분리
private const val FIGMA_HEIGHT = 917f


// 계산한 스케일 함수 담음.
val LocalFigmaDimens = staticCompositionLocalOf<(Float) -> Dp> {
    { it.dp } // 기본값
}

// 확장
val Int.scaler: Dp @Composable get() = LocalFigmaDimens.current(this.toFloat())
val Float.scaler: Dp @Composable get() = LocalFigmaDimens.current(this)

@Composable
fun rememberFigmaDimens(): (Float) -> Dp {
    val config = LocalConfiguration.current

    return remember(config.screenWidthDp, config.screenHeightDp) {


        // 어떤 방향이든 짧은 쪽을 너비, 긴 쪽을 높이로 간주
        val actualWidth = config.screenWidthDp.toFloat() //현재 기기
        val actualHeight = config.screenHeightDp.toFloat() //현재 기기

        // 가로 모드를 세로처럼 취급
        val portraitWidth = minOf(actualWidth, actualHeight) //minOf가 type 더 안전.
        val portraitHeight = maxOf(actualWidth, actualHeight)

        // 가로 세로 스케일 중 더 작은 값을 공통 스케일로 사용함.
        val scale = minOf(
            portraitWidth / FIGMA_WIDTH,
            portraitHeight / FIGMA_HEIGHT
        )

        // 단일 스케일 함수 반환
        return@remember { px: Float -> (px * scale).dp }
    }
}


//가로 세로 따로 스케일링
@Composable
fun rememberFigmaDimensSeparate(): Pair<(Float) -> Dp, (Float) -> Dp> {
    val config = LocalConfiguration.current

    // 화면 크기가 바뀔 때, 다시 계산
    return remember(config.screenWidthDp, config.screenHeightDp) {
        val screenWidth = config.screenWidthDp.toFloat()
        val screenHeight = config.screenHeightDp.toFloat()

        val w: (Float) -> Dp = { px ->
            (px * (screenWidth / FIGMA_WIDTH)).dp
        }

        val h: (Float) -> Dp = { px ->
            (px * (screenHeight / FIGMA_HEIGHT)).dp
        }

        w to h
    }
}