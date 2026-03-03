package com.example.login.ui.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 서비스 내 모든 약관 동의 화면에서 공통으로 사용하는 하단 버튼
 */
@Composable
fun AgreeFooterButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    applyNavPadding: Boolean = true
) {
    // 디자인 가이드에 정의된 고정 그라데이션 색상
    val activeBrush = Brush.horizontalGradient(
        listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
    )
    val inactiveBrush = Brush.horizontalGradient(
        listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
    )

    GradientButtonCore(
        text = text,
        enabled = enabled,
        activeGradient = activeBrush,
        inactiveGradient = inactiveBrush,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(if (applyNavPadding) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 0.dp) // 화면 좌우 여백 20dp 고정
    )
}