package com.linku.core.util.caller

fun getCaller(): StackTraceElement = Throwable().stackTrace[1]
