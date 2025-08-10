package com.example.curation.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.CurationRecommendedLinksSection
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.curation.CurationDetailViewModel
import com.example.design.theme.color.Basic
import com.example.curation.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.core.model.RecommendedLink
import com.example.curation.CurationLinksUiState
import com.example.curation.RecommendedLinkCard
import kotlin.math.ceil
import kotlin.math.min
import androidx.compose.ui.platform.LocalUriHandler
import com.example.curation.CurationViewModel

import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import com.example.curation.CurationDetailUiState
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay



/* ===== 실제 화면: VM에서 닉네임 받아와 Content 호출 ===== */
@Composable
fun CurationDetailScreen(
    userId: Long,
    curationId: Long,
    nickname: String? = null,
    //viewModel: CurationViewModel = hiltViewModel(),
    detailViewModel: CurationDetailViewModel = hiltViewModel(),
    homeViewModel: CurationViewModel = hiltViewModel(),   // 닉네임 전용,하트 상태/토글 재사용
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
        detailViewModel.loadRecommendedLinks(userId, curationId)
    }
    val linksState = detailViewModel.links.collectAsState().value

    //큐레이션 디테일 사용자 정보 전달.
    LaunchedEffect(curationId) {
        detailViewModel.loadCurationDetail(curationId)
    }


    val detailState = detailViewModel.detail.collectAsState().value

    // 좋아요 상태/바쁨 상태
    val liked = homeViewModel.highlightLiked.collectAsState(initial = null).value
    val likeBusy = homeViewModel.likeBusy.collectAsState().value

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
        detailState = detailState,
        liked = liked,
        likeBusy = likeBusy,
        onToggleLike = { homeViewModel.toggleHighlightLike() }
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

        // 가로 페이저 섹션 (API 연동 + 기존 카드 재사용)
        CurationRecommendedLinksPagerSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            links = linksState.items,
            loading = linksState.loading,
            onRetry = { /* 다시 로드 트리거는 부모에서 viewModel.load... 호출로 연결 */ },
            onClick = { url -> runCatching { uri.openUri(url) } }
        )

        Spacer(Modifier.height(16.dp))

        // 하단 위로-토닥 문구 카드
        PositiveNoteCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(Modifier.height(16.dp))
    }
}

/* ===== 하이라이트 카드: 하단만 둥글게 + 단색 배경 + 큰 로고 ===== */
@Composable
private fun HighlightCard(
    nickname: String,
    monthLabel: String,
    onBack: () -> Unit,
    detailState: CurationDetailUiState,
    liked: Boolean?,
    likeBusy: Boolean,
    onToggleLike: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(268.dp)
    ) {
        // 배경 단색 (피그마 CB59EB)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
//                .padding(top = 24.dp) // 추가 여유
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(Color(0xFFCB59EB))
        )

//        // 오른쪽 하단 로고 (기기 폭 비례로 큼직하게)
//        val logoSize = (maxWidth * 0.46f).coerceIn(112.dp, 192.dp)
//        Image(
//            painter = painterResource(R.drawable.ic_logo_light),
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(end = 0.dp, bottom = 16.dp)
//                .size(logoSize)
//                .graphicsLayer(alpha = 0.60f)
//        )
        // 오른쪽 하단 로고 (기기 폭 비례로 큼직하게)
        val logoSize = (maxWidth * 0.46f).coerceIn(112.dp, 192.dp)
        Image(
            painter = painterResource(R.drawable.ic_logo_light),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp) // 오른쪽으로 10 이동
                .padding(bottom = 16.dp)
                .size(logoSize)
                .graphicsLayer(alpha = 0.60f)
        )

        // 하트 토글 버튼 (로고 위 겹치기)

        // 상단 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 70.dp)
                .align(Alignment.TopStart)
        ) {
            Spacer(Modifier.height(12.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = LocalColorTheme.current.white,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )

            Text(
                text = "큐레이션 콘텐츠",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = LocalColorTheme.current.white,
                modifier = Modifier.align(Alignment.Center)
            )
            Spacer(Modifier.height(16.dp))
        }

        // 현재 날짜에서 전달 구하기
        val prevMonthLabel = LocalDate.now()
            .minusMonths(1)
            .format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))

        // 본문
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp)
                .padding(start = 16.dp, end = 16.dp, top = 36.dp)
        ) {
            // 제목 + 하트 (같은 줄, 7월호 기준 우측 36dp, 위로 2dp 올림)
            val HEART_GAP = 36.dp         // ← 더 오른쪽으로 (원래 30dp였으면 여길 조절)
            val HEART_NUDGE_Y = (-5).dp   // ← 살짝 위로 올리기(필요 시 -1 ~ -4dp 튜닝)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "링큐 큐레이션  |  2025년 ${prevMonthLabel}호",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.white,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alignByBaseline()
                )

                //Spacer(Modifier.width(40.dp))
                Spacer(Modifier.width(HEART_GAP))

                HeartToggleButton(
                    liked = liked ?: false,
                    busy = likeBusy,
                    onClick = onToggleLike,
                    modifier = Modifier
                        .size(32.dp)
                        .alignByBaseline()
                        .offset(y = HEART_NUDGE_Y)
                )
            }
            Spacer(Modifier.height(8.dp))
//            Text(
//                text = "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
//                style = MaterialTheme.typography.bodyMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                ),
//                color = LocalColorTheme.current.white
//            )
            Text(
                text = run {
                    val fromApi = replaceNickname(detailState.headerMent, nickname)
                    if (fromApi.isNotBlank()) fromApi
                    else "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = Paperlogy, fontWeight = FontWeight.Medium, fontSize = 16.sp
                ),
                color = LocalColorTheme.current.white
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "${nickname}님의 ${monthLabel} 상황/감정태그 요약",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = LocalColorTheme.current.white
            )
//            Spacer(Modifier.height(8.dp))
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                EmotionChip("#슬픔")
//                Spacer(Modifier.width(8.dp))
//                EmotionChip("#커리어고민")
//                Spacer(Modifier.width(8.dp))
//                EmotionChip("#짜증")
//            }
//            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.height(8.dp))
            when {
                detailState.loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(LocalColorTheme.current.white.copy(alpha = 0.25f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(" ", color = LocalColorTheme.current.white) }
                            if (it != 2) Spacer(Modifier.width(8.dp))
                        }
                    }
                }
                detailState.topTags.isNotEmpty() -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        detailState.topTags.take(3).forEachIndexed { idx, tag ->
                            EmotionChip("#$tag")
                            if (idx != detailState.topTags.lastIndex) Spacer(Modifier.width(8.dp))
                        }
                    }
                }
                else -> {
                    // 폴백: 기존 더미 유지
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmotionChip("#슬픔")
                        Spacer(Modifier.width(8.dp))
                        EmotionChip("#커리어고민")
                        Spacer(Modifier.width(8.dp))
                        EmotionChip("#짜증")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HeartToggleButton(
    modifier: Modifier = Modifier,
    liked: Boolean,
    busy: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)                         // 충분한 터치 타깃
            .clip(CircleShape)
            .clickable(enabled = !busy) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline
            ),
            contentDescription = if (liked) "좋아요 취소" else "좋아요",
            tint = Color.White,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer { alpha = if (busy) 0.5f else 1f } // 요청 중이면 살짝 흐리게
        )
    }
}
private fun replaceNickname(text: String?, nickname: String): String {
    if (text.isNullOrBlank()) return ""
    return text.replace("(닉네임)", nickname)
        .replace("{닉네임}", nickname)
        .replace("\$닉네임", nickname)
}

/* =============================================================================
   추천 링크 섹션 (가로 페이저) — 기존 카드 UI는 그대로 사용
   한 페이지 3개, 최대 9개, 아래 분홍색 바 인디케이터
============================================================================= */

data class LinkItem(
    val title: String,
    val imageRes: Int,
    val url: String
)
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun CurationRecommendedLinksPagerSection(
//    modifier: Modifier = Modifier,
//    links: List<RecommendedLink>,
//    loading: Boolean,
//    onRetry: () -> Unit = {},
//    onClick: (String) -> Unit
//) {
//    // 최대 9개
//    val items = remember(links) { links.take(9) }
//    val perPage = 3
//    val pageCount = remember(items) { ceil(items.size / perPage.toFloat()).toInt().coerceAtLeast(1) }
//
//    // 카드 하나의 세로 높이 (기존 80.dp → 96.dp로 확대)
//    val rowHeight: Dp = 96.dp
//
//// 카드 사이 세로 간격 (기존 8.dp → 12.dp)
//    val spacing = 12.dp
//
//// 전체 컨테이너 높이 = (카드 높이 × 페이지당 카드 수) + (간격 × (페이지당 카드 수 - 1))
//    val containerHeight = rowHeight * perPage + spacing * (perPage - 1)
//
//
//    // 0.8초 이하면 스켈레톤, 그 이후엔 '준비중' 화면
//    var showPreparing by remember { mutableStateOf(false) }
//    LaunchedEffect(loading) {
//        showPreparing = false
//        if (loading) {
//            delay(800)
//            showPreparing = true
//        }
//    }
////
//    Column(modifier = modifier) {
//        Text(
//            text = "추천 링크",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontFamily = Paperlogy, fontWeight = FontWeight.Bold, fontSize = 20.sp
//            )
//        )
//        Spacer(Modifier.height(8.dp))
//
//        when {
//            loading && items.isEmpty() && !showPreparing -> {
//                // 1단계: 스켈레톤 3개
//                SkeletonLinks(height = rowMinHeight, spacing = spacing)
//                Spacer(Modifier.height(8.dp))
//            }
//
//            loading && items.isEmpty() && showPreparing -> {
//                // 2단계: 준비중 화면 (네가 올려준 스샷 느낌)
//                LinksPreparing(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(containerHeight) // 페이저 영역과 동일 높이
//                        .clip(RoundedCornerShape(16.dp))
//                        .background(Color(0xFFFFFFFF))
//                        .padding(horizontal = 16.dp)
//                )
//            }
//
//            items.isEmpty() -> {
//                EmptyLinks(onRetry = onRetry)
//            }
//
//            else -> {
//                val pagerState = rememberPagerState(pageCount = { pageCount })
//
//                HorizontalPager(
//                    state = pagerState,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(containerHeight)
//                ) { page ->
//                    val start = page * perPage
//                    val end = min(start + perPage, items.size)
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(spacing),
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        for (i in start until end) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(rowHeight)
//                            ) {
//                                // 기존 카드 재사용 (타이틀/이미지/도메인/카테고리 모두 처리)
//                                RecommendedLinkCard(
//                                    link = items[i],
//                                    onClick = onClick
//                                )
//                            }
//                        }
//                    }
//                }
//
//                Spacer(Modifier.height(8.dp))
//
//                // 인디케이터
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    repeat(pageCount) { i ->
//                        val selected = pagerState.currentPage == i
//                        Box(
//                            modifier = Modifier
//                                .padding(horizontal = 3.dp)
//                                .height(6.dp)
//                                .width(if (selected) 18.dp else 6.dp)
//                                .clip(RoundedCornerShape(50))
//                                .background(if (selected) Color(0xFFCB59EB) else Color(0xFFEDEDED))
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurationRecommendedLinksPagerSection(
    modifier: Modifier = Modifier,
    links: List<RecommendedLink>,
    loading: Boolean,
    onRetry: () -> Unit = {},
    onClick: (String) -> Unit
) {
    // 최대 9개
    val items = remember(links) { links.take(9) }
    val perPage = 3
    val pageCount = remember(items) { ceil(items.size / perPage.toFloat()).toInt().coerceAtLeast(1) }

    // 레이아웃 설정 (기존과 동일)
    val rowHeight: Dp = 96.dp
    val rowMinHeight: Dp = rowHeight
    val spacing = 12.dp

    // 로딩일 때만 여유 버퍼(잘림 방지), 평소엔 0dp → 기존 UI 100% 유지
    val extraSpace = if (loading && items.isEmpty()) 12.dp else 0.dp
    val containerHeight = rowMinHeight * perPage + spacing * (perPage - 1) + extraSpace

    // 0.8초 이하면 스켈레톤, 이후엔 '준비중' 화면
    var showPreparing by remember { mutableStateOf(false) }
    LaunchedEffect(loading) {
        showPreparing = false
        if (loading) {
            delay(800)
            showPreparing = true
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "추천 링크",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
        )
        Spacer(Modifier.height(8.dp))

        when {
            loading && items.isEmpty() && !showPreparing -> {
                // 1단계: 스켈레톤
                SkeletonLinks(height = rowMinHeight, spacing = spacing)
                Spacer(Modifier.height(8.dp))
            }

            loading && items.isEmpty() && showPreparing -> {
                // 2단계: 준비중 화면 (페이저 영역과 동일 높이)
                LinksPreparing(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(containerHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                )
            }

            items.isEmpty() -> {
                EmptyLinks(onRetry = onRetry)
            }

            else -> {
                val pagerState = rememberPagerState(pageCount = { pageCount })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(containerHeight)
                ) { page ->
                    val start = page * perPage
                    val end = min(start + perPage, items.size)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        for (i in start until end) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeight) // 기존과 동일 높이
                            ) {
                                RecommendedLinkCard(link = items[i], onClick = onClick)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp)) // 인디케이터 위 간격도 기존과 동일
                PagerIndicator(pageCount = pageCount, current = pagerState.currentPage)
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

// 2) 준비중 화면
@Composable
private fun LinksPreparing(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로고 배경 + 로고 이미지 함께
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEDE9FF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_whiteback),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .alpha(0.82f),         // ← 로고를 옅게(밝기 낮춘 느낌)
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(12.dp))     // ← 로고와 제목 간격 18 → 12

        // 제목
        Text(
            "잠시만 기다려주세요!",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        )

        Spacer(Modifier.height(4.dp))      // ← 제목-설명 간격 6 → 4

        // 설명
        Text(
            "AI가 한달 감정&상황 데이터를 바탕으로\n추천 링크를 준비하고 있어요",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFF87898F)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))     // 16 → 14 (하단도 살짝 타이트하게)

        // 안내 문구
        Text(
            "＊ 검증 과정으로 인해 추천 링크 수가 제한될 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFFFF5E5E)
            ),
            textAlign = TextAlign.Center
        )
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

// 4) 인디케이터
@Composable
private fun PagerIndicator(pageCount: Int, current: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { i ->
            val selected = current == i
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(6.dp)
                    .width(if (selected) 18.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) Color(0xFFCB59EB) else Color(0xFFEDEDED))
            )
        }
    }
}
/* ===== 카드 UI (RecommendedLink 버전) ===== */
@Composable
fun RecommendedLinkCard(
    link: RecommendedLink,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
            .clickable { onClick(link.url) }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ctx = LocalContext.current

        if (!link.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(link.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEDEDED))
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = link.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                maxLines = 2
            )

            Spacer(Modifier.height(4.dp))

            if (!link.categories.isNullOrEmpty()) {
                Row {
                    link.categories!!.take(2).forEachIndexed { idx, tag ->
                        if (idx > 0) Spacer(Modifier.width(6.dp))
                        TagChip(tag)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!link.domainImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(link.domainImageUrl)
                            .build(),
                        contentDescription = "출처 아이콘",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = link.domain ?: "외부 링크",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF43454B)
                    )
                )
            }
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
                fontFamily = Paperlogy,
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}

/* ===== 하단 긍정 메시지 카드 ===== */
@Composable
private fun PositiveNoteCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7EAFE)) // 연보라 톤
            .padding(start = 16.dp, end = 16.dp, top = 50.dp)
    ) {
        Text(
            text = "지금 떠오르지 않아도 괜찮아요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = LocalColorTheme.current.black
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = Color(0xFF43454B)
        )
    }
}

/* ===== 감정 칩 (기존) ===== */
@Composable
private fun HighlightChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColorTheme.current.white.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = LocalColorTheme.current.white,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun EmotionChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF9A3AB5),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 12.sp
            )
        )
    }
}

/* ===== 데모 데이터 & 프리뷰 ===== */
private val demoLinks = listOf(
    LinkItem("서울 근교 드라이브 코스 TOP5", R.drawable.img_seoul_card, "https://example.com/1"),
    LinkItem("감성 무드등 추천", R.drawable.img_travel_card, "https://example.com/2"),
    LinkItem("주말 호캉스 체크리스트", R.drawable.img_travel_card, "https://example.com/3"),
    LinkItem("몰입을 부르는 작업법", R.drawable.img_travel_card, "https://example.com/4"),
    LinkItem("글램핑 예약, 누구보다 싸게하기", R.drawable.img_travel_card, "https://example.com/5"),
    LinkItem("비 오는 날 플레이리스트", R.drawable.img_travel_card, "https://example.com/6"),
    LinkItem("소도시 카페투어 루트", R.drawable.img_travel_card, "https://example.com/7"),
    LinkItem("가볍게 읽는 에세이 5선", R.drawable.img_travel_card, "https://example.com/8"),
    LinkItem("7일 루틴 만들기 가이드", R.drawable.img_travel_card, "https://example.com/9"),
)

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

///* ===== 실제 화면: VM에서 닉네임 받아와 Content 호출 ===== */
//@Composable
//fun CurationDetailScreen(
//    viewModel: CurationViewModel = hiltViewModel(),
//    onBack: () -> Unit = {}            // ← 외부에서 받음
//) {
//    val nickname = viewModel.nickname.collectAsState(initial = "").value.orEmpty()
//    val monthLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))
//
//    CurationDetailScreenContent(
//        nickname = nickname.ifBlank { "세나" },
//        monthLabel = monthLabel,
//        onBack = onBack                 // ← 여기서 넘김
//    )
//}
///* ===== UI 전용 Content (프리뷰에서 이걸 사용) ===== */
//@Composable
//private fun CurationDetailScreenContent(
//    nickname: String,
//    monthLabel: String,
//    onBack: () -> Unit = {}
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(LocalColorTheme.current.white)
//            .windowInsetsPadding(WindowInsets.statusBars) // 상태바 겹침 방지
//    ) {
//
//
//        // 🔴 보라색 카드: 좌우 여백 없이 꽉 채움 (full-bleed)
//        HighlightCard(
//            nickname = nickname,
//            monthLabel = monthLabel,
//            onBack = onBack
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        // 추천 링크 (좌우 16 여백 유지)
//        CurationRecommendedLinksSection(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        )
//        Spacer(Modifier.height(16.dp))
//    }
//}
//
///* ===== 하이라이트 카드: 하단만 둥글게 + 높이 명시 ===== */
//@Composable
//private fun HighlightCard(
//    nickname: String,
//    monthLabel: String,
//    onBack: () -> Unit
//) {
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(240.dp)   // 부모에는 clip 주지 않기!
//    ) {
//        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
//        val bleedDp = (screenWidthDp - maxWidth) / 2
//
//        // 배경 (Compose 그라데이션)
//        Box(
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .fillMaxSize()
//                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
//                .background(Color(0xFFCB59EB))   // ← 단색 배경 (피그마 값 CB59EB, 알파 FF)
//        )
//
//        // 오른쪽 하단 로고
//        Image(
//            painter = painterResource(R.drawable.ic_logo_light),
//            contentDescription = null,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(end = 16.dp, bottom = 14.dp) // 필요에 맞게 조절
//                .size(72.dp)                           // 필요 시 크기 조절
//        )
//
//
//        // 상단 바
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
//                .align(Alignment.TopStart)
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_back),
//                contentDescription = "뒤로",
//                tint = LocalColorTheme.current.white,
//                modifier = Modifier
//                    .size(18.dp)
//                    .align(Alignment.CenterStart)
//                    .clickable { onBack() }
//            )
//            Text(
//                text = "큐레이션 콘텐츠",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                ),
//                color = LocalColorTheme.current.white,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        // 본문
//        Column(
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(top = 72.dp)
//                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
//        ) {
//            Text(
//                text = "링큐 큐레이션  |  2025년 ${monthLabel}호",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp
//                ),
//                color = LocalColorTheme.current.white
//            )
//            Spacer(Modifier.height(6.dp))
//            Text(
//                text = "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
//                style = MaterialTheme.typography.bodyMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                ),
//                color = LocalColorTheme.current.white
//            )
//            Spacer(Modifier.height(16.dp))
//            Text(
//                text = "${nickname}님의 ${monthLabel} 상황/감정태그 요약",
//                style = MaterialTheme.typography.bodySmall.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 13.sp
//                ),
//                color = LocalColorTheme.current.white
//            )
//            Spacer(Modifier.height(4.dp))
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                EmotionChip("#슬픔")
//                Spacer(Modifier.width(8.dp))
//                EmotionChip("#커리어고민")
//                Spacer(Modifier.width(8.dp))
//                EmotionChip("#짜증")
//            }
//        }
//    }
//}
////@Composable
////private fun HighlightCard(
////    nickname: String,
////    monthLabel: String,
////    onBack: () -> Unit
////) {
////    Box(
////        modifier = Modifier
////            .fillMaxWidth()                      // ▷ 좌우 꽉
////            .height(240.dp)                      // 필요 시 조정
////            .clip(
////                RoundedCornerShape(
////                    topStart = 0.dp, topEnd = 0.dp,
////                    bottomStart = 24.dp, bottomEnd = 24.dp
////                )
////            )
////    ) {
////        Image(
////            painter = painterResource(id = R.drawable.img_curationdetail_bg),
////            contentDescription = null,
////            contentScale = ContentScale.Crop,
////            modifier = Modifier.matchParentSize()
////                .padding(horizontal = (-16).dp)
////        )
////
////        // ▷ 상단바: ← 는 좌, 타이틀은 가운데 정렬
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
////                .align(Alignment.TopStart)
////        ) {
////            Icon(
////                painter = painterResource(id = R.drawable.ic_back),
////                contentDescription = "뒤로",
////                tint = LocalColorTheme.current.white,
////                modifier = Modifier
////                    .size(18.dp)
////                    .align(Alignment.CenterStart)
////                    .clickable { onBack() }
////            )
////            Text(
////                text = "큐레이션 콘텐츠",
////                style = MaterialTheme.typography.titleMedium.copy(
////                    fontFamily = Paperlogy,
////                    fontWeight = FontWeight.Medium,
////                    fontSize = 16.sp
////                ),
////                color = LocalColorTheme.current.white,
////                modifier = Modifier.align(Alignment.Center)
////            )
////        }
////
////        // ▷ 본문 (좌우 16)
////        Column(
////            modifier = Modifier
////                .align(Alignment.TopStart)
////                .padding(top = 56.dp)
////                .padding(horizontal = 16.dp) // 모든 본문 텍스트 좌우 여백
////        ) {
////            Text(
////                text = "링큐 큐레이션  |  2025년 ${monthLabel}호",
////                style = MaterialTheme.typography.titleMedium.copy(
////                    fontFamily = Paperlogy,
////                    fontWeight = FontWeight.Bold,
////                    fontSize = 20.sp
////                ),
////                color = LocalColorTheme.current.white
////            )
////            Spacer(Modifier.height(6.dp))
////            Text(
////                text = "생각은 많은데 정리가 안 되죠.\n세나님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
////                style = MaterialTheme.typography.bodyMedium.copy(
////                    fontFamily = Paperlogy,
////                    fontWeight = FontWeight.Medium,
////                    fontSize = 16.sp
////                ),
////                color = LocalColorTheme.current.white
////            )
////            Spacer(Modifier.height(10.dp))
////
////            // 보조 라벨
////            Text(
////                text = "${nickname}님의 ${monthLabel} 상황/감정태그 요약",
////                style = MaterialTheme.typography.bodySmall.copy(
////                    fontFamily = Paperlogy,
////                    fontWeight = FontWeight.Medium,
////                    fontSize = 13.sp
////                ),
////                color = LocalColorTheme.current.white
////            )
////        }
////
////        // ▷ 하단 칩(흰 배경 + 보라 텍스트)
////        Row(
////            modifier = Modifier
////                .align(Alignment.BottomStart)
////                .padding(start = 16.dp, bottom = 14.dp),
////            verticalAlignment = Alignment.CenterVertically
////        ) {
////            EmotionChip("#슬픔")
////            Spacer(Modifier.width(8.dp))
////            EmotionChip("#커리어고민")
////            Spacer(Modifier.width(8.dp))
////            EmotionChip("#짜증")
////        }
////    }
////}
//@Composable
//private fun HighlightChip(text: String) {
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(20.dp))
//            .background(LocalColorTheme.current.white.copy(alpha = 0.2f))
//            .padding(horizontal = 10.dp, vertical = 6.dp)
//    ) {
//        Text(
//            text = text,
//            color = LocalColorTheme.current.white,
//            style = MaterialTheme.typography.bodySmall.copy(
//                fontFamily = Paperlogy,
//                fontSize = 12.sp
//            )
//        )
//    }
//}
//
///* ===== 프리뷰: Hilt/VM 없이도 렌더링 됨 ===== */
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun PreviewCurationDetailScreen() {
//    Surface {
//        CurationDetailScreenContent(
//            nickname = "세나",
//            monthLabel = "8월"
//        )
//    }
//}
//
////칩 배경
//@Composable
//private fun EmotionChip(text: String) {
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(20.dp))
//            .background(LocalColorTheme.current.white) // 흰 박스
//            .padding(horizontal = 10.dp, vertical = 6.dp)
//    ) {
//        Text(
//            text = text,
//            color = androidx.compose.ui.graphics.Color(0xFF9A3AB5),  // #9A3AB5
//            style = MaterialTheme.typography.bodySmall.copy(
//                fontFamily = Paperlogy,
//                fontSize = 12.sp
//            )
//        )
//    }
//}