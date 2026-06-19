package com.linku.core.model

import androidx.annotation.DrawableRes
import com.linku.design.R

enum class EmotionType(
    val id: Long,
    val tagName: String,
    @DrawableRes val imgRes: Int
) {
    JOY(
        id = 1L,
        tagName = "즐거움",
        imgRes = R.drawable.ic_joy
    ),
    CALM(
        id = 2L,
        tagName = "평온",
        imgRes = R.drawable.ic_calm
    ),
    EXCITE(
        id = 3L,
        tagName = "설렘",
        imgRes = R.drawable.ic_excite
    ),
    SAD(
        id = 4L,
        tagName = "슬픔",
        imgRes = R.drawable.ic_sad
    ),
    IRRITATION(
        id = 5L,
        tagName = "짜증",
        imgRes = R.drawable.ic_irritation
    ),
    ANGER(
        id = 6L,
        tagName = "분노",
        imgRes = R.drawable.ic_anger
    );

    companion object {
        fun fromId(id: Long?): EmotionType? {
            return entries.firstOrNull { it.id == id }
        }

        fun fromTagName(tagName: String?): EmotionType? {
            return entries.firstOrNull { it.tagName == tagName }
        }

        fun tagNameOf(id: Long?): String? {
            return fromId(id)?.tagName
        }

        fun idOf(tagName: String?): Long? {
            return fromTagName(tagName)?.id
        }
    }
}