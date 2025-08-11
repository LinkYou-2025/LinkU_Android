package com.example.curation


// 역할: 추천 링크 리스트 섹션 + 카드
// 기능: 서버에서 받은 RecommendedLink(title, imageUrl, url, categories, domain)를 표시
// 관심사: 리스트 반복, 썸네일/아이콘 표시, 클릭 콜백

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun CurationRecommendedLinksSection(
    modifier: Modifier = Modifier,
    links: List<RecommendedLink>,
    loading: Boolean,
    onRetry: () -> Unit = {},
    onClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = "추천 링크",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
        )
        Spacer(Modifier.height(8.dp))

        when {
            loading && links.isEmpty() -> {
                repeat(2) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F3F3))
                    )
                    Spacer(Modifier.height(8.dp))
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
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "새로고침",
                        color = Color(0xFFCB59EB),
                        modifier = Modifier.clickable { onRetry() }
                    )
                }
            }
            else -> {
                links.take(9).forEach { link ->
                    RecommendedLinkCard(link = link, onClick = onClick)
                    Spacer(Modifier.height(8.dp))
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
    // 서버가 domain을 안 줄 수도 있으니 URL에서 호스트 보강
    val domain = link.domain ?: link.url.toHost()
    val iconRes = resolveSourceIcon(domain)
    val sourceLabel = resolveSourceLabel(domain)

    val ctx = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
            .clickable { onClick(link.url) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!link.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(link.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_detail_image_url_null),
                contentDescription = "이미지 없음",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = link.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = Paperlogy, fontWeight = FontWeight.Medium, fontSize = 15.sp
                ),
                maxLines = 2
            )

            Spacer(Modifier.height(4.dp))

            // 카테고리 뱃지 (최대 2개)
            link.categories?.take(2)?.let { cats ->
                Row {
                    cats.forEachIndexed { i, c ->
                        TagChip(c)
                        if (i != cats.lastIndex) Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // 출처: 아이콘 + 라벨
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "출처 아이콘",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = sourceLabel,
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

// URL -> host 추출 (domain 비어있을 때 보강)
private fun String.toHost(): String? = try {
    java.net.URI(this).host
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


//역할: 추천 링크 리스트를 보여주는 섹션
//
//기능: 뉴스, 쇼핑, 팁 등의 링크 카드들을 보여줌
//
//관심사: 리스트 반복, 터치 가능 카드 뷰
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.curation.Paperlogy
//import com.example.curation.R
//
//
////Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp))
//@Composable
//fun CurationRecommendedLinksSection(modifier: Modifier = Modifier) {
//    Column(modifier = modifier) {
//        Text(
//            text = "추천 링크",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp
//            )
//        )
//
//        val links = listOf(
//            LinkItem("서울 근교 드라이브 코스 TOP5", R.drawable.img_seoul_card, "https://example.com/1"),
//            LinkItem("감성 무드등 추천", R.drawable.img_travel_card, "https://example.com/2")
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        links.forEach { link ->
//            RecommendedLinkCard(link)
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//    }
//}
//
//@Composable
//fun RecommendedLinkCard(item: LinkItem) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
//            .clickable { /* TODO */ }
//            .padding(4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Image(
//            painter = painterResource(id = item.imageRes),
//            contentDescription = null,
//            modifier = Modifier
//                .size(60.dp)
//                .clip(RoundedCornerShape(8.dp))
//        )
//
//        Column(
//            modifier = Modifier
//                .padding(start = 12.dp)
//                .fillMaxWidth()
//        ) {
//            Text(
//                text = item.title,
//                style = MaterialTheme.typography.bodyLarge.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 15.sp
//                )
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Row {
//                TagChip("여행")
//                Spacer(modifier = Modifier.width(6.dp))
//                TagChip(if (item.title.contains("드라이브")) "힐링" else "행복")
//            }
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            // 출처: 아이콘 + 텍스트
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                val iconRes = if (item.title.contains("드라이브")) R.drawable.ic_naver else R.drawable.ic_blog
//                Image(
//                    painter = painterResource(id = iconRes),
//                    contentDescription = "출처 아이콘",
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = if (item.title.contains("드라이브")) "Naver" else "Blog",
//                    style = MaterialTheme.typography.bodySmall.copy(
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 12.sp,
//                        color = Color(0xFF43454B)
//                    )
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TagChip(text: String) {
//    Box(
//        modifier = Modifier
//            .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
//            .padding(horizontal = 6.dp, vertical = 2.dp)
//    ) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodySmall.copy(
//                fontFamily = Paperlogy,
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewCurationRecommendedLinksSection() {
//    CurationRecommendedLinksSection()
//}