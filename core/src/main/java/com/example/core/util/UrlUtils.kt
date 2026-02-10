package com.example.core.util

fun ensureHttpScheme(raw: String): String =
    if (raw.startsWith("http://") || raw.startsWith("https://")) raw
    else "https://$raw"