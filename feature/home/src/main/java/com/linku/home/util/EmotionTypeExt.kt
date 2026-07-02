package com.linku.home.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.core.model.EmotionType
import com.linku.home.R

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