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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.linkuColors
import com.linku.home.R
import com.linku.home.model.ClipboardLinkCandidate
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 클립보드에서 http/https 링크와 해당 복사 시각을 함께 감지합니다.
 *
 * 최초 진입, 클립보드 변경 알림, 앱 복귀 및 창 포커스 획득 시점에 같은 `ClipData`에서 URL과 timestamp를 함께 읽습니다.
 * 따라서 앱이 종료된 동안 유지된 기존 항목과 동일 URL을 다시 복사한 새 항목을 구분할 수 있습니다.
 *
 * @param schemes 클립보드 링크 후보로 허용할 URL 스킴 목록
 */
@Composable
fun rememberClipboardLinkCandidate(
    schemes: List<String> = listOf("https://", "http://"),
): State<ClipboardLinkCandidate?> {
    val context = LocalContext.current
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused

    val clipboard =
        remember(context) { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    val candidateState = remember { mutableStateOf<ClipboardLinkCandidate?>(null) }

    /** 현재 primary clip의 URL과 복사 시각을 하나의 스냅샷으로 읽습니다. */
    fun readClipboardCandidate(): ClipboardLinkCandidate? {
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount <= 0) return null

        val url = clip.getItemAt(0)
            .coerceToText(context)
            ?.toString()
            ?.trim()
            .orEmpty()

        if (url.isEmpty() || schemes.none { url.startsWith(it, ignoreCase = true) }) {
            return null
        }

        return ClipboardLinkCandidate(
            url = url,
            copiedAtMillis = clip.description.timestamp,
        )
    }

    DisposableEffect(clipboard, schemes) {
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            candidateState.value = readClipboardCandidate()
        }
        clipboard.addPrimaryClipChangedListener(listener)

        // 리스너 등록 직후 읽어 등록 전후에 바뀐 클립보드 항목도 놓치지 않습니다.
        candidateState.value = readClipboardCandidate()

        onDispose {
            clipboard.removePrimaryClipChangedListener(listener)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // 포커스가 있을 때만 읽어 접근 제한으로 반환된 null을 빈 클립보드로 오인하지 않습니다.
        if (isWindowFocused) {
            candidateState.value = readClipboardCandidate()
        }
    }

    LaunchedEffect(isWindowFocused) {
        // Android 10 이상에서는 창 포커스를 획득한 뒤에만 클립보드를 정상적으로 읽을 수 있습니다.
        if (isWindowFocused) {
            candidateState.value = readClipboardCandidate()
        }
    }

    return candidateState
}

/**
 * 상단 배너 UI:
 * - link가 있을 때 노출
 * - 링크는 안내 문구를 제외한 가용 너비 안에서 말줄임표로 표시
 * - 안내 문구는 항상 한 줄 전체를 표시
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
    val colors = MaterialTheme.linkuColors
    val scope = rememberCoroutineScope()

    // 드래그 오프셋(px)
    var offsetYPx by remember { mutableStateOf(0f) }
    val dismissThresholdPx = with(density) { 30.dp.toPx() } // 이 이상 아래로 끌면 닫기

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(colors.maincolor)
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
                    .padding(horizontal = 21.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = link.trim(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.white,
                    fontFamily = LocalFontTheme.current.font,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(
                        weight = 1f,
                        fill = false,
                    ),
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.clipboard_link_paste_prompt),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.white,
                    fontFamily = LocalFontTheme.current.font,
                    maxLines = 1,
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
            link = "https://blog.naver.com/linku/a-very-long-link-that-needs-ellipsis",
            onDismiss = {},
            onPasteClick = {}
        )
    }
}
