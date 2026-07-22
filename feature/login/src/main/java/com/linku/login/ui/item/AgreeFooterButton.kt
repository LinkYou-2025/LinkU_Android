package com.linku.login.ui.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linku.design.component.GradientButtonCore

/**
 * 서비스 내 모든 약관 동의 화면에서 공통으로 사용하는 하단 버튼
 */
@Composable
internal fun AgreeFooterButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    applyNavPadding: Boolean = true
) {
    GradientButtonCore(
        text = text,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(if (applyNavPadding) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 0.dp) // 화면 좌우 여백 20dp 고정
    )
}