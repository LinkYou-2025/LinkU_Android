package com.linku.core.model.auth

enum class Gender {
    NONE, MALE, FEMALE;

    companion object {
        fun fromApiValue(apiValue: String?): Gender =
            runCatching { valueOf(apiValue.orEmpty().uppercase()) }.getOrDefault(NONE)
    }
}