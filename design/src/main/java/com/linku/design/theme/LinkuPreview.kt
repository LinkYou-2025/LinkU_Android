package com.linku.design.theme

import androidx.compose.runtime.Composable

@Composable
fun LinkuPreview(content: @Composable () -> Unit) {
    ThemeProvider {
        content()
    }
}