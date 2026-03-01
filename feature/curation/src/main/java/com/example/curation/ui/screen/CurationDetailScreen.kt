package com.example.curation.ui.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.curation.CurationDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.example.core.model.RecommendedLink
import com.example.curation.CurationLinksUiState

import kotlin.math.min
import com.example.curation.CurationViewModel

import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import com.example.curation.CurationDetailUiState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.ensureHttpScheme
import com.example.curation.ui.detail_card.HighlightCard
import com.example.curation.ui.recommend_list.CurationRecommendedLinksSection
import com.example.curation.ui.recommend_list.RecommendedLinkCardSkeleton
import com.example.curation.ui.recommend_list.SkeletonEnd
import com.example.curation.ui.recommend_list.SkeletonStart
import com.example.design.theme.font.Paperlogy


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
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))
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

    ) {


        //  HighlightCard는 스크롤 되지 않도록 해야함.

        Box(
            modifier = Modifier

        ) {
            HighlightCard(
                nickname = nickname,
                monthLabel = monthLabel,
                onBack = onBack,
                detailState = detailState,
                liked = liked ?: false,
                likeBusy = likeBusy,
                onToggleLike = onToggleLike
            )
        }


        Spacer(Modifier.height(20.dp))

        //여기만 스크롤이 가능해야함.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (linksState.loading) {
                SectionTitleSkeleton(
                    modifier = Modifier.padding(start = 24.dp)
                )
            } else {
                Text(
                    text = "추천 링크",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy.font,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            CurationRecommendedLinksPagerWrapper(
                links = linksState.items,
                loading = linksState.loading,
                onRetry = { /* reload */ },
                onClick = { url -> uri.openUri(ensureHttpScheme(url)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            PositiveNoteCard(
                footerMent = detailState.footerMent,
                loading = detailState.loading,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(32.dp))
        }
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
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SkeletonLinks()

                Spacer(Modifier.height(16.dp))

                //  인디케이터 영역 명확히 확보
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PagerIndicatorSkeleton()
                }
            }
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
        PagerIndicator(
            pageCount = pageCount,
            currentPage = pagerState.currentPage,

        )
    }
}

//제목 스켈레톤 추가(추천 링크)
@Composable
private fun SectionTitleSkeleton(
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberGrayShimmerBrush()

    Box(
        modifier = modifier
            .width(80.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(shimmerBrush)
    )
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
private fun SkeletonLinks() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)   // 홈 스크린과 동일 패딩
    ) {
        repeat(3) {
            RecommendedLinkCardSkeleton() // ← 모든 디자인 동일
            Spacer(Modifier.height(10.dp))
        }
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
                fontFamily = Paperlogy.font,
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
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberGrayShimmerBrush()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (loading) {
                    Modifier.background(shimmerBrush)
                } else {
                    Modifier.background(Color(0xFFFBEEFF))
                }
            )
            .padding(horizontal = 20.dp, vertical = 25.dp)
    ) {
        if (!loading) {
            Text(
                text = footerMent?.trim()
                    ?: "지금 떠오르지 않아도 괜찮아요.\n영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요.",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF43454B)
                )
            )
        }
    }
}

//감정 박스 스켈레놑+ 쉬머
@Composable
private fun rememberGrayShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "gray_shimmer")

    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = LinearEasing
            )
        ),
        label = "shift"
    ).value

    return Brush.linearGradient(
        colors = listOf(
            SkeletonStart,
            SkeletonEnd,
            SkeletonStart
        ),
        start = Offset(shift - 400f, 0f),
        end = Offset(shift, 0f)
    )
}

//인디케이터 + 스켈레톤 + 쉬머
@Composable
private fun PagerIndicatorSkeleton() {
    val shimmerBrush = rememberGrayShimmerBrush()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.5.dp))
                .background(shimmerBrush)
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val selected = currentPage == index

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (selected) 32.dp else 6.dp)
                    .height(6.dp)
                    .clip(
                        if (selected) RoundedCornerShape(3.5.dp)
                        else RoundedCornerShape(50)
                    )
                    .background(
                        if (selected) Color(0xFFE5ACF4)
                        else Color(0xFFE9EAEE)
                    )
            )
        }
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





