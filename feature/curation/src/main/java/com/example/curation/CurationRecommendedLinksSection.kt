package com.example.curation


//역할: 추천 링크 리스트를 보여주는 섹션
//
//기능: 뉴스, 쇼핑, 팁 등의 링크 카드들을 보여줌
//
//관심사: 리스트 반복, 터치 가능 카드 뷰

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.curation.R

@Composable
fun CurationRecommendedLinksSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("추천 링크", style = MaterialTheme.typography.titleMedium)

        val links = listOf(
            LinkItem("서울 근교 드라이브 코스", R.drawable.img_seoul_card, "https://example.com/1"),
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
            .clickable { /* TODO: Handle click */ }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = item.title)
    }
}