package com.example.curation.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curation.ui.recommend_list.CurationRecommendedLinksSection
import com.example.curation.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.curation.CurationViewModel
import com.example.curation.ui.list_card.LikedCurationCard
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.R as Res
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.curation.ui.main_card.CurationHighlightSection
import com.example.curation.ui.main_card.HighlightCurationCard
import com.example.curation.ui.top_bar.CurationTopBar
import com.example.design.top.search.SearchBarTopSheet
import com.example.core.model.RecommendedLink
import com.example.curation.ui.list_card.LikedCurationSkeleton
import com.example.curation.ui.recommend_list.RecommendedLinkCardSkeleton

// 간단 확장함수
private fun String.toLabel(): String = runCatching {
    val y = substring(0, 4).toInt()
    val m = substring(5, 7).toInt()
    "${y}년 ${m}월호"
}.getOrElse { this }

fun ensureHttpScheme(raw: String): String =
    if (raw.startsWith("http://") || raw.startsWith("https://")) raw
    else "https://$raw"

@Composable
fun CurationScreen(
    viewModel: CurationViewModel = hiltViewModel(),
    onOpenDetail: (Long, Long) -> Unit = { _, _ -> }
) {
    // 추가: TopSheet 표시 여부
    var showSearch by remember { mutableStateOf(false) }
    val uri = LocalUriHandler.current
    val nickname by viewModel.nickname.collectAsState()

    val userId by viewModel.userId.collectAsState(initial = -1L)
    val currentCurationId by viewModel.currentCurationId.collectAsState(initial = -1L)

    val canOpenDetail = userId > 0 && currentCurationId > 0

    LaunchedEffect(canOpenDetail) {
        if (canOpenDetail) {
            viewModel.loadHomeRecommendedLinksTop2(userId, currentCurationId)
        }
    }

    val homeLinksState by viewModel.homeLinks.collectAsState()


    //좋아요 리스트 상태 수집
    val likedItems by viewModel.likedCurations.collectAsState()
    val likedLoading by viewModel.likedLoading.collectAsState()

    // 현재 월을 "8월" 같은 형식으로 가져옴
    val currentMonth = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))
    }

    // 닉네임 불러오기
    LaunchedEffect(Unit) {
        viewModel.loadNickname()
    }

    Box(Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.white),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 홈스크린처럼 TopBar를 item {} 안에 배치
            item {
                //CurationTopBar()
                CurationTopBar(
                    onClickSearch = { viewModel.updateSearchTopSheetVisible(true) }
                )
            }
            // 현재 날짜에서 전달 구하기
            val prevMonthLabel = LocalDate.now()
                .minusMonths(1)
                .format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))

            item {
                // 제목 영역 (24dp)
                Text(
                    text = "${nickname}님을 위한 ${prevMonthLabel}의 큐레이션",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 19.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 하이라이트 카드 (좌우 20dp)
                CurationHighlightSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    viewModel = viewModel,
                    onOpenDetail = { onOpenDetail(userId, currentCurationId) }
                )

                Spacer(modifier = Modifier.height(25.dp))
            }



            // 2. 추천 링크
            // 내부에서 padding 제거하고 modifier로 전달
            item {

                Text(
                    text = "추천 링크",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 24.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
                when {
                    //로딩 시 스켈레톤 + 쉬머
                    homeLinksState.loading && homeLinksState.items.isEmpty() -> {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            repeat(2) {
                                RecommendedLinkCardSkeleton()
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                    else -> {
                        CurationRecommendedLinksSection(
                            modifier = Modifier.fillMaxWidth(),
                            links = homeLinksState.items,
                            loading = homeLinksState.loading,
                            onRetry = {
                                if (canOpenDetail) {
                                    viewModel.loadHomeRecommendedLinksTop2(
                                        userId,
                                        currentCurationId
                                    )
                                }
                            },
                            onClick = { url -> runCatching { uri.openUri(url) } }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp)) //이거 간격이 큰데

                // 3. 좋아요한 큐레이션 제목
                Column(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 0.dp
                    )
                ) {
                    Text(
                        text = "${nickname}님이 좋아요한 큐레이션",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = Paperlogy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = LocalColorTheme.current.black
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }




            item {
                // 어댑터로 변환 (title은 고정문구/월 라벨 포맷)
                val uiItems = likedItems.map {
                    UICurationItem(
                        title = "링큐 큐레이션",
                        date = it.month.toLabel(),      // "2025-07" -> "2025년 7월호"
                        imageUrl = it.thumbnailUrl,
                        liked = true
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {


                    when {
                        likedLoading && uiItems.isEmpty() -> {
                            Spacer(Modifier.height(10.dp))
                            repeat(3) {
                                LikedCurationSkeleton()        //스켈레톤 + 쉬머 적용함.
                                Spacer(Modifier.height(10.dp))
                            }
                        }

                        uiItems.isEmpty() -> {
                            LikedCurationEmptyState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }

                        else -> {
                            uiItems.forEachIndexed { idx, item ->
                                val domain = likedItems.getOrNull(idx)

                                LikedCurationCard(
                                    item = item,
                                    onCardClick = {
                                        // 카드 탭 → 상세
                                        if (userId > 0 && domain != null) {
                                            onOpenDetail(userId, domain.id)
                                        }
                                    },
                                    onHeartClick = {
                                        likedItems.getOrNull(idx)?.let { domain ->
                                            viewModel.unlikeFromLikedList(domain.id)   // 서버 취소 + 낙관적 제거
                                        }
                                    }
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }


        // 검색창 탑 시트
        SearchBarTopSheet(
            visible = viewModel.searchTopSheetVisible,
            onLinkClick = {},
            onDismiss = { viewModel.updateSearchTopSheetVisible(false) },
            onQueryChange = { viewModel.fastSearch(it) },
            onQuerySave = { viewModel.addRecentQuery(it) },
            onQueryDelete = { viewModel.removeRecentQuery(it) },
            onQueryClear = { viewModel.clearRecentQuery() },
            fastSearchItems = viewModel.fastSearchItems.collectAsState().value,
            recentQuerys = viewModel.recentQueryList.collectAsState().value.map{it.text}
        )
    }



//좋아요한 큐레이션이 없는 경우 보여지는 화면.
@Composable
fun LikedCurationEmptyState(
    modifier: Modifier = Modifier,
    emptyIconRes: Int = R.drawable.img_curation_liked_null // 점선+링크가 합쳐진 PNG
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),                 // 필요시 조절
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이콘(점선 박스 포함) 크기 고정
        Image(
            painter = painterResource(id = emptyIconRes),
            contentDescription = null,
            modifier = Modifier.size(82.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "아직 좋아요한 큐레이션이 아직 없어요!",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = LocalColorTheme.current.black
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "좋아요를 눌러보러 갈까요?",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFF87898F)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LinksPreparingHome(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(60.dp), // 필요시 조절
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(Modifier.height(12.dp))

        // 설명
        Text(
            "AI가 한달 감정&상황 데이터를 바탕으로\n추천 링크를 준비하고 있어요",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFF87898F)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))

        // 안내 문구
        Text(
            "＊ 검증 과정으로 인해 추천 링크 수가 제한될 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFFFF5E5E)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390, heightDp = 2000)
@Composable
fun PreviewCurationScreenExact() {

    val previewLinks = listOf(
        RecommendedLink(
            isInternal = true,
            userLinkuId = 1L,
            title = "성신여대 홈페이지",
            url = "https://sungshin.ac.kr",
            imageUrl = null,
            domain = "sungshin.ac.kr",
            domainImageUrl = null,
            categories = listOf("기타")
        ),
        RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "나만의 자기관리 체크리스트 만들기 - Adobe",
            url = "https://adobe.com",
            imageUrl = null,
            domain = "adobe.com",
            domainImageUrl = null,
            categories = listOf("정보")
        )
    )

    val previewLiked = listOf(
        UICurationItem(
            title = "링큐 큐레이션",
            date = "2025년 7월호",
            imageUrl = null,
            liked = true
        ),
        UICurationItem(
            title = "링큐 큐레이션",
            date = "2025년 6월호",
            imageUrl = null,
            liked = true
        )
    )

    val uri = LocalUriHandler.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = maxWidth

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.white),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { CurationTopBar() }

            item { Spacer(Modifier.height(19.dp)) }

            item {
                Text(
                    text = "세나님을 위한 11월의 큐레이션",
                    fontFamily = Paperlogy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            item {
                HighlightCurationCard(
                    imageUrl = null,
                    title = "링큐 큐레이션",
                    date = "2025년 11월호",
                    liked = true,
                    likeBusy = false,
                    modifier = Modifier
                        .width(width)
                        .padding(horizontal = 20.dp)
                )
            }

            item { Spacer(Modifier.height(25.dp)) }

            item {

                Text(
                    text = "추천 링크",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 24.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))


                CurationRecommendedLinksSection(
                    modifier = Modifier.fillMaxWidth(),
                    links = previewLinks,
                    loading = false,
                    onRetry = {},
                    onClick = { url -> runCatching { uri.openUri(url) } }
                )
            }

            item { Spacer(Modifier.height(25.dp)) }

            item {
                Text(
                    text = "세나님이 좋아요한 큐레이션",
                    fontFamily = Paperlogy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp)
                )
            }

            item { Spacer(Modifier.height(18.dp)) }

            items(previewLiked) { item ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LikedCurationCard(
                        item = item,
                        onCardClick = {},
                        onHeartClick = {}
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}