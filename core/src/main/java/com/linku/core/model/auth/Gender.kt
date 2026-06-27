package com.linku.core.model.auth

enum class Gender(val value: String) {
    NONE("NONE"),
    MALE("MALE"),
    FEMALE("FEMALE");

    // 서버에서 받은 String을 이넘으로 받아오는 로직, 마이페이지에서 사용할 수 있기에 추가함.
    companion object {
        fun fromValue(value: String?): Gender =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: NONE
    }
}