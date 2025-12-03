package com.example.curation.ui.list_card



//역할: 사용자가 좋아요한 큐레이션 리스트
//
//기능: 세로로 나열된 큐레이션 카드들 렌더링
//
//관심사: 데이터 바인딩, 좋아요 표시, 목록 UI
//큐레이션 메인 페이지(디테일 아님.) 밑에 좋아요 한 리스트까 뜨는 ui임.


import androidx.compose.ui.unit.lerp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import coil3.compose.rememberAsyncImagePainter
import com.example.curation.ui.util.rememberScaleFactor
import com.example.curation.ui.util.shimmer

@Composable
fun CurationLikedSection(nickname: String) {

    val scale = rememberScaleFactor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            //.padding(horizontal = (20 * scale).dp)
    ) {
        // 제목
        Text(
            text = "${nickname}님이 좋아요한 큐레이션",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = (20 * scale).sp
            ),
            modifier = Modifier
                .padding(
                    start = (24 * scale).dp,
                    top = (25 * scale).dp,
                    bottom = (18 * scale).dp
                )
        )

        Spacer(modifier = Modifier.height((8 * scale).dp))

        // 좋아요 리스트 only 프리뷰용.
        val likedCurations = listOf(
            UICurationItem("링큐 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
            UICurationItem("링큐 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
            UICurationItem("링큐 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = (24 * scale).dp)
        ) {
            items(likedCurations) { item ->
                LikedCurationCard(
                    item = item,
                    modifier = Modifier
                        .padding(horizontal = (20 * scale).dp)
                )
                Spacer(modifier = Modifier.height((10 * scale).dp))
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
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onCardClick != null) { onCardClick?.invoke() }
    ) {
        val w = maxWidth
        val isTablet = w >= 600.dp
        val scale = rememberScaleFactor()

        /** ✔ 카드 높이 반응형 */
        val targetHeight = when {
            w <= 430.dp -> 120.dp                    // 일반 스마트폰
            w >= 600.dp -> 160.dp                    // 태블릿
            else -> lerp(                             // 울트라 등
                120.dp,
                160.dp,
                ((w - 430.dp) / (600.dp - 430.dp)).coerceIn(0f, 1f)
            )
        }

        val corner = if (isTablet) 18.dp * scale else 18.dp

        Box(
            modifier = Modifier
                .height(targetHeight)
                .clip(RoundedCornerShape(corner))
        ) {
            /** 배경 이미지 */
            Image(
                painter = when {
                    item.imageUrl?.isNotBlank() == true ->
                        rememberAsyncImagePainter(item.imageUrl)
                    item.imageRes != null ->
                        painterResource(item.imageRes)
                    else ->
                        painterResource(R.drawable.img_trump_card)
                },
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            /** ✔ 위치 비율(Figma 기준비 적용) */
            val titleX = w * (26f / 372f)
            val titleY = targetHeight * (54f / 120f)
            val dateY  = targetHeight * (80f / 120f)

            val gotoX  = w * (1f - (22f / 372f))
            val gotoY  = targetHeight * (100f / 120f)

            /** ✔ 글자 크기: 폰 고정, 태블릿만 Scale */
            val titleFont = if (isTablet) (20 * scale).sp else 20.sp
            val dateFont  = if (isTablet) (16 * scale).sp else 16.sp
            val gotoFont  = if (isTablet) (13 * scale).sp else 13.sp

            /** 제목 */
            Text(
                text = item.title,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = titleFont,
                color = Color.White,
                modifier = Modifier.offset(titleX, titleY)
            )

            /** 날짜 */
            Text(
                text = item.date,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = dateFont,
                color = Color.White,
                modifier = Modifier.offset(titleX, dateY)
            )

            /** 보러가기 */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = if (isTablet) 24.dp * scale else 24.dp,
                        bottom = if (isTablet) 16.dp * scale else 16.dp
                    )
                    .clickable { onCardClick?.invoke() }
            ) {
                Text(
                    "보러가기",
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = gotoFont,
                    color = Color.White
                )

                Spacer(Modifier.width(6.dp))

                Image(
                    painter = painterResource(R.drawable.ic_curation_vector),
                    contentDescription = null,
                    modifier = Modifier.size(if (isTablet) 10.dp * scale else 10.dp)
                )
            }

            /** 하트 */
            Icon(
                painter = painterResource(R.drawable.ic_heart),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(if (isTablet) 18.dp * scale else 18.dp)
                    .size(if (isTablet) 16.dp * scale else 16.dp)
                    .clickable { onHeartClick?.invoke() }
            )
        }
    }
}
@Composable
fun LikedCurationSkeleton() {
    val scale = rememberScaleFactor()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((120 * scale).dp)
            .padding(horizontal = (20 * scale).dp)
            .clip(RoundedCornerShape((18 * scale).dp))
            .shimmer()
            .background(Color(0xFFEDEDED))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCurationLikedSection() {
    CurationLikedSection(nickname = "세나님")
}