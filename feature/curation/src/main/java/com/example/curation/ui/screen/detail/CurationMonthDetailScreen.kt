package com.example.curation.ui.screen.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.curation.ui.main_card.CurationMainCard
import com.example.design.util.scaler
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurationMonthDetailScreen(
    curationId: Long,
    onBack: () -> Unit,
    sharedCardModifier: Modifier
) {
    val systemUiController = rememberSystemUiController()

    // StatusBar 설정
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }

    // 뒤로가기 처리
    BackHandler {
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 상단 카드 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.scaler)
                .clipToBounds()
        ) {
            CurationMainCard(
                imageUrl = null,
                modifier = sharedCardModifier
                    .fillMaxSize()
            )
        }

        //  이후 상세 콘텐츠 영역
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 350.scaler)
        ) {
            // TODO: 월별 큐레이션 상세 콘텐츠 구현하기.
        }
    }
}