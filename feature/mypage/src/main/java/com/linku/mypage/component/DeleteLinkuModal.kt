package com.linku.mypage.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.modal.ModalWindow
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.mypage.R

/**
 * AI 요약이 포함된 링크를 삭제할 때 보여주는 확인 모달입니다.
 * Figma의 "AI 요약 링크 삭제" 변형(로고 없음)에 맞춰 [ModalWindow]를 `showLogo = false`로 사용합니다.
 *
 * @param visible 삭제 확인 모달의 표시 여부
 * @param onDismiss 외부 영역이나 취소 버튼 또는 확인 처리 직후 모달을 닫을 때 실행할 콜백.
 * 순수 UI 상태 정리만 수행해야 합니다.
 * @param onConfirm 삭제 확인 버튼을 눌렀을 때 실행할 콜백
 */
@Composable
fun DeleteLinkuModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalWindow(
        visible = visible,
        onOkay = onConfirm,
        onDismiss = onDismiss,
        positiveText = stringResource(R.string.ai_linku_delete_confirm),
        negativeText = stringResource(R.string.ai_linku_delete_cancel),
        title = stringResource(R.string.ai_linku_delete_title),
        showLogo = false,
    ) {
        Text(
            text = stringResource(R.string.ai_linku_delete_message),
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
