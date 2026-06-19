package com.linku.core.model.auth

interface SelectionItem {
    val displayName: String
    val serverKey: String
}

inline fun <reified T> selectionItemOfServerKey(serverKey: String): T?
        where T : Enum<T>, T : SelectionItem {
    return runCatching { enumValueOf<T>(serverKey) }.getOrNull()
}

inline fun <reified T> selectionItemOfDisplayName(displayName: String): T?
        where T : Enum<T>, T : SelectionItem {
    return enumValues<T>().firstOrNull { it.displayName == displayName }
}
