package com.example.curation.ui


/*
* 역할: "세나님을 위한 8월의 큐레이션" 단일 추천 섹션
API로 받아온 하나의 큐레이션 이미지 데이터를 표시

*/

import coil3.compose.rememberAsyncImagePainter
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import androidx.compose.runtime.collectAsState

import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
@Composable
fun CurationHighlightSection(
    modifier: Modifier = Modifier,
    viewModel: CurationViewModel, //디폴트 제거(좋아요)
    //viewModel: CurationViewModel = hiltViewModel(),
    onOpenDetail: (() -> Unit)? = null
) {
    // 수집은 lifecycle-aware 권장
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val recentCuration by viewModel.recentCuration.collectAsStateWithLifecycle()
    val highlightLiked by viewModel.highlightLiked.collectAsStateWithLifecycle()
    val likeBusy by viewModel.likeBusy.collectAsStateWithLifecycle()

    LaunchedEffect(recentCuration, isGenerating) {
        if (recentCuration == null && !isGenerating) {
            viewModel.loadMonthlyCuration()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 🔔 서버 성공이지만 result=null 인 경우를 '정상 무(無)데이터'로 간주
    //     → 빈 상태 전용 이미지 노출: img_curation_null
    //     (errorMessage == null, isGenerating == false, recentCuration == null)
    // ─────────────────────────────────────────────────────────────
    val isEmptySuccess = recentCuration == null && !isGenerating && errorMessage == null

    when {
        // 1) 데이터가 있으면 이미지 카드
        recentCuration != null -> {
            Log.d("CurationUI", "큐레이션 표시 - URL: ${recentCuration!!.thumbnailUrl}")
            HighlightCardOnlyImage(
                imageUrl = recentCuration!!.thumbnailUrl,
                liked = highlightLiked,
                likeBusy = likeBusy,
                onToggleLike = { viewModel.toggleHighlightLike() },
                onCardClick = onOpenDetail,
                modifier = modifier
            )
        }

        // 2) 로딩 중
        isGenerating -> {
            Log.d("CurationUI", "큐레이션 생성 중")
            Text(text = "큐레이션 생성 중...", modifier = Modifier.padding(horizontal = 20.dp))
        }

        // 3) 성공이지만 result=null → 빈 상태 이미지로 대체
        isEmptySuccess -> {
            Log.d("CurationUI", "큐레이션 없음(result=null) → null 이미지 표시")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null, // ✨ 여기 핵심 변경!
                modifier = modifier
            )
        }

        // 4) 에러 (원래 로직 유지 — 필요 시 같은 null 이미지를 써도 됨)
        errorMessage != null -> {
            Log.w("CurationUI", "오류 발생: $errorMessage")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null, // 이전엔 trump 이미지를 썼다면 여기서도 통일 가능
                modifier = modifier
            )
        }

        // 5) 기타 대비(이상 상태) — 안전망
        else -> {
            Log.d("CurationUI", "큐레이션 없음(기타 경로)")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null, // ✨ 안전망도 null 이미지로
                modifier = modifier
            )
        }
    }
}
//@Composable
//fun CurationHighlightSection(
//    modifier: Modifier = Modifier,
//    viewModel: CurationViewModel = hiltViewModel(),
//    onOpenDetail: (() -> Unit)? = null
//) {
//    val isGenerating by viewModel.isGenerating.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//    val recentCuration by viewModel.recentCuration.collectAsState()
//
//    //  추가: 하트 상태/로딩 상태
//    val highlightLiked by viewModel.highlightLiked.collectAsState()
//    val likeBusy by viewModel.likeBusy.collectAsState()
//
//
//    LaunchedEffect(viewModel) {
//        if (recentCuration == null && !isGenerating) {
//            viewModel.loadMonthlyCuration()
//        }
//    }
//
//    when {
//        // 1) 데이터가 있으면 무조건 이미지 먼저!
//        recentCuration != null -> {
//            Log.d("CurationUI", "큐레이션 표시 - URL: ${recentCuration!!.thumbnailUrl}")
//            HighlightCardOnlyImage(
//                imageUrl = recentCuration!!.thumbnailUrl,
//                liked = highlightLiked,                  //  VM 상태 전달
//                likeBusy = likeBusy,                     // 중복탭 방지
//                onToggleLike = { viewModel.toggleHighlightLike() },
//                onCardClick = onOpenDetail,            //  여기서 그대로 전달
//                modifier = modifier
//            )
//        }
//
//        // 2) 그 다음 로딩
//        isGenerating -> {
//            Log.d("CurationUI", "큐레이션 생성 중")
//            // 필요하면 로딩용 임시 카드도 가능:
//            // HighlightCardWithFallback(imageRes = R.drawable.img_trump_card_main, modifier = modifier)
//            Text(text = "큐레이션 생성 중...", modifier = Modifier.padding(horizontal = 20.dp))
//        }
//
//        // 3) 에러
//        errorMessage != null -> {
//            Log.w("CurationUI", "오류 발생: $errorMessage")
//            HighlightCardWithFallback(
//                imageRes = R.drawable.img_trump_card_main,
//                modifier = modifier
//            )
//        }
//
//        // 4) 아무 것도 없을 때
//        else -> {
//            Log.d("CurationUI", "큐레이션 없음")
//            HighlightCardWithFallback(
//                imageRes = R.drawable.img_trump_card_main,
//                modifier = modifier
//            )
//        }
//    }
//}

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
    val ctx = LocalContext.current
    val url = imageUrl.orEmpty()

    val cacheKey = remember(url) { "curation-" + url.substringAfterLast('/') }

    val localLoader = remember {
        ImageLoader.Builder(ctx)
            .build()
    }

    // 프리페치
    LaunchedEffect(url, cacheKey) {
        if (url.isNotBlank()) {
            localLoader.enqueue(
                ImageRequest.Builder(ctx)
                    .data(url)
                    .memoryCacheKey(cacheKey)
                    .diskCacheKey(cacheKey)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()
            )
        }
    }

    // 표시도 같은 키
    val request = remember(url, cacheKey) {
        ImageRequest.Builder(ctx)
            .data(url)
            .memoryCacheKey(cacheKey)
            .diskCacheKey(cacheKey)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }
    val painter = rememberAsyncImagePainter(model = request, imageLoader = localLoader)

    // ✅ 경고/타입 문제 없는 로깅 (State vs StateFlow 얽힘 방지)
    val pState by painter.state.collectAsState(initial = AsyncImagePainter.State.Empty)
    when (pState) {
        is AsyncImagePainter.State.Success ->
            Log.d("CurationUI", "이미지 성공: ${(pState as AsyncImagePainter.State.Success).result.request.data}")
        is AsyncImagePainter.State.Error ->
            Log.e("CurationUI", "이미지 실패", (pState as AsyncImagePainter.State.Error).result.throwable)
        is AsyncImagePainter.State.Loading ->
            Log.d("CurationUI", "이미지 로딩: $url")
        else -> Unit
    }
    //var liked by remember { mutableStateOf(false) }
//    val ctx = androidx.compose.ui.platform.LocalContext.current
//    var aspect by remember { mutableStateOf<Float?>(null) }
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
            painter = painter, // ← 여기!
            contentDescription = "추천 큐레이션 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = !likeBusy) { onCardClick?.invoke() }
        )
//        Image(
//            painter = imagePainter,
//            contentDescription = "추천 큐레이션 이미지",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable(                 // 배경만 이동
//                    enabled = !likeBusy,
//                    onClick = { onCardClick?.invoke() }
//                )
//        )

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
// 8월이면 7월호를 반환하는 util 함수
fun getPreviousKoreanCurationDate(): String {
    val today = LocalDate.now()
    val previousMonth = today.minusMonths(1)
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월호", Locale.KOREAN)
    return previousMonth.format(formatter)
}
@Composable
fun HighlightCardWithFallback(
    imageRes: Int,
    title: String = "링큐 큐레이션",                 // 시그니처 유지 (미사용)
    date: String = getPreviousKoreanCurationDate(), // 시그니처 유지 (미사용)
    modifier: Modifier = Modifier
) {
    // 원본 비율 계산 (없으면 16:9 기본)
    val painter = painterResource(id = imageRes)
    val intrinsic = painter.intrinsicSize
    val aspect = remember(intrinsic) {
        val w = intrinsic.width
        val h = intrinsic.height
        if (w > 0f && h > 0f) w / h else 16f / 9f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)            // 기존 여백 유지
            .clip(RoundedCornerShape(12.dp)) // 기존 라운드 유지
            .aspectRatio(aspect)             // ✅ 원본 비율 고정(세로 자동)
    ) {
        // ✅ 노크롭: 이미지가 잘리지 않도록 Fit 사용
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit, // 잘림 없음(레터박스 가능)
            modifier = Modifier.fillMaxSize()
        )
        // ⛔️ 텍스트/하트 전부 제거 (완전히 비어있는 이미지 카드)
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHighlightCardWithFallback() {
    HighlightCardWithFallback(imageRes = R.drawable.img_curation_null)
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