package com.example.curation.ui


/*
* 역할: "세나님을 위한 8월의 큐레이션" 단일 추천 섹션

기능: 백엔드에서 가져온 하나의 추천 큐레이션을 보여줌 (가장 강조됨)

관심사: API로 받아온 하나의 큐레이션 데이터를 표시

*/
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import com.example.curation.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.curation.CurationItem

@Composable
fun CurationHighlightSection(nickname: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "${nickname}을 위한 8월의 큐레이션",
            style = MaterialTheme.typography.titleMedium
        )

        val highlight = CurationItem(
            title = "트럼프 큐레이션",
            date = "2025년 8월호",
            imageRes = R.drawable.img_trump_card,
            liked = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        HighlightCard(item = highlight)
    }
}

@Composable
fun HighlightCard(item: CurationItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(painter = painterResource(id = item.imageRes), contentDescription = null)
        Text(text = item.title)
        Text(text = item.date)
    }
}