package com.example.login.ui.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


//약관 보고 다시 바텀 시트 돌아올 때,애니메이션 작동하지 않게 하는...시트
@Composable
fun NoAnimBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    scrimColor: Color = Color.Black.copy(alpha = 0.12f),
    shape: Shape,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    Box(modifier = Modifier.fillMaxSize()) {

        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismissRequest()
                }
        )

        // BottomSheet body (NO animation)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .imePadding(),
            shape = shape,
            color = containerColor,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                content()
            }
        }
    }
}
