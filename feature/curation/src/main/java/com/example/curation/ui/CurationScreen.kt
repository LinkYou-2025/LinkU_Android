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
import com.example.curation.CurationRecommendedLinksSection
import com.example.curation.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.curation.CurationViewModel
import com.example.curation.LikedCurationCard
import com.example.curation.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.R as Res
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



@Composable
fun CurationScreen(
    viewModel: CurationViewModel = hiltViewModel(),
    onOpenDetail: () -> Unit = {}
) {
    val nickname by viewModel.nickname.collectAsState()

    // 현재 월을 "8월" 같은 형식으로 가져옴
    val currentMonth = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("M월", Locale.KOREAN))
    }

    // 닉네임 불러오기
    LaunchedEffect(Unit) {
        viewModel.loadNickname()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // ✅ 홈스크린처럼 TopBar를 item {} 안에 배치
        item {
            CurationTopBar()
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // 1. 큐레이션 하이라이트 텍스트
                Text(
                    text = "${nickname}님을 위한 ${currentMonth}의 큐레이션",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black
                )
                CurationHighlightSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDetail() }
                )

                Spacer(modifier = Modifier.height(20.dp))



                // 2. 추천 링크
                // 내부에서 padding 제거하고 modifier로 전달
                CurationRecommendedLinksSection(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))

                // 3. 좋아요한 큐레이션 텍스트
                Text(
                    text = "${nickname}님이 좋아요 한 큐레이션",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = LocalColorTheme.current.black
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 4. 좋아요 큐레이션 카드 리스트
        val likedCurations = listOf(
            UICurationItem("트럼프 큐레이션", "2025년 7월호", Res.drawable.img_trump_card, liked = true),
            UICurationItem("트럼프 큐레이션", "2025년 6월호", Res.drawable.img_trump_card, liked = true),
            UICurationItem("트럼프 큐레이션", "2025년 5월호", Res.drawable.img_trump_card, liked = true)
        )

        // 좋아요한 큐레이션 리스트
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                likedCurations.forEach { item ->
                    LikedCurationCard(item)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun CurationTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColorTheme.current.white)
    ) {
        // 🔹 상단 로고 + 알림 아이콘
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.38.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = Res.drawable.ic_linkukor),
                contentDescription = "링큐 로고",
                modifier = Modifier
                    .height(24.dp)
                    .padding(start = 19.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = Res.drawable.ic_alarm),
                contentDescription = "알림",
                modifier = Modifier
                    .padding(end = 13.8.dp)
                    .height(27.18.dp),
                tint = LocalColorTheme.current.gray[300]
            )
        }

        // 🔹 빠른 링크 검색 박스
        Row(
            modifier = Modifier
                .padding(top = 15.dp, start = 16.dp, end = 16.dp)
                .height(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = Basic.maincolor)
                    .clickable { /* TODO: 검색 액션 */ }
                    .padding(horizontal = 18.51.dp, vertical = 15.dp)
            ) {
                Icon(
                    painter = painterResource(id = Res.drawable.ic_logo_white),
                    contentDescription = null,
                    modifier = Modifier.height(17.dp),
                    tint = LocalColorTheme.current.white
                )

                Text(
                    text = "빠른 링크 검색",
                    color = LocalColorTheme.current.white,
                    modifier = Modifier.padding(start = 36.98.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = Paperlogy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
@Composable
fun CurationScreenPreviewable(nickname: String = "홍길동") {
    val likedCurations = listOf(
        UICurationItem("트럼프 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
        UICurationItem("트럼프 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
        UICurationItem("트럼프 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        // 상단 바
        CurationTopBar()

        Spacer(modifier = Modifier.height(2.dp))

        // 하이라이트 섹션
        //CurationHighlightSection(nickname = "${nickname}님")

        Spacer(modifier = Modifier.height(16.dp))

        // 추천 링크 섹션 (고정)
        CurationRecommendedLinksSection()

        Spacer(modifier = Modifier.height(16.dp))

        // 좋아요한 큐레이션 텍스트
        Text(
            text = "${nickname}님이 좋아요 한 큐레이션",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 좋아요한 큐레이션 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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

@Preview(showBackground = true)
@Composable
fun PreviewCurationScreen() {
    MaterialTheme {
        CurationScreenPreviewable()
    }
}

//@Composable
//fun CurationScreen(
//    viewModel: CurationViewModel = hiltViewModel()
//) {
//    val nickname by viewModel.nickname.collectAsState()
//
//    // 닉네임 로딩
//    LaunchedEffect(Unit) {
//        viewModel.loadNickname()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(bottom = 16.dp)
//    ) {
//        // 1. 상단바 (로고 + 빠른 링크 검색)
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 50.dp), // Status bar height 고려
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_linkukor),
//                    contentDescription = "링큐 로고",
//                    modifier = Modifier
//                        .size(50.dp)
//                        .padding(start = 8.dp)
//                )
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_alarm),
//                    contentDescription = "알림",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.LightGray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp)
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        ),
//                        shape = RoundedCornerShape(24.dp)
//                    )
//                    .padding(horizontal = 16.dp)
//                    .clickable {
//                        // TODO: 검색 클릭 시 액션
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_logo_white),
//                    contentDescription = "링크 아이콘",
//                    tint = Color.White,
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Text(
//                    text = "빠른 링크 검색",
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 16.sp
//                    ),
//                    color = Color.White
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 2. 하이라이트 섹션 (닉네임 반영)
//        CurationHighlightSection(nickname = "${nickname}님")
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 3. 추천 링크 (고정)
//        CurationRecommendedLinksSection()
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 4. 좋아요한 큐레이션
//        Text(
//            text = "${nickname}님이 좋아요 한 큐레이션",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp
//            ),
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        val likedCurations = listOf(
//            CurationItem("트럼프 큐레이션", "2025년 7월호", R.drawable.img_trump_card, liked = true),
//            CurationItem("트럼프 큐레이션", "2025년 6월호", R.drawable.img_trump_card, liked = true),
//            CurationItem("트럼프 큐레이션", "2025년 5월호", R.drawable.img_trump_card, liked = true)
//        )
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .padding(horizontal = 16.dp),
//            contentPadding = PaddingValues(vertical = 8.dp)
//        ) {
//            items(likedCurations) { item ->
//                LikedCurationCard(item = item)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }
//}
//@Composable
//fun CurationTopBar() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(LocalColorTheme.current.white)
//    ) {
//        // 🔹 상단 로고 + 알림 아이콘
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 50.38.dp, start = 16.dp, end = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = com.example.design.R.drawable.ic_linkukor),
//                contentDescription = "링큐 로고",
//                modifier = Modifier
//                    .height(24.dp)
//                    .padding(start = 19.dp)
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Icon(
//                painter = painterResource(id = com.example.design.R.drawable.ic_alarm),
//                contentDescription = "알림",
//                modifier = Modifier
//                    .padding(end = 13.8.dp)
//                    .height(27.18.dp),
//                tint = LocalColorTheme.current.gray[300]
//            )
//        }
//
//        // 🔹 빠른 링크 검색 박스
//        Row(
//            modifier = Modifier
//                .padding(top = 15.dp, start = 16.dp, end = 16.dp)
//                .height(48.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(18.dp))
//                    .background(brush = Basic.maincolor)
//                    .clickable { /* TODO: 검색 액션 */ }
//                    .padding(horizontal = 18.51.dp, vertical = 15.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_logo_white),
//                    contentDescription = null,
//                    modifier = Modifier.height(17.dp),
//                    tint = LocalColorTheme.current.white
//                )
//
//                Text(
//                    text = "빠른 링크 검색",
//                    color = LocalColorTheme.current.white,
//                    modifier = Modifier.padding(start = 36.98.dp),
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontFamily = Paperlogy,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//            }
//        }
//    }
//}
//@Composable
//fun CurationTopBar() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 0.dp)
//    ) {
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_linkukor),
//                contentDescription = "링큐 로고",
//                modifier = Modifier
//                    .size(50.dp)
//                    .padding(start = 8.dp)
//            )
//
//            Spacer(modifier = Modifier.weight(0.01f))
//
//            Image(
//                painter = painterResource(id = R.drawable.ic_alarm),
//                contentDescription = "알림",
//                modifier = Modifier.size(24.dp)
//            )
//        }
//
//        //Spacer(modifier = Modifier.height(4.dp))
//
//        // 빠른 링크 검색 (Figma 그라데이션 적용)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFF2C6FFF), // 시작: 파랑
//                            Color(0xFFC800FF)  // 끝: 보라
//                        )
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .padding(horizontal = 16.dp)
//                .clickable { /* TODO: 검색 액션 */ },
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_logo_white),
//                contentDescription = "링크 아이콘",
//                tint = Color.White,
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = "빠른 링크 검색",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                ),
//                color = Color.White
//            )
//        }
//    }
//}
//preview

