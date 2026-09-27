package com.linku.core.util

/** HTTP(S) 스킴이 없는 문자열 앞에 HTTPS 스킴을 붙입니다. */
fun ensureHttpScheme(raw: String): String =
    if (raw.startsWith("http://") || raw.startsWith("https://")) raw
    else "https://$raw"
