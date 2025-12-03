package com.example.curation.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.curation.CurationDetailViewModel
import com.example.curation.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.core.model.RecommendedLink
import com.example.curation.CurationLinksUiState

import kotlin.math.ceil
import kotlin.math.min
import com.example.curation.CurationViewModel
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset

import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.TextStyle
import com.example.curation.CurationDetailUiState
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.curation.ui.detail_card.HighlightCard
import com.example.curation.ui.recommend_list.CurationRecommendedLinksSection
import kotlinx.coroutines.delay


//헬퍼


/* ===== 실제 화면: VM에서 닉네임 받아와 Content 호출 ===== */
@Composable
fun CurationDetailScreen(
    userId: Long,
    curationId: Long,
    nickname: String? = null,
    //viewModel: CurationViewModel = hiltViewModel(),
    detailViewModel: CurationDetailViewModel, // 외부 주입만 사용 -> 수정.
    homeViewModel: CurationViewModel,
    //detailViewModel: CurationDetailViewModel = hiltViewModel(),
    //homeViewModel: CurationViewModel = hiltViewModel(),   // 닉네임 전용,하트 상태/토글 재사용
    onBack: () -> Unit = {},

) {
    // 닉네임 로드 (홈 VM)
    LaunchedEffect(Unit) {
        homeViewModel.loadNickname()
    }


    val nicknameState = homeViewModel.nickname.collectAsState(initial = "")
    val finalNickname = ((nickname ?: nicknameState.value) ?: "").ifBlank { "세나" }

    // 추천 링크 로드 (디테일 VM)
    LaunchedEffect(userId, curationId) {
        detailViewModel.loadAll(userId, curationId) // detail + links + like(뷰모델 내부 분기)
    }

    val detail by detailViewModel.detail.collectAsStateWithLifecycle()
    val linksState by detailViewModel.links.collectAsStateWithLifecycle()

    val liked by homeViewModel.highlightLiked.collectAsStateWithLifecycle(initialValue = null)
    val likeBusy by homeViewModel.likeBusy.collectAsStateWithLifecycle()

    //좋아요 상태 로드
    LaunchedEffect(curationId) {
        homeViewModel.refreshHighlightLike(curationId)   // 초기 좋아요 상태 로드
        homeViewModel.setCurrentCurationId(curationId)   // ← 현재 CID를 뷰모델에 주입
    }

    val monthLabel = remember {
        java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("M월", java.util.Locale.KOREAN))
    }

    CurationDetailScreenContent(
        nickname = finalNickname,
        monthLabel = monthLabel,
        linksState = linksState,
        onBack = onBack,
        detailState = detail,
        liked = liked,
        likeBusy = likeBusy,
        onToggleLike = { homeViewModel.toggleLikeFor(curationId) }
        //onToggleLike = { homeViewModel.toggleHighlightLike() }
    )
}

/* ===== UI 전용 Content ===== */
@Composable
private fun CurationDetailScreenContent(
    nickname: String,
    monthLabel: String,
    linksState: CurationLinksUiState,
    onBack: () -> Unit = {},
    detailState: CurationDetailUiState,
    liked: Boolean?,
    likeBusy: Boolean,
    onToggleLike: () -> Unit
) {
    val uri = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
            //.windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
    )
    {
        // 보라색 카드 (full-bleed)
        HighlightCard(
            nickname = nickname,
            monthLabel = monthLabel,
            onBack = onBack,
            detailState = detailState,
            liked = liked ?: false,
            likeBusy = likeBusy,
            onToggleLike = onToggleLike
        )

        Spacer(Modifier.height(16.dp))

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

        Spacer(Modifier.height(20.dp))

        // 가로 페이저 섹션 (API 연동 + 기존 카드 재사용)
        CurationRecommendedLinksPagerWrapper(
            links = linksState.items,
            loading = linksState.loading,
            onRetry = { /* 새로고침 콜백 */ },
            onClick = { url -> uri.openUri(ensureHttpScheme(url)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))


        // footerMent 바인딩
        PositiveNoteCard(
            footerMent = detailState.footerMent,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurationRecommendedLinksPagerWrapper(
    links: List<RecommendedLink>,
    loading: Boolean,
    onRetry: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(links) { links.take(9) }
    val perPage = 3
    val pageCount = ((items.size + perPage - 1) / perPage).coerceAtLeast(1)

    Column(modifier) {

        // 로딩 중일 때: 스켈레톤 3개만 표시하고 페이저/인디케이터 숨김
        if (loading) {
            SkeletonLinks(height = 120.dp, spacing = 12.dp)
            return
        }

        // 링크 없음
        if (items.isEmpty()) {
            EmptyLinks(onRetry)
            return
        }

        val pagerState = rememberPagerState(pageCount = { pageCount })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->

            val start = page * perPage
            val end = min(start + perPage, items.size)
            val slice = items.subList(start, end)

            CurationRecommendedLinksSection(
                modifier = Modifier.fillMaxWidth(),
                links = slice,
                loading = false,   //로딩 아님 (Wrapper에서 처리)
                onRetry = onRetry,
                onClick = onClick
            )
        }

        Spacer(Modifier.height(12.dp))

        // 인디케이터
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { index ->
                val selected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (selected) 18.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selected) Color(0xFFE5ACF4)
                            else Color(0xFFE9EAEE)
                        )
                )
            }
        }
    }
}
// 심플한 쉐이머 브러시
@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "shift"
    )
    val colors = listOf(Color(0xFFEDEDED), Color(0xFFF7F7F7), Color(0xFFEDEDED))
    return Brush.linearGradient(colors, start = Offset(0f, 0f), end = Offset(shift, shift))
}

// 1) 스켈레톤 3개
@Composable
private fun SkeletonLinks(height: Dp, spacing: Dp) {
    val brush = rememberShimmerBrush()
    Column {
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            if (it != 2) Spacer(Modifier.height(spacing))
        }
    }
}


// 3) 빈 상태
@Composable
private fun EmptyLinks(onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        Text("아직 추천할 링크가 없어요.")
        Spacer(Modifier.height(8.dp))
        Text("새로고침", color = Color(0xFFCB59EB), modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .padding(2.dp)
            .then(Modifier.clickable { onRetry() }))
    }
}




@Composable
fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}

/* ===== 하단 긍정 메시지 카드 ===== */
@Composable
private fun PositiveNoteCard(
    footerMent: String?,
    modifier: Modifier = Modifier
) {
    val text = footerMent?.trim()
        ?: "지금 떠오르지 않아도 괜찮아요.\n영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요."

    Column(
        modifier = modifier
            .background(
                color = Color(0xFFFBEEFF),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight(400),
                color = Color(0xFF43454B)
            )
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCurationDetailScreen() {
    val demo = listOf(
        RecommendedLink(
            isInternal = true,
            userLinkuId = 11L,
            title = "성신여대 홈페이지",
            url = "https://www.sungshin.ac.kr/sites/main_kor/main.jsp",
            imageUrl = null,
            domain = "sungshin.ac.kr",
            domainImageUrl = null,
            categories = listOf("기타")
        ),
        RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "나만의 자기관리 체크리스트 만들기 - Adobe",
            url = "https://www.adobe.com/kr/acrobat/hub/create-a-self-care-checklist.html",
            imageUrl = "https://picsum.photos/400/240",
            domain = "adobe.com",
            domainImageUrl = null,
            categories = listOf("정보", "생산성")
        ),
        RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "주간 읽을거리 모음",
            url = "https://example.com/weekly",
            imageUrl = null,
            domain = null,
            domainImageUrl = null,
            categories = listOf("뉴스")
        )
    )
    val demoDetail = CurationDetailUiState(
        loading = false,
        topTags = listOf("설렘", "통학 중", "공부 중"),
        headerMent = "세나님의 하루가 반짝였던 순간이에요. 그 감정에 어울리는 콘텐츠를 추천해요.",
        footerMent = "설렘은 가장 강력한 동기부여예요. 지금, 그 에너지를 믿어보세요."
    )


    Surface {
        CurationDetailScreenContent(
            nickname = "세나",
            monthLabel = "8월",
            linksState = CurationLinksUiState(loading = false, items = demo),
            detailState = demoDetail,
            onBack = {},
            liked = false,          // 프리뷰 기본값
            likeBusy = false,       // 프리뷰 기본값
            onToggleLike = {}       // 프리뷰 기본값
        )
    }
}





