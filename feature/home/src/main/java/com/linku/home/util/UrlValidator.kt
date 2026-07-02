package com.linku.home.util

private val urlRegex = Regex(
    pattern = """https?://[^\s]+|www\.[^\s]+""",
    option = RegexOption.IGNORE_CASE
)

fun containsMultipleLinks(text: String): Boolean {
    return urlRegex.findAll(text).count() >= 2
}