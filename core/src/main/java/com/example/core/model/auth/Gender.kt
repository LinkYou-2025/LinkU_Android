package com.example.core.model.auth

enum class Gender(val value: Int) {
    NONE(0),
    MALE(1),
    FEMALE(2);

    companion object {
        fun fromValue(value: Int): Gender =
            entries.find { it.value == value } ?: NONE
    }
}