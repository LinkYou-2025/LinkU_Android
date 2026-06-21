package com.linku.core.model

enum class EmotionType(
    val id: Long,
    val tagName: String
) {
    JOY(id = 1L, tagName = "즐거움"),
    CALM(id = 2L, tagName = "평온"),
    EXCITE(id = 3L, tagName = "설렘"),
    SAD(id = 4L, tagName = "슬픔"),
    IRRITATION(id = 5L, tagName = "짜증"),
    ANGER(id = 6L, tagName = "분노");

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