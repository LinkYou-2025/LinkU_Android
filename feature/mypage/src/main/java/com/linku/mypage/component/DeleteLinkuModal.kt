package com.linku.mypage.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.modal.ModalWindow
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic

/**
 * AI 요약이 포함된 링크를 삭제할 때 보여주는 확인 모달입니다.
 * Figma의 "AI 요약 링크 삭제" 변형(로고 없음)에 맞춰 [ModalWindow]를 `showLogo = false`로 사용합니다.
 */
@Composable
fun DeleteLinkuModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalWindow(
        visible = visible,
        onOkay = onConfirm,
        onDismiss = onDismiss,
        positiveText = "삭제하기",
        negativeText = "취소하기",
        title = "해당 링크를 삭제하시겠습니까?",
        showLogo = false
    ) {
        Text(
            text = "AI 요약이 포함된 링크입니다.\n삭제 시 링크와 요약 내용이 영구적으로 삭제됩니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteLinkuModal() {
    ThemeProvider {
        DeleteLinkuModal(
            visible = true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}