package com.example.curation.ui.recommend_list


// 역할: 추천 링크 리스트 섹션 + 카드
// 기능: 서버에서 받은 RecommendedLink(title, imageUrl, url, categories, domain)를 표시
// 관심사: 리스트 반복, 썸네일/아이콘 표시, 클릭 콜백

import androidx.compose.ui.unit.lerp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.curation.ui.resolveSourceIcon
import com.example.curation.ui.resolveSourceLabel
import com.example.core.model.RecommendedLink
import com.example.curation.R
import coil3.request.crossfade
import com.example.curation.Paperlogy
import com.example.curation.ui.util.ShimmerSkeleton
import com.example.curation.ui.util.rememberScaleFactor
import com.example.curation.ui.util.shimmer
import java.net.URI




@Composable
fun CurationRecommendedLinksSection(
    modifier: Modifier = Modifier,
    links: List<RecommendedLink>,
    loading: Boolean,
    onRetry: () -> Unit = {},
    onClick: (String) -> Unit //url 클릭 콜백

) {
    Column(modifier = modifier) {
//        Text(
//            text = "추천 링크",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontFamily = Paperlogy, fontWeight = FontWeight.Bold, fontSize = 20.sp
//            ),
//            modifier = Modifier.padding(start = 24.dp)
//        )
//        Spacer(Modifier.height(20.dp))

        when {
            loading && links.isEmpty() -> {
                repeat(2) {
                    RecommendedLinkCardSkeleton()
                    Spacer(Modifier.height(10.dp))
                }
            }
            links.isEmpty() -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F8F8))
                        .padding(16.dp)
                ) {
                    Text("아직 추천할 링크가 없어요.", fontSize = 14.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "새로고침",
                        color = Color(0xFFCB59EB),
                        modifier = Modifier.clickable { onRetry() }
                    )
                }
            }
            else -> {
                links.take(9).forEachIndexed { index, link ->
                    RecommendedLinkCard(link = link, onClick = onClick)

                    // 마지막 카드가 아닐 때만 Spacer(10dp) 추가
                    if (index != links.take(9).lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedLinkCard(
    link: RecommendedLink,
    onClick: (String) -> Unit
) {
    val domain = link.domain ?: link.url.toHost()
    val iconRes = resolveSourceIcon(domain)
    val sourceLabel = resolveSourceLabel(domain)

    val ctx = LocalContext.current
    val scale = rememberScaleFactor()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        //폰은 글자 크기 고정, 테블릿은 글자 크기 사이즈업.
        val w = maxWidth
        val isTablet = w >= 600.dp
        val scale = rememberScaleFactor()

        val titleSize = if (isTablet) (15 * scale).sp else 15.sp
        val sourceSize = if (isTablet) (12 * scale).sp else 12.sp
        val tagSize = if (isTablet) (10 * scale).sp else 10.sp


        // 반응형 높이
        val targetHeight = when {
            w <= 430.dp -> 105.dp            // 일반 스마트폰 (S25 포함)
            w >= 600.dp -> 135.dp            // 태블릿
            else -> lerp(                    // 대형 스마트폰 (울트라 등)
                105.dp,
                135.dp,
                ((w - 430.dp) / (600.dp - 430.dp)).coerceIn(0f, 1f)
            )
        }

        // shadow + 배경 제대로 보이게 정석 패턴
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = Color(0x22000000),
                )
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .height(targetHeight)
                .clickable { onClick(link.url) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {

                // 이미지 박스
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {

                    ShimmerSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    if (!link.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(ctx)
                                .data(link.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_detail_image_url_null),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(Modifier.width(18.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {

                    // 제목: 1줄로 고정 + ... 처리
                    Text(
                        text = link.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = Paperlogy,
                            fontWeight = FontWeight(500),
                            fontSize = titleSize
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))

                    // 카테고리
                    link.categories?.take(2)?.let { cats ->
                        Row {
                            cats.forEachIndexed { i, c ->
                                TagChip(c)
                                if (i != cats.lastIndex) Spacer(Modifier.width(11.dp))
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    // 출처
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = sourceLabel,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = Paperlogy,
                                fontWeight = FontWeight(600),
                                fontSize = sourceSize,
                                color = Color(0xFF43454B)
                            )
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun TagChip(text: String) {

    Box(
        modifier = Modifier
            .height(20.dp)
            .background(
                color = Color(0xFFF5F6F9),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(start = 6.dp, top = 3.dp, end = 6.dp, bottom = 3.dp)
    ) {
        Text(   //TODO : 이거 글자, 박스 작음.
            text = text,
            style = TextStyle(
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF87898F)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun RecommendedLinkCardSkeleton() { //TODO : 디자이너에게 실제 확인 후 컬러 물어보기!
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 9.dp)
    ) {

        val w = maxWidth

        // 반응형 높이
        val targetHeight = when {
            w <= 430.dp -> 105.dp
            w >= 600.dp -> 135.dp
            else -> lerp(
                105.dp,
                135.dp,
                ((w - 430.dp) / (600.dp - 430.dp)).coerceIn(0f, 1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(targetHeight)
                .clip(RoundedCornerShape(12.dp))
                .shimmer()
                .background(Color(0xFFEDEDED))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                // 이미지 스켈레톤
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCDCDC))
                )

                Spacer(Modifier.width(18.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCDCDC))
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCDCDC))
                    )

                    Spacer(Modifier.height(12.dp))

                    Row {
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCDCDC))
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCDCDC))
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCDCDC))
                    )
                }
            }
        }
    }
}

// URL -> host 추출 (domain 비어있을 때 보강)
private fun String.toHost(): String? = try {
    URI(this).host
} catch (_: Exception) { null }

/* ================== Preview ================== */

@Preview(showBackground = true)
@Composable
fun PreviewCurationRecommendedLinksSection() {
    val uri = LocalUriHandler.current
    val demo = listOf(
        RecommendedLink(
            isInternal = true,
            userLinkuId = 11L,
            title = "성신여대 홈페이지",
            url = "https://www.sungshin.ac.kr/sites/main_kor/main.jsp",
            imageUrl = null,                         // 썸네일 없으면 아이콘만 표시
            domain = "sungshin.ac.kr",
            domainImageUrl = null,
            categories = listOf("기타")
        ),
        RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "나만의 자기관리 체크리스트 만들기 - Adobe",
            url = "https://www.adobe.com/kr/acrobat/hub/create-a-self-care-checklist.html",
            imageUrl = "https://picsum.photos/200",
            domain = "adobe.com",
            domainImageUrl = null,
            categories = listOf("정보")
        )
    )

    CurationRecommendedLinksSection(
        links = demo,
        loading = false,
        onRetry = {},
        onClick = { url -> runCatching { uri.openUri(url) } }
    )
}

