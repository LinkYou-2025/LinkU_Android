package com.example.curation.ui


/*
* 역할: "세나님을 위한 8월의 큐레이션" 단일 추천 섹션
API로 받아온 하나의 큐레이션 이미지 데이터를 표시

*/


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.curation.CurationViewModel
import com.example.curation.R
import com.example.curation.Paperlogy
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CurationHighlightSection(
    modifier: Modifier = Modifier,
    viewModel: CurationViewModel = hiltViewModel(),
    onOpenDetail: (() -> Unit)? = null
) {
    val isGenerating by viewModel.isGenerating.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val recentCuration by viewModel.recentCuration.collectAsState()

    //  추가: 하트 상태/로딩 상태
    val highlightLiked by viewModel.highlightLiked.collectAsState()
    val likeBusy by viewModel.likeBusy.collectAsState()


    LaunchedEffect(viewModel) {
        if (recentCuration == null && !isGenerating) {
            viewModel.loadMonthlyCuration()
        }
    }

    when {
        // 1) 데이터가 있으면 무조건 이미지 먼저!
        recentCuration != null -> {
            Log.d("CurationUI", "큐레이션 표시 - URL: ${recentCuration!!.thumbnailUrl}")
            HighlightCardOnlyImage(
                imageUrl = recentCuration!!.thumbnailUrl,
                liked = highlightLiked,                  //  VM 상태 전달
                likeBusy = likeBusy,                     // 중복탭 방지
                onToggleLike = { viewModel.toggleHighlightLike() },
                onCardClick = onOpenDetail,            //  여기서 그대로 전달
                modifier = modifier
            )
        }

        // 2) 그 다음 로딩
        isGenerating -> {
            Log.d("CurationUI", "큐레이션 생성 중")
            // 필요하면 로딩용 임시 카드도 가능:
            // HighlightCardWithFallback(imageRes = R.drawable.img_trump_card_main, modifier = modifier)
            Text(text = "큐레이션 생성 중...", modifier = Modifier.padding(horizontal = 20.dp))
        }

        // 3) 에러
        errorMessage != null -> {
            Log.w("CurationUI", "오류 발생: $errorMessage")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_trump_card_main,
                modifier = modifier
            )
        }

        // 4) 아무 것도 없을 때
        else -> {
            Log.d("CurationUI", "큐레이션 없음")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_trump_card_main,
                modifier = modifier
            )
        }
    }
}

//@Composable
//fun HighlightCardOnlyImage(imageUrl: String?) {
//    val painter = rememberAsyncImagePainter(model = imageUrl)
//
//    Image(
//        painter = painter,
//        contentDescription = "추천 큐레이션 이미지",
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .padding(top = 16.dp, start = 0.dp, end = 0.dp) // ← 위 여백 + 좌우 여백
//            .clip(RoundedCornerShape(12.dp))
//    )
//}

@Composable
fun HighlightCardOnlyImage(
    imageUrl: String?,
    liked: Boolean?,                 // null이면 아직 모르는 상태(로딩)
    likeBusy: Boolean,
    onToggleLike: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    //var liked by remember { mutableStateOf(false) }
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var aspect by remember { mutableStateOf<Float?>(null) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 배경 이미지 (API 이미지)
//        val painter = rememberAsyncImagePainter(model = imageUrl)
//        Image(
//            painter = painter,
//            contentDescription = "추천 큐레이션 이미지",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
        // 배경 이미지 + 카드 이동 클릭은 배경에만!
        val imagePainter = rememberAsyncImagePainter(model = imageUrl)
        Image(
            painter = imagePainter,
            contentDescription = "추천 큐레이션 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable(                 // 배경만 이동
                    enabled = !likeBusy,
                    onClick = { onCardClick?.invoke() }
                )
        )

//        // 좋아요 하트
//        Icon(
//            painter = painterResource(id = if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline),
//            contentDescription = "좋아요",
//            tint = Color.White,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(12.dp)
//                .size(24.dp)
//                .clickable {
//                    liked = !liked
//
//                    // TODO: 좋아요 상태 서버에 반영 (2025-08-09 예정)
//                    // if (liked) {
//                    //     viewModel.likeCuration(curationId)
//                    // } else {
//                    //     viewModel.unlikeCuration(curationId)
//                    // }
//                }
//        )
        // null이면 outline 유지(또는 alpha 처리로 로딩감)
        val isLiked = (liked == true)
//        Icon(
//            painter = painterResource(id = if (isLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline),
//            contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
//            tint = Color.White,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(12.dp)
//                .size(24.dp)
//                .clickable(enabled = !likeBusy) { onToggleLike() }
//        )
        // 하트는 완전히 별도의 clickable (부모 클릭과 분리)
        Icon(
            painter = painterResource(id = if (isLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline),
            contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(24.dp)
                .clickable(
                    // ripple/전파 최소화
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    enabled = !likeBusy,
                    onClick = { onToggleLike() }     // 토글만 수행
                )
        )
    }
}


//날짜 불러오기
fun getCurrentKoreanCurationDate(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월호", Locale.KOREAN)
    return today.format(formatter)
}

@Composable
fun HighlightCardWithFallback(
    imageRes: Int,
    title: String = "링큐 큐레이션",
    //date: String = "2025년 8월호"
    date: String = getCurrentKoreanCurationDate(),
    modifier: Modifier = Modifier
) {
    var liked by remember { mutableStateOf(false) } // 좋아요 상태 기억
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 16.dp) // ← 위 여백 + 좌우 여백
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$title 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 좋아요 하트 (우측 상단)
        Icon(
            painter = painterResource(id = if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline),
            contentDescription = "좋아요",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(24.dp)
                .clickable { liked = !liked } // 클릭 시 토글!
        )

        // 텍스트 (왼쪽 하단)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            )
            Text(
                text = date,
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
fun PreviewHighlightCardWithFallback() {
    HighlightCardWithFallback(imageRes = R.drawable.img_trump_card_main)
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationHighlightSection() {
    HighlightCardOnlyImage(
        imageUrl = "https://dummyimage.com/600x400/000/fff&text=Preview",
        liked = false,
        likeBusy = false,
        onToggleLike = {}
    )
}


//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.curation.CurationItem
//import com.example.curation.Paperlogy
//import com.example.curation.R
//import com.example.curation.CurationViewModel
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//
//
//@Composable
//fun HighlightCard(item: CurationItem) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .clip(RoundedCornerShape(12.dp))
//    ) {
//        // 배경 이미지
//        Image(
//            painter = painterResource(id = item.imageRes),
//            contentDescription = item.title,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(12.dp))
//        )
//
//        // 좋아요 아이콘 (우측 상단)
//        Icon(
//            painter = painterResource(id = R.drawable.ic_heart_outline),
//            contentDescription = "좋아요",
//            tint = Color.White,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(12.dp)
//                .size(24.dp)
//        )
//
//        //  제목 & 날짜 (왼쪽 하단)
//        Column(
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(start = 16.dp, bottom = 16.dp, end = 80.dp) // 오른쪽 여백 확보
//        ) {
//            Text(
//                text = item.title,
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    color = Color.White
//                )
//            )
//            Text(
//                text = item.date,
//                style = MaterialTheme.typography.bodyMedium.copy(
//                    fontFamily = Paperlogy,
//                    fontSize = 14.sp,
//                    color = Color.White
//                )
//            )
//        }
//
//        // "보러가기 >" → Box의 직접 자식으로 배치 (align 정상 동작)
//        Text(
//            text = "보러가기 >",
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Medium,
//                fontSize = 14.sp,
//                color = Color.White
//            ),
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun CurationHighlightSection(
//    nickname: String,
//    viewModel: CurationViewModel = hiltViewModel()
//) {
//    val isGenerating by viewModel.isGenerating.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    // 앱 시작 시 한 번만 호출
//    LaunchedEffect(Unit) {
//        viewModel.generateMonthlyCuration(userId = 1L) // 로그인 전 → 임시 userId
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(
//            text = "${nickname}을 위한 8월의 큐레이션",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                fontSize = 22.sp
//            )
//        )
//
//        when {
//            isGenerating -> Text("큐레이션을 생성 중입니다...")
//            errorMessage != null -> Text("오류: $errorMessage")
//            else -> {
//                // 성공 시 UI 표시
//                val highlight = CurationItem(
//                    title = "트럼프 큐레이션",
//                    date = "2025년 8월호",
//                    imageRes = R.drawable.img_trump_card,
//                    liked = false
//                )
//                Spacer(modifier = Modifier.height(6.dp))
//                HighlightCard(item = highlight)
//            }
//        }
//    }
//}
//
////@Composable
////fun CurationHighlightSection(nickname: String) {
////    Column(modifier = Modifier.padding(16.dp)) {
////        Text(
////            text = "${nickname}을 위한 8월의 큐레이션",
////            style = MaterialTheme.typography.titleMedium.copy(
////                fontFamily = Paperlogy,
////                fontWeight = FontWeight.Bold,
////                fontSize = 22.sp
////            )
////        )
////
////        val highlight = CurationItem(
////            title = "트럼프 큐레이션",
////            date = "2025년 8월호",
////            imageRes = R.drawable.img_trump_card,
////            liked = false
////        )
////
////        Spacer(modifier = Modifier.height(6.dp))
////        HighlightCard(item = highlight)
////    }
////}
//
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewCurationHighlightSection() {
//    CurationHighlightSection(nickname = "세나님")
//}