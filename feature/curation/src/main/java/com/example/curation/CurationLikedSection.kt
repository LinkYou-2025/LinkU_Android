package com.example.curation



//역할: 사용자가 좋아요한 큐레이션 리스트
//
//기능: 세로로 나열된 큐레이션 카드들 렌더링
//
//관심사: 데이터 바인딩, 좋아요 표시, 목록 UI




import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.Paperlogy

@Composable
fun CurationLikedSection(nickname: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 제목
        Text(
            text = "${nickname}님이 좋아요 한 큐레이션",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(start = 12.dp)

        )

        Spacer(modifier = Modifier.height(8.dp))

        // 좋아요 리스트
        val likedCurations = listOf(
            CurationItem("링큐 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
            CurationItem("링큐 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
            CurationItem("링큐 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
        )

        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(likedCurations) { item ->
                LikedCurationCard(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun LikedCurationCard(item: CurationItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 좋아요 하트 (우측 상단)
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = "좋아요",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(24.dp)
        )

        // 텍스트 (왼쪽 하단)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = Paperlogy,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }

        // 보러가기 > (오른쪽 하단)
        Text(
            text = "보러가기 >",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCurationLikedSection() {
    CurationLikedSection(nickname = "세나님")
}