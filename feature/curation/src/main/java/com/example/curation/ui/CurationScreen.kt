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
import androidx.compose.foundation.layout.width
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
import com.example.curation.CurationItem
import com.example.curation.CurationRecommendedLinksSection
import com.example.curation.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.curation.LikedCurationCard
import com.example.curation.Paperlogy


@Composable
fun CurationScreen() {

    // 하드코딩된 닉네임 (서버 연동 시 이 변수만 교체)
    val nickname = "세나"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        //  상단 바
        CurationTopBar()
        //Spacer(modifier = Modifier.height(16.dp))

        //  1. 하이라이트 섹션
        CurationHighlightSection(nickname = "${nickname}님")
        Spacer(modifier = Modifier.height(2.dp))

        // 2. 추천 링크 (고정)
        CurationRecommendedLinksSection()
        Spacer(modifier = Modifier.height(16.dp))

        // 3. 좋아요한 큐레이션 (이 부분만 스크롤)
        Text(
            text = "${nickname}님이 좋아요 한 큐레이션",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // LazyColumn으로 내부 스크롤
        val likedCurations = listOf(
            CurationItem("트럼프 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
            CurationItem("트럼프 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
            CurationItem("트럼프 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 영역만큼 스크롤 가능
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(likedCurations) { item ->
                LikedCurationCard(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CurationTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_linkukor),
                contentDescription = "링큐 로고",
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(0.01f))

            Image(
                painter = painterResource(id = R.drawable.ic_alarm),
                contentDescription = "알림",
                modifier = Modifier.size(24.dp)
            )
        }

        //Spacer(modifier = Modifier.height(4.dp))

        // 빠른 링크 검색 (Figma 그라데이션 적용)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2C6FFF), // 시작: 파랑
                            Color(0xFFC800FF)  // 끝: 보라
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp)
                .clickable { /* TODO: 검색 액션 */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logo_white),
                contentDescription = "링크 아이콘",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "빠른 링크 검색",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationScreen() {
    CurationScreen()
}
