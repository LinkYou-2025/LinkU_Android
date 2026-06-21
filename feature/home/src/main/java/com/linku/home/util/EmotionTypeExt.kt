package com.linku.home.util

import androidx.annotation.DrawableRes
import com.linku.core.model.EmotionType
import com.linku.home.R

val EmotionType.imgRes: Int
    @DrawableRes get() = when (this) {
        EmotionType.JOY -> R.drawable.ic_joy
        EmotionType.CALM -> R.drawable.ic_calm
        EmotionType.EXCITE -> R.drawable.ic_excite
        EmotionType.SAD -> R.drawable.ic_sad
        EmotionType.IRRITATION -> R.drawable.ic_irritation
        EmotionType.ANGER -> R.drawable.ic_anger
    }