package com.example.linku_android

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.theme.ThemeProvider
import com.example.linku_android.component.LinkuNavigationBar
import com.example.linku_android.component.NavigationItem
import com.example.linku_android.component.centerButtonSize

data class NavigationBarProp(
    val currentNavigationItem: NavigationItem?,
    val onNavigate: (NavigationItem) -> Unit,
    val onCenterButtonClicked: () -> Unit,
)

data class CenterButtonProp(
    val onDismissed: () -> Unit,
)

private val centerButtonTopOffsetFromNavBarTopCenter = 12.dp
@Composable
fun MainScreen(
    navigationBarProp: NavigationBarProp?,
    centerButtonProp: CenterButtonProp?,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var navBarCenter by remember { mutableStateOf(Offset.Zero) }
    var navBarTopPx by remember { mutableStateOf(0f) }     // ⬅️ 바의 top 좌표
    var navBarSizePx by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val insets = WindowInsets.navigationBars
    val bottomInset = with(LocalDensity.current) { insets.getBottom(this).toDp() }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
        ),
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (navigationBarProp != null) {
                // 내비게이션 바의 위치/사이즈를 캡처
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        navBarTopPx = pos.y
                        navBarSizePx = androidx.compose.ui.geometry.Size(
                            coords.size.width.toFloat(),
                            coords.size.height.toFloat()
                        )
                        navBarCenter = with(density) {
                            Offset(
                                x = pos.x + coords.size.width / 2f,
                                y = pos.y + coords.size.height / 2f
                            )
                        }
                    }
                ) {
                    LinkuNavigationBar(
                        currentNavigationItem = navigationBarProp.currentNavigationItem,
                        onNavigate = navigationBarProp.onNavigate,
                    )
                }
            }
        }
        // FAB 슬롯은 비워둔다 (여기 쓰면 위로 떠버림)
    ) { innerPadding ->
        // 컨텐츠
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }

        // === 중앙 버튼: 오버레이로 정확히 배치 ===
        if (navigationBarProp != null) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { } // 오버레이 용도
            ) {
                IconButton(
                    onClick = centerButtonProp?.onDismissed ?: navigationBarProp.onCenterButtonClicked,
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .offset {
                            // IconButton의 좌상단 기준 오프셋이므로, 중앙 정렬을 위해 절반만큼 빼준다.
                            val btnW = with(density) { 57.6.dp.toPx() }
                            val btnH = with(density) { 48.dp.toPx() }
                            // navBarCenter.y 에서 약간 +y 해서 바 안쪽으로
                            val x = (navBarCenter.x - btnW / 2f).toInt()
                            val y = (navBarCenter.y - (btnH + bottomInset.toPx()) / 2f).toInt()
                            IntOffset(x, y)
                        }
                        .width(57.6.dp)
                        .height(48.dp)
                        .background(
                            LocalColorTheme.current.gray[100],
                            shape = RoundedCornerShape(14.dp)
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = null,
                        modifier = Modifier
                            .size(centerButtonSize.width, centerButtonSize.height)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    if (isPressed) {
                                        drawRect(
                                            brush = Basic.maincolor,
                                            blendMode = BlendMode.SrcIn
                                        )
                                    }
                                }
                            },
                        tint = Color.Unspecified,
                    )
                }
            }
        }
    }
}

//@Composable
//fun MainScreen(
//    navigationBarProp: NavigationBarProp?,
//    centerButtonProp: CenterButtonProp?,
//    content: @Composable () -> Unit,
//) {
//    val density = LocalDensity.current
//
//    var centerButtonCenter by remember { mutableStateOf(Offset.Zero) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // 화면
//            Box(
//                modifier = Modifier.weight(1f)
//            ) {
//                content()
//            }
//            // 내비게이션 바
//            if (navigationBarProp != null) Box(
//                modifier = Modifier.onGloballyPositioned {
//                    val offset = it.positionInParent()
//                    centerButtonCenter = with(density) {
//                        Offset(
//                            x = offset.x + it.size.width / 2,
//                            y = offset.y + it.size.height / 2
//                        )
//                    }
//                },
//            ) {
//                NavigationBar(
//                    currentNavigationItem = navigationBarProp.currentNavigationItem,
//                    onNavigate = navigationBarProp.onNavigate,
//                )
//            }
//        }
//        // 중앙 버튼
//        if (navigationBarProp != null) {
//            val interactionSource = remember { MutableInteractionSource() }
//            val isPressed by interactionSource.collectIsPressedAsState()
//
//            Box(
//                modifier = Modifier.offset {
//                    Offset(
//                        x = centerButtonCenter.x - centerButtonSize.width.toPx() - centerButtonSize.width.toPx() / 2,
//                        y = centerButtonCenter.y - centerButtonSize.height.toPx() / 2
//                    ).round()
//                },
//            ) {
//                IconButton(
//                    onClick = centerButtonProp?.onDismissed ?: navigationBarProp.onCenterButtonClicked,
//                    interactionSource = interactionSource,  // interactionSource를 적용해야 눌림 감지됨
//                    modifier = Modifier
//                        .width(57.6.dp)
//                        .height(48.dp)
//                        .offset(
//                            y = (7.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()) * -1
//                        ) // 바텀 네비게이션 안에 오도록 위치 조정
//                        .background(LocalColorTheme.current.gray[100], shape = RoundedCornerShape(14.dp)),
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_plus),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(centerButtonSize.width, centerButtonSize.height)
//                            .graphicsLayer(alpha = 0.99f) // BlendMode 사용을 위한 설정
//                            .drawWithCache {
//                                onDrawWithContent {
//                                    drawContent()
//                                    if (isPressed) {
//                                        drawRect(
//                                            brush = Basic.maincolor,
//                                            blendMode = BlendMode.SrcIn
//                                        )
//                                    }
//                                }
//                            },
//                        tint = Color.Unspecified, // 기본 tint 제거
//                    )
//                }
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    var currentNavigationItem by remember { mutableStateOf(NavigationItem.HOME) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    ThemeProvider {
        MainScreen(
            navigationBarProp = NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = { currentNavigationItem = it },
                onCenterButtonClicked = { isCenterButtonActivated = true },
            ),
            centerButtonProp = if (isCenterButtonActivated) CenterButtonProp(
                onDismissed = { isCenterButtonActivated = false },
            ) else null,
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "테스트")
            }
        }
    }
}