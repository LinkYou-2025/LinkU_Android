package com.example.design.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
* 피그마 레이아웃 기준으로 반응형 ui를 작업할 수 있는 공통 유틸 파일입니다.
* 피그마에서 가로 412 , 세로 917 기준으로 dp를 사용해서 작업했습니다.
* 로그인, 회원가입 임시로 적용중
* TODO : 유지만 팀장 확인 받기.
* */

@Composable
fun rememberFigmaDimens(): Pair<(Float) -> Dp, (Float) -> Dp> {
    val config = LocalConfiguration.current //현재 화면 구성을 가져옴.(너비, 높이)
    val screenWidth = config.screenWidthDp.dp //피그마에서 설정한 412에 대한 비율을 계싼해서 현재 화면 너미에 맞춰 조정함.
    val screenHeight = config.screenHeightDp.dp //피그마에서 설정한 917에 대한 비율을 계산해서 현재 화면 높이에 맞춰 조장힘.

    // 1. 가로 모드인지 확인
    val isLandscape = config.screenWidthDp > config.screenHeightDp

    // 2. 가로 너비의 기준을 잡음
    // (태블릿 가로모드에서 디자인이 너무 퍼지지 않도록 최대 너비를 제한하거나 세로 비율에 맞춤)
    val baseWidth = if (isLandscape) {
        // 가로모드일 때는 세로 높이의 일정 비율 혹은 고정된 최대 너비(예: 600dp)를 기준으로 잡음
        minOf(screenWidth, 600.dp)
    } else {
        screenWidth
    }


    // 3. 계산 로직 (가로모드일 때는 중앙 정렬을 위해 w 사용 시 보정 필요할 수 있음)
    val w: (Float) -> Dp = { px -> baseWidth * (px / 412f) }
    val h: (Float) -> Dp = { px -> screenHeight * (px / 917f) }

    return w to h
}

