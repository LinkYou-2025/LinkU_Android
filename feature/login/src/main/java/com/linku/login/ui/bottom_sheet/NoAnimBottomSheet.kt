package com.linku.login.ui.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler


@Composable
fun NoAnimBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    val colorTheme = MaterialTheme.linkuColors
    val containerColor = colorTheme.white

    Box(
        // Surface의 shadowElevation이 콘텐츠 영역(시스템 내비게이션 바 바로 위) 밖으로
        // 번져서, 그 아래 시스템 내비게이션 바 자리에 회색 그라데이션처럼 비치는 걸 막음.
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds(),
        contentAlignment = Alignment.BottomCenter
    ) {

        // Scrim(배경 어둡게)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .noRippleClickable { onDismissRequest() }
        )

        // BottomSheet body (NO animation)
        Surface(
            modifier = Modifier
                .widthIn(max = 600.dp) // 태블릿 가로 모드에서 무한정 늘어남 방지
                .fillMaxWidth()
                .wrapContentHeight()
                .imePadding(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = containerColor,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.scaler) // 세로 태딩 반응형 적용함.
            ) {
                content()
            }
        }
    }
}
