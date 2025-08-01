package com.example.core.model

enum class EmotionType(
    val id: Long,
    val tagName: String
) {
    JOY(1, "즐거움"),
    PEACE(2, "평온"),
    EXCITEMENT(3, "설렘"),
    SADNESS(4, "슬픔"),
    ANNOYANCE(5, "짜증"),
    ANGER(6, "분노");

    companion object {
        fun fromId(id: Long): EmotionType? = values().find { it.id == id }
    }
}