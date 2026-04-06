package com.linku.home.component

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 클립보드에서 http/https 로 시작하는 텍스트만 감지해서 State로 제공
 */
@Composable
fun rememberClipboardUrl(
    schemes: List<String> = listOf("https://", "http://")
): State<String?> {
    val context = LocalContext.current
    val clipboard =
        remember(context) { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    val urlState = remember { mutableStateOf<String?>(null) }

    fun readClipboardText(): String? {
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount <= 0) return null

        val text = clip.getItemAt(0)
            .coerceToText(context)
            ?.toString()
            ?.trim()

        return text
    }

    fun toUrlOrNull(raw: String?): String? {
        val v = raw?.trim().orEmpty()
        if (v.isEmpty()) return null
        return if (schemes.any { v.startsWith(it, ignoreCase = true) }) v else null
    }

    DisposableEffect(clipboard) {
        // 최초 1회 세팅
        urlState.value = toUrlOrNull(readClipboardText())

        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            urlState.value = toUrlOrNull(readClipboardText())
        }
        clipboard.addPrimaryClipChangedListener(listener)

        onDispose {
            clipboard.removePrimaryClipChangedListener(listener)
        }
    }

    return urlState
}

/**
 * 상단 배너 UI:
 * - link가 있을 때 노출
 * - 아래로 드래그(일정 거리 이상)하면 onDismiss 호출
 * - 탭하면 onPasteClick 호출(원하는 동작 연결)
 */
@Composable
fun ClipboardLinkPasteBanner(
    visible: Boolean,
    link: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onPasteClick: (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // 드래그 오프셋(px)
    var offsetYPx by remember { mutableStateOf(0f) }
    val dismissThresholdPx = with(density) { 30.dp.toPx() } // 이 이상 아래로 끌면 닫기

    // 표시용 링크 26자 제한(넘치면 ... 으로 표시)
    val displayLink = remember(link) {
        val trimmed = link.trim()
        if (trimmed.length <= 26) trimmed else trimmed.take(26) + "..."
    }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = modifier
                .offset { IntOffset(0, offsetYPx.roundToInt()) }
                .fillMaxWidth()
                .drawBehind {
                    // Figma: X 0, Y 4, Blur 10, Spread 4, Color #8447DF @ 35%
                    val shadowColor = Color(0xFF8447DF).copy(alpha = 0.35f).toArgb()
                    val dx = 0.dp.toPx()
                    val dy = 4.dp.toPx()
                    val blurPx = 10.dp.toPx()
                    val spreadPx = 4.dp.toPx()
                    val radiusPx = 50.dp.toPx()

                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        val fp = paint.asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(blurPx, dx, dy, shadowColor)
                        }

                        // spread 만큼 도형 자체 크기 확장
                        val left = -spreadPx
                        val top = -spreadPx
                        val right = size.width + spreadPx
                        val bottom = size.height + spreadPx

                        canvas.nativeCanvas.drawRoundRect(
                            left, top, right, bottom,
                            radiusPx, radiusPx,
                            fp
                        )
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(LocalColorTheme.current.maincolor)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            offsetYPx = (offsetYPx + delta).coerceAtLeast(0f)
                        },
                        onDragStopped = {
                            if (offsetYPx >= dismissThresholdPx) {
                                offsetYPx = 0f
                                onDismiss()
                            } else {
                                scope.launch { offsetYPx = 0f }
                            }
                        }
                    )
                    .clickable(enabled = onPasteClick != null) { onPasteClick?.invoke() }
                    .padding(horizontal = 21.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$displayLink 링크를 붙여넣을까요?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.white,
                    fontFamily = LocalFontTheme.current.font,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Clipboard Banner - Visible", showBackground = true)
@Composable
private fun PreviewClipboardLinkPasteBannerVisible() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F6)) // 배경 회색(예시)
    ) {
        ClipboardLinkPasteBanner(
            visible = true,
            link = "https://blog.naver.com/linku/",
            onDismiss = {},
            onPasteClick = {}
        )
    }
}
