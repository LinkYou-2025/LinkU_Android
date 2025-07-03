package com.example.linku_android.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.design.theme.ThemeProvider
import com.example.design.theme.color.Basic
import com.example.linku_android.R

enum class NavigationItem(
    val title: String,
    @DrawableRes val icon: Int,
    val size: Size,
    val magnification: Float,
) {
    HOME(
        title = "홈",
        icon = R.drawable.ic_home,
        size = Size(26f, 26f),
        magnification = 1.2f,
    ),
    FILE(
        title = "파일",
        icon = R.drawable.ic_file,
        size = Size(27.85f, 26f),
        magnification = 1.2f,
    ),
    CURATION(
        title = "큐레이션",
        icon = R.drawable.ic_curation,
        size = Size(31f, 13.78f),
        magnification = 0.85f,
    ),
    MY_PAGE(
        title = "마이",
        icon = R.drawable.ic_mypage,
        size = Size(27.83f, 27.83f),
        magnification = 1.2f,
    )
}

val centerButtonSize = DpSize(19.2.dp, 19.2.dp)
val iconHeight = 24.dp

@Composable
fun NavigationBar(
    currentNavigationItem: NavigationItem?,
    onNavigate: (NavigationItem) -> Unit,
) {
    val density = LocalDensity.current
    val padding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    var maxHeight by remember { mutableStateOf<Dp?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0xFFEDEDED), // 상단 테두리 색상
                    start = Offset(0f, 0f), // 좌측 상단에서 시작
                    end = Offset(size.width, 0f), // 우측 상단까지 선을 그림
                    strokeWidth = 1.dp.toPx() // 테두리 두께
                )
            }
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = padding)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFEDEDED))
                    .width(16.dp)
                    .height(1.dp),
            )
            listOf(
                NavigationItem.HOME,
                NavigationItem.FILE,
                null,
                NavigationItem.CURATION,
                NavigationItem.MY_PAGE,
            ).forEach { item ->
                if (item != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(item) }
                            .let { m -> maxHeight?.let { m.height(it) } ?: m }
                            .onGloballyPositioned {
                                with(density) {
                                    val h = it.size.height.toDp()
                                    maxHeight = maxHeight?.let { max(it, h) } ?: h
                                }
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(
                                top = if (item == NavigationItem.CURATION) 27.56.dp else 21.dp,
                                bottom = 5.dp
                            ),
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(iconHeight * item.size.width / item.size.height * item.magnification)
                                    .height(iconHeight * item.magnification)
                                    .offset(y = if (item == NavigationItem.CURATION) (-2).dp else 0.dp)
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            if (currentNavigationItem == item) {
                                                drawRect(
                                                    brush = Basic.maincolor,
                                                    blendMode = BlendMode.SrcIn
                                                )
                                            }
                                        }
                                    },
                                tint = Color.Unspecified,
                            )
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.W400,
                                style = if (currentNavigationItem == item) {
                                    TextStyle(brush = Basic.maincolor)
                                } else {
                                    TextStyle(color = Color(0xFFCACACA))
                                },
                                modifier = Modifier.padding(top = 11.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .weight(1f)
                            .let { m -> maxHeight?.let { m.height(it) } ?: m }
                            .onGloballyPositioned {
                                with(density) {
                                    val h = it.size.height.toDp()
                                    maxHeight = maxHeight?.let { max(it, h) } ?: h
                                }
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(top = 21.dp, bottom = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(centerButtonSize.width)
                                    .height(centerButtonSize.height)
                            )
                            Text(
                                text = "",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.W400,
                                style = TextStyle(color = Color.Transparent),
                                modifier = Modifier.padding(top = 11.dp)
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFEDEDED))
                    .width(16.dp)
                    .height(1.dp),
            )
        }
    }
}

@Preview
@Composable
fun PreviewNavigationBar() {
    ThemeProvider {
        NavigationBar(
            currentNavigationItem = NavigationItem.HOME,
            onNavigate = {},
        )
    }
}