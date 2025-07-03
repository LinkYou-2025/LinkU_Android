package com.example.linku_android

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.theme.ThemeProvider
import com.example.linku_android.component.NavigationBar
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

    var centerButtonCenter by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 화면
            Box(
                modifier = Modifier.weight(1f)
            ) {
                content()
            }
            // 내비게이션 바
            if (navigationBarProp != null) Box(
                modifier = Modifier.onGloballyPositioned {
                    val offset = it.positionInParent()
                    centerButtonCenter = with(density) {
                        Offset(
                            x = offset.x + it.size.width / 2,
                            y = offset.y + it.size.height / 2
                        )
                    }
                },
            ) {
                NavigationBar(
                    currentNavigationItem = navigationBarProp.currentNavigationItem,
                    onNavigate = navigationBarProp.onNavigate,
                )
            }
        }
        // 중앙 버튼
        if (navigationBarProp != null) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier.offset {
                    Offset(
                        x = centerButtonCenter.x - centerButtonSize.width.toPx() - centerButtonSize.width.toPx() / 2,
                        y = centerButtonCenter.y - centerButtonSize.height.toPx() / 2
                    ).round()
                },
            ) {
                IconButton(
                    onClick = centerButtonProp?.onDismissed ?: navigationBarProp.onCenterButtonClicked,
                    interactionSource = interactionSource,  // interactionSource를 적용해야 눌림 감지됨
                    modifier = Modifier
                        .width(57.6.dp)
                        .height(48.dp)
                        .offset(
                            y = (7.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()) * -1
                        ) // 바텀 네비게이션 안에 오도록 위치 조정
                        .background(LocalColorTheme.current.gray[100], shape = RoundedCornerShape(14.dp)),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = null,
                        modifier = Modifier
                            .size(centerButtonSize.width, centerButtonSize.height)
                            .graphicsLayer(alpha = 0.99f) // BlendMode 사용을 위한 설정
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
                        tint = Color.Unspecified, // 기본 tint 제거
                    )
                }
            }
        }
    }
}

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