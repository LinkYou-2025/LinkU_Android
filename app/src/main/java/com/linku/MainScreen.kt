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
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var navBarCenter by remember { mutableStateOf(Offset.Zero) }
    var navBarTopPx by remember { mutableFloatStateOf(0f) }     // ⬅️ 바의 top 좌표
    var navBarSizePx by remember { mutableStateOf(Size.Zero) }

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