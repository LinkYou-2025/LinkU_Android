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
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens


//약관 보고 다시 바텀 시트 돌아올 때,애니메이션 작동하지 않게 하는...시트
@Composable
fun NoAnimBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    scrimColor: Color = Color.Black.copy(alpha = 0.12f),
    shape: Shape,
    containerColor: Color = Color.White, //null 대신 white로 재변경.
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    // 현재 디자인 테마의 컬러 스킴 가져오기
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()

    // 파라미터로 받은 컬러가 없으면 테마의 white 사용
    val finalContainerColor = containerColor ?: colorTheme.white

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter // 중앙 정렬 - 테블릿 가로 모드 대응.
        ) {

        // Scrim(배경 어둡게)
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
                .widthIn(max = 600.dp) // 태블릿 가로 모드에서 무한정 늘어남 방지
                .fillMaxWidth()
                .wrapContentHeight()
                .imePadding(),
            shape = shape,
            color = finalContainerColor,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = h(12f)) // 세로 태딩 반응형 적용함.
            ) {
                content()
            }
        }
    }
}
