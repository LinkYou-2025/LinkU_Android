package com.example.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.theme.font.Paperlogy

enum class NavigationItem(
    val title: String,
    @DrawableRes val icon: Int,
) {
    HOME(
        title = "홈",
        icon = R.drawable.ic_home
    ),
    FILE(
        title = "파일",
        icon = R.drawable.ic_file
    ),
    CURATION(
        title = "큐레이션",
        icon = R.drawable.ic_curation
    ),
    MY_PAGE(
        title = "마이",
        icon = R.drawable.ic_mypage
    )
}

@Composable
fun BottomNavigationBar(
    selectedTab: NavigationItem,
    onTabSelected: (NavigationItem) -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = Color(0xFFEDEDED), // 상단 테두리 색상
                        start = Offset(0f, 0f), // 좌측 상단에서 시작
                        end = Offset(size.width, 0f), // 우측 상단까지 선을 그림
                        strokeWidth = 1.dp.toPx() // 테두리 두께
                    )
                },
            containerColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // ✅ 내부 아이콘들만 좌우 패딩 적용
                horizontalArrangement = Arrangement.SpaceBetween // ✅ 균등 분배
            ) {
                listOf(
                    NavigationItem.HOME,
                    NavigationItem.FILE,
                    null,
                    NavigationItem.CURATION,
                    NavigationItem.MY_PAGE,
                ).forEach { item ->
                    item?.let {
                        NavigationBarItem(
                            selected = selectedTab == item,
                            onClick = { onTabSelected(item) },
                            icon = {
                                Icon(
                                    painterResource(id = item.icon),
                                    contentDescription = "홈",
                                    modifier = Modifier
                                        .width(26.dp)
                                        .height(26.dp)
                                        .graphicsLayer(alpha = 0.99f)  // BlendMode를 적용하기 위해 사용
                                        .drawWithCache {
                                            onDrawWithContent {
                                                drawContent()

                                                if (selectedTab == item) {
                                                    drawRect(
                                                        brush = Basic.maincolor, // Gradient
                                                        blendMode = BlendMode.SrcIn
                                                    )
                                                }
                                            }
                                        },
                                    tint = Color.Unspecified
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontFamily = Paperlogy.font,
                                    fontWeight = FontWeight.Normal,
                                    style = if (selectedTab == item) {
                                        TextStyle(brush = Basic.maincolor)
                                    } else {
                                        TextStyle(color = LocalColorTheme.current.gray[400])
                                    }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent // ✅ 클릭 시 보라색 배경 제거!
                            ),
                            interactionSource = null, // ✅ 클릭 효과 감추기
                        )
                    } ?: run {
                        Spacer(modifier = Modifier.weight(1f)) // ✅ 플로팅 버튼 자리 확보
                    }
                }
            }
        }

        // ✅ 플로팅 버튼 추가
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .width(57.6.dp)
                    .height(48.dp)
                    .offset(y = (25.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()) * -1) // 바텀 네비게이션에 걸치도록 위치 조정
                    .background(LocalColorTheme.current.gray[100], shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "추가 버튼",
                    modifier = Modifier
                        .width(19.2.dp)
                        .height(19.2.dp)
                        .clickable(
                            indication = null, // ✅ 클릭 시 회색 박스(터치 피드백) 제거
                            interactionSource = remember { MutableInteractionSource() } // ✅ 클릭 효과 감추기
                        ) {
                            onFabClick()
                        },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomNavigationBar() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.fillMaxSize())
        }
        BottomNavigationBar(
            selectedTab = NavigationItem.HOME,
            onTabSelected = {},
            onFabClick = {}
        )
    }
}