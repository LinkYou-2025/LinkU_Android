package com.example.linku_android.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.ThemeProvider
import com.example.design.theme.color.Basic

val centerButtonSize = DpSize(19.2.dp, 19.2.dp)
val iconHeight = 24.dp

@Composable
fun LinkuNavigationBar(
    currentLinkuNavigationItem: LinkuNavigationItem?,
    onNavigate: (LinkuNavigationItem) -> Unit,
    applySystemBottomInset: Boolean = true,
    onFABClick: () -> Unit,
) {
    val bottomPadding =
        if (applySystemBottomInset) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        else 0.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 상단 보더
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDEDED))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
                .padding(horizontal = 8.dp)
                // 가장 큰 자식 높이에 Row 전체 높이를 맞춰 모든 칸이 동일 높이
                .height(IntrinsicSize.Min)
        ) {
            // 좌/우 여백이 필요하면 weight 없이 Spacer로 간단히 처리
            // Spacer(modifier = Modifier.width(8.dp))

            // 가운데는 FAB 자리(null)로 비워두는 구성 유지
            val items = listOf(
                LinkuNavigationItem.HOME,
                LinkuNavigationItem.FILE,
                null, // ⬅️ FAB 자리
                LinkuNavigationItem.CURATION,
                LinkuNavigationItem.MY_PAGE
            )

            items.forEach { item ->
                when (item) {
                    null -> CenterHole(
                        onClicK = { onFABClick() }
                    )
                    else -> NavItem(
                        item = item,
                        selected = currentLinkuNavigationItem == item,
                        onClick = { onNavigate(item) }
                    )
                }
            }

            // Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun RowScope.NavItem(
    item: LinkuNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // CURATION만 약간의 시각 보정이 있었던 부분을 파라미터로 명시
    val topPadding = if (item == LinkuNavigationItem.CURATION) 27.56.dp else 21.dp
    val iconYOffset = if (item == LinkuNavigationItem.CURATION) (-2).dp else 0.dp

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()              // Row의 IntrinsicSize.Min과 맞물려 동일 높이 보장
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = topPadding, bottom = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .width(iconHeight * item.size.width / item.size.height * item.magnification)
                    .height(iconHeight * item.magnification)
                    .offset(y = iconYOffset)
                    .graphicsLayer(alpha = 0.99f) // 캐시 최적화 트릭 유지
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            if (selected) {
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
                fontFamily = LocalFontTheme.current.font,
                style = if (selected) TextStyle(brush = Basic.maincolor)
                else TextStyle(color = Color(0xFFCACACA)),
                modifier = Modifier.padding(top = 11.dp)
            )
        }
    }
}

@Composable
private fun RowScope.CenterHole(
    onClicK: () -> Unit = {},
) {
    // FAB가 덮어쓸 빈 공간. 기존 centerButtonSize를 그대로 사용
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        LinkuNavigationBarFAB{ onClicK() }
    }
}

@Preview
@Composable
private fun PreviewNavigationBar() {
    ThemeProvider {
        LinkuNavigationBar(
            currentLinkuNavigationItem = LinkuNavigationItem.HOME,
            onNavigate = {},
            onFABClick = {}
        )
    }
}