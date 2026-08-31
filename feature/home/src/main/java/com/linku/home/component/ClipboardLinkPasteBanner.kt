package com.linku.home.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.linkuColors
import com.linku.home.R
import kotlin.math.roundToInt

/** 사용자가 복사한 URL을 링크 저장 화면에 붙여넣도록 안내하는 홈 하단 말풍선입니다. */
@Composable
fun ClipboardLinkPasteBanner(
    link: String,
    onDismiss: () -> Unit,
    onPasteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val dismissDragThresholdPx = with(LocalDensity.current) { 30.dp.toPx() }
    var offsetYPx by remember(link) { mutableFloatStateOf(0f) }

    AnimatedVisibility(
        visible = link.isNotBlank(),
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = offsetYPx.roundToInt()) }
                .fillMaxWidth()
                .drawBehind {
                    val shadowColor = Color(0xFF8447DF).copy(alpha = 0.35f).toArgb()
                    val blurPx = 10.dp.toPx()
                    val spreadPx = 4.dp.toPx()
                    val radiusPx = 50.dp.toPx()

                    drawIntoCanvas { canvas ->
                        val shadowPaint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(blurPx, 0f, 4.dp.toPx(), shadowColor)
                        }
                        canvas.nativeCanvas.drawRoundRect(
                            -spreadPx,
                            -spreadPx,
                            size.width + spreadPx,
                            size.height + spreadPx,
                            radiusPx,
                            radiusPx,
                            shadowPaint,
                        )
                    }
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 50))
                    .background(colors.maincolor)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            offsetYPx = (offsetYPx + delta).coerceAtLeast(0f)
                        },
                        onDragStopped = {
                            if (offsetYPx >= dismissDragThresholdPx) {
                                onDismiss()
                            }
                            offsetYPx = 0f
                        },
                    )
                    .clickable(onClick = onPasteClick)
                    .padding(horizontal = 21.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = link.trim(),
                    modifier = Modifier.weight(weight = 1f, fill = false),
                    color = colors.white,
                    fontFamily = LocalFontTheme.current.font,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.clipboard_link_paste_prompt),
                    color = colors.white,
                    fontFamily = LocalFontTheme.current.font,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
            }
        }
    }
}
