package com.example.curation


//역할: 추천 링크 리스트를 보여주는 섹션
//
//기능: 뉴스, 쇼핑, 팁 등의 링크 카드들을 보여줌
//
//관심사: 리스트 반복, 터치 가능 카드 뷰

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.Paperlogy
import com.example.curation.R


//Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp))
@Composable
fun CurationRecommendedLinksSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "추천 링크",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        val links = listOf(
            LinkItem("서울 근교 드라이브 코스 TOP5", R.drawable.img_seoul_card, "https://example.com/1"),
            LinkItem("감성 무드등 추천", R.drawable.img_travel_card, "https://example.com/2")
        )

        Spacer(modifier = Modifier.height(8.dp))

        links.forEach { link ->
            RecommendedLinkCard(link)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecommendedLinkCard(item: LinkItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
            .clickable { /* TODO */ }
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

@Preview(showBackground = true)
@Composable
fun PreviewCurationRecommendedLinksSection() {
    CurationRecommendedLinksSection()
}