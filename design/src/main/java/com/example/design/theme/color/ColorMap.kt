package com.example.design.theme.color

import androidx.compose.ui.graphics.Color

class ColorMap {
    private val map: Map<Int, Color>

    constructor(vararg entries: Pair<Int, Color>) {
        map = entries.toMap()
    }

    operator fun get(key: Int) = map[key] ?: Color.Unspecified
}