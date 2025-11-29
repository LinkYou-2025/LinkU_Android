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
import com.example.curation.CurationRecommendedLinksSection
import com.example.curation.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.curation.LikedCurationCard
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
import com.example.design.top.search.SearchBarTopSheet

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

    // 네가 가진 링크 데이터 → 검색 대상 리스트로 변환
//    val allFastLinks = remember(homeLinksState.items) {
//        homeLinksState.items.map { link ->
//            FastSearchItem(
//                title = link.title,
//                url   = ensureHttpScheme(link.url)
//            )
//        }
//    }



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
                Column(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 19.dp,

                    )
                )  {
                    // 1. 큐레이션 하이라이트 텍스트
                    Text(
                        text = "${nickname}님을 위한 ${prevMonthLabel}의 큐레이션",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = Paperlogy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CurationHighlightSection(
                        modifier = Modifier.fillMaxWidth(),
                        viewModel = viewModel,   // 같은 그래프-스코프 VM 전달
                        onOpenDetail = { onOpenDetail(userId, currentCurationId) }
                    )
                    Spacer(modifier = Modifier.height(25.dp))


                    // 2. 추천 링크
                    // 내부에서 padding 제거하고 modifier로 전달
                    when {
                        homeLinksState.loading && homeLinksState.items.isEmpty() -> {
                            // 로딩 중 + 아직 결과 없음 → 준비중 UI
                            LinksPreparingHome(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        else -> {
                            CurationRecommendedLinksSection(
                                modifier = Modifier.fillMaxWidth(),
                                links = homeLinksState.items,          // 🔗 API 결과(top2)
                                loading = homeLinksState.loading,      // 섹션 내부 스켈레톤/상태에 사용
                                onRetry = {
                                    if (canOpenDetail) {
                                        viewModel.loadHomeRecommendedLinksTop2(userId, currentCurationId)
                                    }
                                },
                                onClick = { url -> runCatching { uri.openUri(url) } }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // 3. 좋아요한 큐레이션 텍스트
                    Column(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 0.dp)
                    ) {
                        Text(
                            text = "${nickname}님을 위한 ${prevMonthLabel}의 큐레이션",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Paperlogy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = LocalColorTheme.current.black
                        )
                    }

                    Spacer(modifier = Modifier.height(0.dp))
                }
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
                            // 간단 플레이스홀더
                            Spacer(Modifier.height(10.dp))
                            repeat(2) { Spacer(Modifier.height(150.dp).fillMaxWidth()) }
                        }
                        uiItems.isEmpty() -> {
                            LikedCurationEmptyState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }
                        else ->

                        {
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
}

@Composable
fun CurationTopBar(
    onClickSearch: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColorTheme.current.white)
    ) {
        // 🔹 상단 로고 + 알림 아이콘
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.38.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = Res.drawable.ic_linkukor),
                contentDescription = "링큐 로고",
                modifier = Modifier
                    .height(24.dp)
                    .padding(start = 19.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = Res.drawable.ic_alarm),
                contentDescription = "알림",
                modifier = Modifier
                    .padding(end = 13.8.dp)
                    .height(27.18.dp),
                tint = LocalColorTheme.current.gray[300]
            )
        }

        // 🔹 빠른 링크 검색 박스
        Row(
            modifier = Modifier
                .padding(top = 15.dp, start = 16.dp, end = 16.dp)
                .height(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = Basic.maincolor)
                    .clickable { onClickSearch() }
                    .padding(horizontal = 18.51.dp, vertical = 15.dp)
            ) {
                Icon(
                    painter = painterResource(id = Res.drawable.ic_logo_white),
                    contentDescription = null,
                    modifier = Modifier.height(17.dp),
                    tint = LocalColorTheme.current.white
                )

                Text(
                    text = "빠른 링크 검색",
                    color = LocalColorTheme.current.white,
                    modifier = Modifier.padding(start = 36.98.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
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

@Composable
fun CurationScreenPreviewable(nickname: String = "세나") {

    val uri = LocalUriHandler.current

    val demoLinks = listOf(
        com.example.core.model.RecommendedLink(
            isInternal = true,
            userLinkuId = 11L,
            title = "서울 근교 드라이브 코스 TOP5",
            url = "https://naver.com",
            imageUrl = null,
            domain = "naver.com",
            domainImageUrl = null,
            categories = listOf("여행", "힐링")
        ),
        com.example.core.model.RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "글램핑 예약, 누구보다 싸게하기",
            url = "https://blog.naver.com",
            imageUrl = null,
            domain = "blog.naver.com",
            domainImageUrl = null,
            categories = listOf("여행", "정보")
        )
    )

    val likedCurations = listOf(
        UICurationItem("링큐 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
        UICurationItem("링큐 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
        UICurationItem("링큐 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
    )

    val prevMonthLabel = LocalDate.now()
        .minusMonths(1)
        .format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        /* ------------------------------
         * 1) TopBar
         * ------------------------------ */
        item {
            CurationTopBar()
        }

        /* ------------------------------
         * 2) ~님을 위한 ~월 큐레이션 (좌우 24dp)
         * ------------------------------ */
        item {
            Text(
                text = "${nickname}님을 위한 ${prevMonthLabel}의 큐레이션",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 19.dp,
                    bottom = 0.dp

                ),

            )
        }

        /* ------------------------------
         * 3) 큐레이션 하이라이트 카드 (좌우 24dp)
         * ------------------------------ */
        item {
            HighlightCurationCard(
                imageUrl = null,
                title = "링큐 큐레이션",
                date = "2025년 ${prevMonthLabel}호",
                liked = true,
                likeBusy = false,
                onClickCard = {},
                onToggleLike = {},
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 20.dp  // 텍스트 이후 20dp
                    )
            )
            Spacer(Modifier.height(25.dp))
        }

        /* ------------------------------
         * 4) 추천 링크 리스트 (좌우 24dp)
         * ------------------------------ */
        item {
            CurationRecommendedLinksSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp, end = 0.dp),
                links = demoLinks,
                loading = false,
                onRetry = {},
                onClick = { url ->
                    val safe = ensureHttpScheme(url)
                    runCatching { uri.openUri(safe) }
                }
            )
        }

        /* ------------------------------
         * 5) 좋아요한 큐레이션 텍스트 (좌우 24dp)
         * ------------------------------ */
        item {
            Text(
                text = "${nickname}님이 좋아요 한 큐레이션",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 25.dp   // 추천 링크 아래 25dp 유지
                    )
            )
        }


        item {
            Spacer(modifier = Modifier.height(18.dp))
        }

        /* ------------------------------
         * 6) 좋아요한 큐레이션 리스트 (좌우 24dp)
         * ------------------------------ */
        items(likedCurations) { item ->
            LikedCurationCard(
                item = item,
                modifier = Modifier.padding(horizontal = 20.dp),
                onCardClick = {},
                onHeartClick = {}
            )
            Spacer(Modifier.height(10.dp))
        }

    }
}



@Preview(showBackground = true)
@Composable
fun PreviewCurationScreen() {
    MaterialTheme {
        CurationScreenPreviewable()
    }
}



