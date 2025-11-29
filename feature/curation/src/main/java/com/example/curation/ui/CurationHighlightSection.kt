package com.example.curation.ui


/*
* 역할: "세나님을 위한 8월의 큐레이션" 단일 추천 섹션
API로 받아온 하나의 큐레이션 이미지 데이터를 표시

*/

import coil3.compose.rememberAsyncImagePainter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalInspectionMode

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
import androidx.compose.ui.text.TextStyle


@Composable
fun CurationHighlightSection(
    modifier: Modifier = Modifier,
    viewModel: CurationViewModel, // hiltViewModel 기본값 제거
    onOpenDetail: (() -> Unit)? = null
) {
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

    val isEmptySuccess = recentCuration == null && !isGenerating && errorMessage == null

    when {
        // 1) 데이터가 있으면 이미지 카드
        recentCuration != null -> {
            Log.d("CurationUI", "큐레이션 표시 - URL: ${recentCuration!!.thumbnailUrl}")
            HighlightCurationCard(
                imageUrl = recentCuration!!.thumbnailUrl,
                title = "링큐 큐레이션",
                date = getPreviousKoreanCurationDate(),
                liked = highlightLiked ?: false,
                likeBusy = likeBusy,
                onClickCard = { onOpenDetail?.invoke() },
                onToggleLike = { viewModel.toggleHighlightLike() },
                modifier = modifier
            )
        }

        // 2) 로딩 중
        isGenerating -> {
            Log.d("CurationUI", "큐레이션 생성 중")
            Text(text = "큐레이션 생성 중...", modifier = Modifier.padding(horizontal = 20.dp))
        }

        // 3) 성공이지만 result=null → 빈 상태 이미지
        isEmptySuccess -> {
            Log.d("CurationUI", "큐레이션 없음(result=null) → null 이미지 표시")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }

        // 4) 에러
        errorMessage != null -> {
            Log.w("CurationUI", "오류 발생: $errorMessage")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }

        // 5) 기타 대비
        else -> {
            Log.d("CurationUI", "큐레이션 없음(기타 경로)")
            HighlightCardWithFallback(
                imageRes = R.drawable.img_curation_null,
                modifier = modifier
            )
        }
    }
}
@Composable
fun HighlightCurationCard(
    imageUrl: String?,
    title: String,
    date: String,
    liked: Boolean,
    likeBusy: Boolean,
    onClickCard: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClickCard() }
    ) {

        // --------------------
        // 1) 백그라운드 이미지
        // --------------------
        Image(
            painter =
                if (isPreview || imageUrl == null)
                    painterResource(R.drawable.img_trump_card_main)
                else
                    rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --------------------
        // 2) 오른쪽 상단 하트
        // --------------------
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                .padding(1.dp)
                .size(16.dp, 15.dp)
                .clickable(enabled = !likeBusy) { onToggleLike() }
        ) {
            Image(
                painter = painterResource(
                    if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // --------------------
        // 3) 왼쪽 하단 텍스트 세트 (프리뷰 전용)
        // --------------------
        if (isPreview) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 26.dp, top = 144.dp)
            ) {

                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 30.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = date,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        // --------------------
        // 4) 오른쪽 하단 "보러가기 >" (실제 + 프리뷰 모두)
        // --------------------
        if (isPreview) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 22.dp, bottom = 27.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "보러가기",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )

                Spacer(Modifier.width(4.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_curation_vector),
                    contentDescription = null,
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .width(6.dp)
                        .height(9.75.dp)
                )
            }
        }

    }
}


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
fun PreviewCurationHighlightSection_Full() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // 상단 제목
        Text(
            text = "세나님을 위한 8월 큐레이션",
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HighlightCurationCard(
            imageUrl = null, // preview니까 null → trump 카드 사용됨
            title = "링큐 큐레이션",
            date = "2025년 8월호",
            liked = true,
            likeBusy = false,
            onClickCard = {},
            onToggleLike = {},
            modifier = Modifier
                .width(372.dp)
                .height(210.dp)
        )
    }
}