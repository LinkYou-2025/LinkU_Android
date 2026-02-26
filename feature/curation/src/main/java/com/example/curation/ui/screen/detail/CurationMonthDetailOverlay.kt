package com.example.curation.ui.screen.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.lerp
import coil3.compose.SubcomposeAsyncImage
import com.example.curation.R
import com.example.curation.ui.util.CurationConstants
import com.example.design.util.scaler
import kotlinx.coroutines.delay



@Composable
fun CurationMonthDetailOverlay(
    page: Int,
    imageUrl: String?,
    onBack: () -> Unit
) {
    var animationState by remember { mutableStateOf<Boolean?>(null) }
    var shouldClose by remember { mutableStateOf(false) }

    val animationProgress by animateFloatAsState(
        targetValue = when (animationState) {
            true -> 1f
            false -> 0f
            null -> 0f
        },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "progress",
        finishedListener = { finalValue ->
            if (finalValue == 0f && animationState == false) {
                shouldClose = true
            }
        }
    )

    val contentProgress by animateFloatAsState(
        targetValue = when (animationState) {
            true -> 1f
            else -> 0f
        },
        animationSpec = tween(
            durationMillis = 250,
            delayMillis = if (animationState == true) 100 else 0,
            easing = FastOutSlowInEasing
        ),
        label = "contentProgress"
    )

    LaunchedEffect(Unit) {
        delay(20)
        animationState = true
    }

    LaunchedEffect(shouldClose) {
        if (shouldClose) {
            onBack()
        }
    }

    BackHandler {
        if (animationState == true) {
            animationState = false
        }
    }

    // 크기/위치 계산
    val startHeight = CurationConstants.CARD_HEIGHT_VALUE.scaler
    val endHeight = CurationConstants.DETAIL_CARD_HEIGHT_VALUE.scaler
    val listCardTopOffset = CurationConstants.CARD_TOP_OFFSET_VALUE.scaler
    val startPadding = CurationConstants.CARD_HORIZONTAL_PADDING_VALUE.scaler

    val horizontalPadding = lerp(startPadding, 0.scaler, animationProgress)
    val cardHeight = lerp(startHeight, endHeight, animationProgress)
    val topOffset = lerp(listCardTopOffset, 0.scaler, animationProgress)

    val resolvedImageUrl = imageUrl?.takeIf { it.isNotBlank() && it != "null" }

    Box(modifier = Modifier.fillMaxSize()) {

        // 배경 오버레이
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = animationProgress * 0.97f))
        )

        // 카드 이미지
        Box(
            modifier = Modifier
                .offset(y = topOffset)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .height(cardHeight)
                .clip(RoundedCornerShape(24.scaler))
                .background(Color(0xFFF2F2F2))
        ) {

        if (resolvedImageUrl == null) {
                Image(
                    painter = painterResource(id = R.drawable.img_curation_example),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SubcomposeAsyncImage(
                    model = resolvedImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 상세 콘텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = endHeight)
                .offset(y = 60.scaler * (1f - contentProgress))
                .graphicsLayer { alpha = contentProgress }
                .background(Color.White)
        ) {
            CurationMonthDetailContent(page = page)

        }
    }
}

@Composable
private fun CurationMonthDetailContent(page: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.scaler)
    ) {
        Spacer(modifier = Modifier.height(20.scaler))
        Text(
            text = "큐레이션 상세 화면 #$page",
            color = Color.Black
        )
    }
}