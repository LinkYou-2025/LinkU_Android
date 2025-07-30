package com.example.data.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toOffsetDateTimeInKorea() = atOffset(ZoneId.of("Asia/Seoul").rules.getOffset(Instant.now()))