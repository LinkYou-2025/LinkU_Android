package com.linku.curation.ui.chip

import androidx.constraintlayout.compose.ConstraintSet

val constraints = ConstraintSet {

    // ==========================================================
    // ConstraintLayout 참조 객체
    // ==========================================================
    val chip1 = createRefFor("chip1")
    val chip2 = createRefFor("chip2")
    val chip3 = createRefFor("chip3")
    val chip4 = createRefFor("chip4")
    val chip5 = createRefFor("chip5")
    val chip6 = createRefFor("chip6")
    val chip7 = createRefFor("chip7")
    val chip8 = createRefFor("chip8")
    val chip9 = createRefFor("chip9")


    // ==========================================================
    // 피그마 기준 칩 중심 좌표를 백분율(상대 좌표)로 계산하여
    // Guideline으로 정의한다.
    // ==========================================================
    val x1 = createGuidelineFromStart(0.41f)
    val y1 = createGuidelineFromTop(0.42f)

    val x2 = createGuidelineFromStart(0.78f)
    val y2 = createGuidelineFromTop(0.36f)

    val x3 = createGuidelineFromStart(0.17f)
    val y3 = createGuidelineFromTop(0.59f)

    val x4 = createGuidelineFromStart(0.77f)
    val y4 = createGuidelineFromTop(0.5f)

    val x5 = createGuidelineFromStart(0.23f)
    val y5 = createGuidelineFromTop(0.28f)

    val x6 = createGuidelineFromStart(0.695f)
    val y6 = createGuidelineFromTop(0.166f)

    val x7 = createGuidelineFromStart(0.656f)
    val y7 = createGuidelineFromTop(0.671f)

    val x8 = createGuidelineFromStart(0.6f)
    val y8 = createGuidelineFromTop(0.3f)

    val x9 = createGuidelineFromStart(0.334f)
    val y9 = createGuidelineFromTop(0.544f)


    // ==========================================================
    // Guideline을 기준으로 각 칩을 배치한다.
    // ==========================================================
    constrain(chip1) {
        start.linkTo(x1)
        end.linkTo(x1)
        top.linkTo(y1)
        bottom.linkTo(y1)
    }

    constrain(chip2) {
        start.linkTo(x2)
        end.linkTo(x2)
        top.linkTo(y2)
        bottom.linkTo(y2)
    }

    constrain(chip3) {
        start.linkTo(x3)
        end.linkTo(x3)
        top.linkTo(y3)
        bottom.linkTo(y3)
    }

    constrain(chip4) {
        start.linkTo(x4)
        end.linkTo(x4)
        top.linkTo(y4)
        bottom.linkTo(y4)
    }

    constrain(chip5) {
        start.linkTo(x5)
        end.linkTo(x5)
        top.linkTo(y5)
        bottom.linkTo(y5)
    }

    constrain(chip6) {
        start.linkTo(x6)
        end.linkTo(x6)
        top.linkTo(y6)
        bottom.linkTo(y6)
    }

    constrain(chip7) {
        start.linkTo(x7)
        end.linkTo(x7)
        top.linkTo(y7)
        bottom.linkTo(y7)
    }

    constrain(chip8) {
        start.linkTo(x8)
        end.linkTo(x8)
        top.linkTo(y8)
        bottom.linkTo(y8)
    }

    constrain(chip9) {
        start.linkTo(x9)
        end.linkTo(x9)
        top.linkTo(y9)
        bottom.linkTo(y9)
    }
}