package com.linku

import androidx.compose.foundation.background
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
import com.linku.design.util.LocalScaffoldBackgroundReporter
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
    // 스플래시, 로그인 그라데이션 화면처럼 몰입형(전체 화면)으로 보여야 하는 화면에서만
    // true로 넘김. 그 외 화면은 기본값(false) — 시스템 바가 항상 보이되, 아이콘 밝기만
    // 여기서 공통으로 맞춰줌. 시스템 바 표시/숨김 제어는 이 한 곳(EdgeToEdgeSystemBars)으로
    // 통일함 — 예전엔 SystemBarController가 화면별로 별도 호출되면서 같은 Window를 서로 다른
    // 타이밍에 건드려 경합이 있었음(로그아웃/탈퇴 직후 시스템 바가 다시 보이던 버그).
    hideSystemBars: Boolean = false,
    // 내비게이션 바만 별도로 숨길지 여부. 기본값은 hideSystemBars와 동일해서 대부분의 화면은
    // 상태바/내비게이션 바를 함께 숨기지만, 약관 동의 바텀시트처럼 상태바는 숨긴 배경을
    // 유지하면서도 내비게이션 바는 항상 보여야 하는 화면에서 따로 지정할 수 있게 분리함.
    hideNavigationBar: Boolean = hideSystemBars,
    statusBarDarkIcons: Boolean = true,
    navigationBarDarkIcons: Boolean? = null,
    dimmed: Boolean = false,
    // 검색 탑 시트처럼 하단 내비게이션 바까지 덮으며 화면 전체 위에 떠야 하는 오버레이.
    // Scaffold 콘텐츠 영역(content) 안에서 그리면 하단 바 뒤까지 덮이지 않아서, outer Box
    // 레벨(dimmed와 같은 레벨)에서 별도로 그림. app 모듈이 소유한 검색 상태를 그대로 넘겨받아
    // 그리기만 하므로 이 화면은 검색이 무엇인지 몰라도 됨.
    searchOverlay: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var navBarCenter by remember { mutableStateOf(Offset.Zero) }
    var navBarTopPx by remember { mutableFloatStateOf(0f) }     // ⬅️ 바의 top 좌표
    var navBarSizePx by remember { mutableStateOf(Size.Zero) }

    // 활성 route가 상태바 기본값을 소유합니다. route 기본값이 바뀌면 새 상태 객체를 만들어
    // 이전 화면의 DisposableEffect.onDispose가 현재 화면의 아이콘 정책을 덮어쓰지 못하게 합니다.
    // 하위 화면은 로딩/다이얼로그 같은 일시적인 상태에서만 이 값을 override합니다.
    val statusBarDarkIconsState = remember(statusBarDarkIcons) {
        mutableStateOf(statusBarDarkIcons)
    }

    // Scaffold 바깥(하단 시스템 바 뒤)에 비치는 배경색. 커스텀 바텀바가 없는 화면에서
    // 시스템 내비게이션 바 뒤 여백이 화면 배경색과 다르게(기본 흰색) 보이는 걸 막기 위한 상태.
    // content() 내부 화면들이 ReportScaffoldBackground(LocalScaffoldBackgroundReporter)로
    // 스스로 보고하며, 그 화면을 벗어나면 자동으로 흰색으로 되돌아감.
    var containerColor by remember { mutableStateOf(Color.White) }

    // 시스템 바 "배경색"은 지정하지 않음 — 각 화면의 상단 색상(gray[100], 그라데이션 등)이
    // 상태바/내비게이션 바까지 자연스럽게 확장되어 보이게(edge-to-edge) 둠.
    // 아이콘 밝기와 바 표시/숨김을 여기서 공통으로 맞춤.
    EdgeToEdgeSystemBars(
        statusBarDarkIcons = statusBarDarkIconsState.value,
        // 별도 정책이 없는 화면은 기존처럼 상태바의 일시적 아이콘 변경을 함께 따릅니다.
        navigationBarDarkIcons = navigationBarDarkIcons ?: statusBarDarkIconsState.value,
        hidden = hideSystemBars,
        hideNavigationBar = hideNavigationBar,
    )

    CompositionLocalProvider(
        LocalStatusBarDarkIcons provides statusBarDarkIconsState,
        LocalScaffoldBackgroundReporter provides { containerColor = it }
    ) {
        // 박스로 감싼 이유는 추후 토스트/alert 등을 화면 전체 위에 띄우기 쉽게 하기 위함입니다.
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                ),
                modifier = Modifier.fillMaxSize(),
                containerColor = containerColor,
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

            if (dimmed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }

            searchOverlay()
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
