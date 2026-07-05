package com.linku.core.model

enum class EmotionId(val value: Long) {
    JOY(1L),
    CALM(2L),
    EXCITE(3L),
    SAD(4L),
    IRRITATION(5L),
    ANGER(6L);

    companion object {
        fun fromValue(value: Long?): EmotionId? {
            if (value == null) return null

            return entries.firstOrNull { it.value == value }
        }
    }
}

enum class EmotionType(
    val id: EmotionId,
    val tagName: String
) {
    JOY(EmotionId.JOY, "즐거움"),
    CALM(EmotionId.CALM, "평온"),
    EXCITE(EmotionId.EXCITE, "설렘"),
    SAD(EmotionId.SAD, "슬픔"),
    IRRITATION(EmotionId.IRRITATION, "짜증"),
    ANGER(EmotionId.ANGER, "분노");

    val value: Long
        get() = id.value

    companion object {
        fun fromValue(value: Long?): EmotionType? {
            val emotionId = EmotionId.fromValue(value)

            return entries.firstOrNull { it.id == emotionId }
        }

        // 태그명으로 찾는 게 필요하다면 fromTagName 사용
//        fun fromTagName(tagName: String?): EmotionType? {
//            if (tagName == null) return null
//
//            return entries.firstOrNull { it.tagName == tagName }
//        }
    }
}