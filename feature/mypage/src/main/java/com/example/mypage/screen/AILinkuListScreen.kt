package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.ThemeProvider
import com.example.mypage.R
import com.example.mypage.component.AILinkuItem
import com.example.design.R as Res

data class DummyAILinku(
    val id: Int,
    val title: String,
    val tags: List<String>,
    val domainImage: Int?,
    val domainName: String,
    val category: String
)

private val previewDummyLinks = listOf(
    DummyAILinku(
        id = 1,
        title = "요즘 대학생들이 진짜 쓰는 앱 TOP 10 정리",
        tags = listOf("생산성·툴", "트렌드"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "자기계발"
    ),
    DummyAILinku(
        id = 2,
        title = "신입 개발자 포트폴리오에서 꼭 보여줘야 하는 것들",
        tags = listOf("포트폴리오", "개발"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "개발"
    ),
    DummyAILinku(
        id = 3,
        title = "디자인 전공생이 참고하면 좋은 UX 리서치 방법",
        tags = listOf("UX", "디자인"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "디자인"
    ),
    DummyAILinku(
        id = 4,
        title = "요즘 대학생들이 진짜 쓰는 앱 TOP 10 정리",
        tags = listOf("생산성·툴", "트렌드"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "자기계발"
    ),
    DummyAILinku(
        id = 5,
        title = "신입 개발자 포트폴리오에서 꼭 보여줘야 하는 것들",
        tags = listOf("포트폴리오", "개발"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "개발"
    ),
    DummyAILinku(
        id = 6,
        title = "디자인 전공생이 참고하면 좋은 UX 리서치 방법",
        tags = listOf("UX", "디자인"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "디자인"
    ),
    DummyAILinku(
        id = 7,
        title = "요즘 대학생들이 진짜 쓰는 앱 TOP 10 정리",
        tags = listOf("생산성·툴", "트렌드"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "자기계발"
    ),
    DummyAILinku(
        id = 8,
        title = "신입 개발자 포트폴리오에서 꼭 보여줘야 하는 것들",
        tags = listOf("포트폴리오", "개발"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "개발"
    ),
    DummyAILinku(
        id = 9,
        title = "디자인 전공생이 참고하면 좋은 UX 리서치 방법",
        tags = listOf("UX", "디자인"),
        domainImage = Res.drawable.ic_domain_blog_naver_logo,
        domainName = "BLOG",
        category = "디자인"
    ),
)

@Composable
fun AILinkuListScreen(
    navController: NavController,
    initialLinks: List<DummyAILinku> = previewDummyLinks
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf("취업", "자기계발", "디자인", "개발", "마케팅")

    val dummyLinks = remember(initialLinks) {
        mutableStateListOf<DummyAILinku>().apply {
            addAll(initialLinks)
        }
    }

    val filteredLinks = if (selectedCategory == null) {
        dummyLinks
    } else {
        dummyLinks.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 58.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(10.dp)
                    .noRippleClickable { navController.popBackStack() }
            )

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_sparkle),
                    contentDescription = null,
                    modifier = Modifier
                        .width(16.42.dp)
                        .height(17.51.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "AI 요약 링크",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.49.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                text = "전체",
                selected = selectedCategory == null,
                onClick = { selectedCategory = null }
            )

            CategoryDropdownChip(
                text = selectedCategory ?: "카테고리",
                expanded = isCategoryMenuExpanded,
                onClick = { isCategoryMenuExpanded = true },
                onDismiss = { isCategoryMenuExpanded = false },
                categories = categories,
                onCategorySelected = {
                    selectedCategory = it
                    isCategoryMenuExpanded = false
                }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 링크 리스트
        if (filteredLinks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_empty_ailinku),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "아직 생성된 AI 요약 링크가 없어요.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[800]
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "링크를 저장하고 AI 요약을 생성해보세요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[600]
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = filteredLinks,
                    key = { _, item -> item.id }
                ) { index, link ->
                    AILinkuItem(
                        linkTitle = link.title,
                        tags = link.tags,
                        domainImage = link.domainImage,
                        domainName = link.domainName,
                        onClickDelete = {
                            dummyLinks.remove(link)
                        }
                    )

                    if (index == filteredLinks.lastIndex) {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) LocalColorTheme.current.black else LocalColorTheme.current.white,
                shape = RoundedCornerShape(10.dp)
            )
            .noRippleClickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) LocalColorTheme.current.white else LocalColorTheme.current.gray[800]
        )
    }
}

@Composable
private fun CategoryDropdownChip(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .noRippleClickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = LocalColorTheme.current.white)
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.gray[200],
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalColorTheme.current.gray[800]
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(R.drawable.ic_arrow_down_gray500),
                contentDescription = null,
                modifier = Modifier.height(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(11.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier
                .width(180.dp)
                .background(LocalColorTheme.current.white)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.white)
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Column {
                    categories.forEachIndexed { index, category ->
                        val dotColor = when (index) {  // TODO: 실제 카테고리별 색상으로 교체
                            0 -> Color(0xFF7FD1C7)
                            1 -> Color(0xFFF6C64B)
                            2 -> Color(0xFF69AEF0)
                            3 -> Color(0xFFE85B52)
                            4 -> Color(0xFF8BD03C)
                            else -> Color(0xFFD9DCE3)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable { onCategorySelected(category) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = category,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.gray[800]
                            )
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAILinkuListScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        AILinkuListScreen(
            navController = navController,
            initialLinks = previewDummyLinks
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAILinkuEmptyScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        AILinkuListScreen(
            navController = navController,
            initialLinks = emptyList()
        )
    }
}