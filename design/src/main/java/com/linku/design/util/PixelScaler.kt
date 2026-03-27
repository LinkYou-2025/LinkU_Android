package com.linku.design.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/*
* 프로토타입 화면의 비율과 실제 레이아웃의 비율이 다를 때,
* 프로토타입 화면의 픽셀 값에 오차를 곱하여 dp와 sp를
* 기기의 비율에 맞게 스케일링 할 수 있는 클래스.
* 확장 함수는 이 클래스가 정의된 스코프 내에서만 사용할 수 있음.
* */
class PixelScaler(
    /*
    * baseWidth, baseHeight: 프로토타입 화면 크기 (ex: 피그마에 보이는 숫자)
    * maxWidth, maxHeight: 레이아웃이 갖는 크기
    * */
    val maxWidth: Dp,
    val maxHeight: Dp,
    val baseWidth: Dp,
    val baseHeight: Dp
){
    // 화면의 가로 길이 오차
    private val scaleW = maxWidth / baseWidth

    // 화면의 세로 길이 오차
    private val scaleH = maxHeight / baseHeight

    // 더 적은 오차로 스케일링
    val scale = minOf(scaleW, scaleH)

    // dp 단위 스케일링 확장 함수
    fun Dp.scaled() = this * scale

    // sp 단위 스케일링 확장 함수
    fun TextUnit.scaled() = (this.value * scale).sp
}
