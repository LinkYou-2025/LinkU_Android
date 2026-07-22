package com.linku.core.util.logging

import com.linku.core.BuildConfig

object LinkuLog {
    @PublishedApi
    internal inline fun debugOnly(block: () -> Unit) {
        if (BuildConfig.DEBUG) {
            block()
        }
    }

    @PublishedApi
    internal val StackTraceElement.getTag: String
        get() = fileName

    @PublishedApi
    internal inline fun StackTraceElement.getMsg(msg: () -> String) = "$methodName ${msg()}"
}