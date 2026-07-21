package com.linku

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.linku.component.LinkuNavigationBar
import com.linku.design.theme.ThemeProvider
import com.linku.design.util.EdgeToEdgeSystemBars
import com.linku.design.util.LocalStatusBarDarkIcons
import com.linku.navigation.LinkuNavigationItem

data class NavigationBarProp(
    val currentLinkuNavigationItem: LinkuNavigationItem?,
    val onNavigate: (LinkuNavigationItem) -> Unit,
    val onCenterButtonClicked: () -> Unit,
)

data class CenterButtonProp(
    val onDismissed: () -> Unit,
)

@Composable
fun MainScreen(
    navigationBarProp: NavigationBarProp?,
    centerButtonProp: CenterButtonProp?,
    onFABClick: () -> Unit,
    // 스플래시, 로그인 그라데이션 화면처럼 자체적으로 시스템 바를 다루는(edge-to-edge) 화면에서만
    // false로 넘김. 그 외 화면은 기본값(true) — 시스템 바 배경은 각 화면 콘텐츠가 그대로 확장되어
    // 비치게 두고(edge-to-edge), 아이콘 밝기와 표시 여부만 여기서 공통으로 맞춰줌.
    applyDefaultSystemBarIcons: Boolean = true,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var navBarCenter by remember { mutableStateOf(Offset.Zero) }
    var navBarTopPx by remember { mutableFloatStateOf(0f) }     // ⬅️ 바의 top 좌표
    var navBarSizePx by remember { mutableStateOf(Size.Zero) }

    // File 탭처럼 상태바 뒤로 어두운 배경(그라데이션 등)이 비치는 화면은 이 값을 직접
    // false로 바꿔서 아이콘을 흰색으로 전환함(LocalStatusBarDarkIcons로 하위에 제공).
    // MainApp까지 콜백을 relay할 필요 없이 화면이 바로 읽고 쓸 수 있음.
    val statusBarDarkIcons = remember { mutableStateOf(true) }

    // 시스템 바 "배경색"은 지정하지 않음 — 각 화면의 상단 색상(gray[100], 그라데이션 등)이
    // 상태바/내비게이션 바까지 자연스럽게 확장되어 보이게(edge-to-edge) 둠.
    // 아이콘 밝기와 바 표시만 공통으로 맞춤.
    if (applyDefaultSystemBarIcons) {
        EdgeToEdgeSystemBars(darkIcons = statusBarDarkIcons.value)
    }

    CompositionLocalProvider(LocalStatusBarDarkIcons provides statusBarDarkIcons) {
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
                        navBarSizePx = Size(
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
                        currentLinkuNavigationItem = navigationBarProp.currentLinkuNavigationItem,
                        onNavigate = navigationBarProp.onNavigate,
                        onFABClick = onFABClick
                    )
                }
            }
        }

    ) { innerPadding ->
        // 컨텐츠
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
    var currentLinkuNavigationItem by remember { mutableStateOf(LinkuNavigationItem.HOME) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    ThemeProvider {
        MainScreen(
            navigationBarProp = NavigationBarProp(
                currentLinkuNavigationItem = currentLinkuNavigationItem,
                onNavigate = { currentLinkuNavigationItem = it },
                onCenterButtonClicked = { isCenterButtonActivated = true },
            ),
            centerButtonProp = if (isCenterButtonActivated) CenterButtonProp(
                onDismissed = { isCenterButtonActivated = false },
            ) else null,
            {}
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "테스트")
            }
        }
    }
}