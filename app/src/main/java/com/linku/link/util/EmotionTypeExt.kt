package com.linku.link.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.R
import com.linku.core.model.EmotionType

val EmotionType.imgRes: Painter
    @Composable get() = painterResource(
        id = when (this) {
            EmotionType.JOY -> R.drawable.ic_joy
            EmotionType.CALM -> R.drawable.ic_calm
            EmotionType.EXCITE -> R.drawable.ic_excite
            EmotionType.SAD -> R.drawable.ic_sad
            EmotionType.IRRITATION -> R.drawable.ic_irritation
            EmotionType.ANGER -> R.drawable.ic_anger
        }
    )

/**
 * 흰색 외곽선이 없는 감정 아이콘을 반환합니다.
 */
val EmotionType.imgResNoBorder: Painter
    @Composable get() = painterResource(
        id = when (this) {
            EmotionType.JOY -> R.drawable.ic_joy_noborder
            EmotionType.CALM -> R.drawable.ic_calm_noborder
            EmotionType.EXCITE -> R.drawable.ic_excite_noborder
            EmotionType.SAD -> R.drawable.ic_sad_noborder
            EmotionType.IRRITATION -> R.drawable.ic_irritation_noborder
            EmotionType.ANGER -> R.drawable.ic_anger_noborder
        }
    )
