package com.example.curation



//역할: 사용자가 좋아요한 큐레이션 리스트
//
//기능: 세로로 나열된 큐레이션 카드들 렌더링
//
//관심사: 데이터 바인딩, 좋아요 표시, 목록 UI



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.curation.model.CurationItem

@Composable
fun CurationLikedSection(nickname: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("$nickname이 좋아요한 큐레이션", style = MaterialTheme.typography.titleMedium)

        val likedCurations = listOf(
            CurationItem("트럼프 큐레이션", "2025년 6월호", R.drawable.trump_card, liked = true),
            CurationItem("트럼프 큐레이션", "2025년 7월호", R.drawable.trump_card, liked = true)
        )

        likedCurations.forEach {
            LikedCurationCard(it)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun LikedCurationCard(item: CurationItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(painter = painterResource(id = item.imageRes), contentDescription = null)
        Text(text = item.title)
        Text(text = item.date)
    }
}