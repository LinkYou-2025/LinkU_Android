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
import com.example.curation.ui.UICurationItem
import com.example.curation.R
import com.example.curation.Paperlogy
import androidx.compose.foundation.clickable

@Composable
fun CurationLikedSection(nickname: String) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        // 제목
        Text(
            text = "${nickname}님이 좋아요한 큐레이션",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .padding(
                    start = 24.dp,   // ← 왼쪽 24
                    top = 25.dp,     // ← 위 25
                    bottom = 18.dp   // ← 아래 18
                )

        )

        Spacer(modifier = Modifier.height(8.dp))

        // 좋아요 리스트
        val likedCurations = listOf(
            UICurationItem("링큐 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
            UICurationItem("링큐 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
            UICurationItem("링큐 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(likedCurations) { item ->
                LikedCurationCard(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)    // ← 모든 카드 좌우 20dp
                )
                Spacer(modifier = Modifier.height(10.dp)) // ← 카드 간 간격 10dp
            }
        }
    }
}

@Composable
fun LikedCurationCard(
    item: UICurationItem,
    modifier: Modifier = Modifier,
    onCardClick: (() -> Unit)? = null,
    onHeartClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = onCardClick != null) { onCardClick?.invoke() } // 카드 탭 → 상세
    ) {
        // 1) 이미지: URL > 리소스 > 폴백
        val painter = when {
            item.imageUrl?.isNotBlank() == true ->
                coil3.compose.rememberAsyncImagePainter(model = item.imageUrl)
            item.imageRes != null ->
                painterResource(id = item.imageRes)
            else ->
                painterResource(id = R.drawable.img_trump_card)
        }

        Image(
            painter = painter,
            contentDescription = null, // 텍스트 오버레이를 빼므로 CD도 생략
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2) 좋아요 하트만 유지 (우상단)
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 18.dp)
                .size(16.dp)
                .clickable { onHeartClick?.invoke() }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCurationLikedSection() {
    CurationLikedSection(nickname = "세나님")
}