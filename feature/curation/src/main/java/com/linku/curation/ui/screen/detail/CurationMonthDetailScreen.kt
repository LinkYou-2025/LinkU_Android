// CurationMonthDetailScreen.kt
package com.linku.curation.ui.screen.detail

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
import com.linku.curation.R
import com.linku.design.util.scaler
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun CurationMonthDetailScreen(
    curationId: Long,
    imageUrl: String?,
    onBack: () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    // 애니메이션 상태: null = 초기, true = 확장, false = 축소(뒤로가기)
    var animationState by remember { mutableStateOf<Boolean?>(null) }

    // 뒤로가기 완료 플래그
    var shouldNavigateBack by remember { mutableStateOf(false) }

    // 애니메이션 진행률 (0 = 리스트 상태, 1 = 디테일 상태)
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
                shouldNavigateBack = true
            }
        }
    )

    // 콘텐츠 슬라이드 애니메이션
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

    // 진입 시 확장 애니메이션 시작
    LaunchedEffect(Unit) {
        delay(20)
        animationState = true
    }

    // 실제 뒤로가기 처리
    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) {
            onBack()
        }
    }

    // StatusBar 설정
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }

    // 뒤로가기 처리 (애니메이션 역재생)
    BackHandler {
        if (animationState == true) {
            animationState = false
        }
    }

    // 크기/위치 계산
    val startHeight = 432.scaler
    val endHeight = 350.scaler

    val horizontalPadding = lerp(33.scaler, 0.scaler, animationProgress)
    val cardHeight = lerp(startHeight, endHeight, animationProgress)

    val listCardTopOffset = 180.scaler
    val topOffset = lerp(listCardTopOffset, 0.scaler, animationProgress)

    // 배경 알파 - 뒤로가기 시 서서히 투명해짐
    val backgroundAlpha = animationProgress

    val resolvedImageUrl = imageUrl
        ?.takeIf { it.isNotBlank() && it != "null" }

    Box(modifier = Modifier.fillMaxSize()) {

        // 배경 오버레이 (서서히 나타났다 사라짐)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = animationProgress))
        )

        // 카드 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .offset(y = topOffset)
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

        // 상세 콘텐츠 (아래에서 위로 슬라이드 + 페이드)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = endHeight)
                .offset(y = 60.scaler * (1f - contentProgress))
                .graphicsLayer { alpha = contentProgress }
                .background(Color.White)
        ) {
            CurationMonthDetailContent(curationId = curationId)
        }
    }
}


@Composable
private fun CurationMonthDetailContent(
    curationId: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.scaler)
    ) {
        Spacer(modifier = Modifier.height(20.scaler))

        Text(
            text = "큐레이션 상세 화면 #$curationId",
            color = Color.Black
        )
    }
}