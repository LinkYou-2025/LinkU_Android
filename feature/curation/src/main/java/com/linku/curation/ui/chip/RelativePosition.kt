package com.linku.curation.ui.chip

import androidx.constraintlayout.compose.ConstraintSet

/**
 * 레이아웃 내에서 특정 칩(Chip)의 상대적인 공간 좌표를 나타내는 데이터 클래스입니다.
 *
 * 이 클래스는 [ConstraintSet] 내에서 가이드라인(Guideline)을 생성하고,
 * 칩의 중심을 해당 위치에 고정하기 위한 좌표 데이터를 담고 있습니다.
 *
 * @property id 칩의 고유 식별자. 레이아웃 참조(Reference)를 생성할 때 사용됩니다.
 * @property x 가로 방향의 상대적 위치 (0.0 ~ 1.0 사이의 값).
 * @property y 세로 방향의 상대적 위치 (0.0 ~ 1.0 사이의 값).
 */
data class ChipPosition(
    val id: String,
    val x: Float,
    val y: Float,
)

/**
 * UI 칩 배치를 위한 미리 정의된 상대 좌표 리스트입니다.
 *
 * 각 [ChipPosition]은 고유 식별자와 정규화된(0.0 ~ 1.0) 가로 및 세로 좌표를 가지며,
 * 이 값들은 [ConstraintSet] 내에서 가이드라인을 생성하여 칩의 위치를 결정하는 데 사용됩니다.
 */
private val chipPositions = listOf(
    ChipPosition("chip1", 0.41f, 0.42f),
    ChipPosition("chip2", 0.78f, 0.36f),
    ChipPosition("chip3", 0.17f, 0.59f),
    ChipPosition("chip4", 0.77f, 0.5f),
    ChipPosition("chip5", 0.23f, 0.28f),
    ChipPosition("chip6", 0.695f, 0.166f),
    ChipPosition("chip7", 0.656f, 0.671f),
    ChipPosition("chip8", 0.6f, 0.3f),
    ChipPosition("chip9", 0.334f, 0.544f),
)

/**
 * [chipPositions]에 정의된 좌표 데이터를 기반으로 각 칩의 레이아웃 제약 조건을 생성하는 [ConstraintSet]입니다.
 *
 * 각 [ChipPosition]의 `x`와 `y` 값을 사용하여 가로 및 세로 가이드라인을 생성하고,
 * 칩의 상하좌우를 해당 가이드라인에 연결함으로써 칩의 중심이 지정된 상대적 위치(0.0 ~ 1.0)에
 * 정확히 배치되도록 구성합니다.
 */
val constraints = ConstraintSet {
    chipPositions.forEach { position ->
        val chip = createRefFor(position.id)
        val x = createGuidelineFromStart(position.x)
        val y = createGuidelineFromTop(position.y)

        constrain(chip) {
            start.linkTo(x)
            end.linkTo(x)
            top.linkTo(y)
            bottom.linkTo(y)
        }
    }
}
