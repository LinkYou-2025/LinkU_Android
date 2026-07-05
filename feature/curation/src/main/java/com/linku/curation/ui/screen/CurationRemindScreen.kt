package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//TODO : 다음 PR에서 구현할거예요....
@Composable
fun CurationRemindScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    Box(modifier = Modifier.fillMaxSize())
}
