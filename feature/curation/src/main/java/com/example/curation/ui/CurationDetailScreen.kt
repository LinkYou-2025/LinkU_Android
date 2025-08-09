package com.example.curation.ui


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
import com.example.curation.CurationViewModel
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.curation.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.curation.RecommendedLinkCard
import kotlin.math.ceil
import kotlin.math.min



/* ===== 실제 화면: VM에서 닉네임 받아와 Content 호출 ===== */
@Composable
fun CurationDetailScreen(
    viewModel: CurationViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val nickname = viewModel.nickname.collectAsState(initial = "").value.orEmpty()
    val monthLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))

    CurationDetailScreenContent(
        nickname = nickname.ifBlank { "세나" },
        monthLabel = monthLabel,
        onBack = onBack
    )
}

/* ===== UI 전용 Content ===== */
@Composable
private fun CurationDetailScreenContent(
    nickname: String,
    monthLabel: String,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
    ) {
        // 보라색 카드 (full-bleed)
        HighlightCard(
            nickname = nickname,
            monthLabel = monthLabel,
            onBack = onBack
        )

        Spacer(Modifier.height(16.dp))

        // 추천 링크: 가로 페이저(1페이지에 3개, 최대 9개)
        CurationRecommendedLinksSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            links = demoLinks
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
    onBack: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // 배경 단색 (피그마 CB59EB)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(Color(0xFFCB59EB))
        )

        // 오른쪽 하단 로고 (기기 폭 비례로 큼직하게)
        val logoSize = (maxWidth * 0.46f).coerceIn(112.dp, 192.dp)
        Image(
            painter = painterResource(R.drawable.ic_logo_light),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(logoSize)
                .graphicsLayer(alpha = 0.18f)
        )

        // 상단 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .align(Alignment.TopStart)
        ) {
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
        }

        // 본문
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Text(
                text = "링큐 큐레이션  |  2025년 ${monthLabel}호",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = LocalColorTheme.current.white
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "생각은 많은데 정리가 안 되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
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
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmotionChip("#슬픔")
                Spacer(Modifier.width(8.dp))
                EmotionChip("#커리어고민")
                Spacer(Modifier.width(8.dp))
                EmotionChip("#짜증")
            }
        }
    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurationRecommendedLinksSection(
    modifier: Modifier = Modifier,
    links: List<LinkItem> = listOf(
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
) {
    val items = remember(links) { links.take(9) }  // 최대 9개
    val perPage = 3
    val pageCount = remember(items) { ceil(items.size / perPage.toFloat()).toInt().coerceAtLeast(1) }

    // 카드 3개가 정확히 보이도록 섹션 높이 고정 (카드 UI는 그대로)
    val rowHeight: Dp = 80.dp
    val spacing = 8.dp
    val containerHeight = rowHeight * 3 + spacing * 2

    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(modifier = modifier) {
        Text(
            text = "추천 링크",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        Spacer(Modifier.height(8.dp))

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
                            .height(rowHeight)
                    ) {
                        RecommendedLinkCard(items[i]) // ← 기존 카드 UI 그대로
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 분홍색 바 인디케이터 (선택 페이지는 길게, 나머지는 점)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { i ->
                val selected = pagerState.currentPage == i
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
}

/* ===== 카드 UI (기존 스타일 그대로) ===== */
@Composable
fun RecommendedLinkCard(item: LinkItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
            .clickable { /* TODO: 링크 클릭 처리 예정 */ }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                TagChip("여행")
                Spacer(modifier = Modifier.width(6.dp))
                TagChip(if (item.title.contains("드라이브")) "힐링" else "행복")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 출처: 아이콘 + 텍스트
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconRes = if (item.title.contains("드라이브")) R.drawable.ic_naver else R.drawable.ic_blog
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "출처 아이콘",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (item.title.contains("드라이브")) "Naver" else "Blog",
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
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "지금 떠오르지 않아도 괜찮아요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = LocalColorTheme.current.black
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = Paperlogy,
                fontSize = 13.sp
            ),
            color = Color(0xFF616161)
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
    Surface {
        CurationDetailScreenContent(
            nickname = "세나",
            monthLabel = "8월"
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