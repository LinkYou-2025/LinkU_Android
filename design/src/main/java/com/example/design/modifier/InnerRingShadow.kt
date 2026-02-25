package com.example.design.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 컴포넌트의 안쪽 테두리를 따라 부드러운 원형 그림자(Inner Ring Shadow) 효과를 적용합니다.
 *
 * 이 함수는 [Brush.radialGradient]를 사용하여 컴포넌트의 중심부터 가장자리까지 그레이디언트를 생성하며,
 * 지정된 두께([edgeThickness]) 영역에만 [shadowColor]를 노출시켜 입체감을 부여합니다.
 *
 * @param shadowColor 가장자리 끝부분에 적용될 그림자의 색상 및 투명도입니다. 기본값은 10% 불투명도의 검정색입니다.
 * @param edgeThickness 그림자가 그려질 가장자리의 두께입니다. 기본값은 2.dp입니다.
 * @return 안쪽 그림자 효과가 적용된 [Modifier] 객체를 반환합니다.
 *
 * @see androidx.compose.ui.draw.drawWithCache
 * @see androidx.compose.ui.graphics.Brush.Companion.radialGradient
 *
 * @sample
 * Box(
 *     modifier = Modifier
 *         .size(100.dp)
 *         .clip(CircleShape)
 *         .innerRingShadow(shadowColor = Color.Black.copy(alpha = 0.2f), edgeThickness = 4.dp)
 * )
 */
fun Modifier.innerRingShadow(
    shadowColor: Color = Color.Black.copy(alpha = 0.10f),
    edgeThickness: Dp = 2.dp
): Modifier = this.drawWithCache {

    // ─────────────────────────────────────────────────────────────
    // 1) 원형 반지름 계산
    // ─────────────────────────────────────────────────────────────
    // size: 이 Modifier가 적용되는 컴포넌트의 실제 픽셀 크기 (Size(widthPx, heightPx))
    // minDimension: width/height 중 더 짧은 변 (원형 그림자 만들 때 "지름"으로 삼기 좋음)
    //
    // r: radialGradient의 반지름(px)
    // - minDimension / 2: "짧은 변" 기준으로 원의 반지름을 잡음
    // - coerceAtLeast(1f): 너무 작은 값(0에 가까운 값)으로 인해 0으로 나눔/이상 계산을 피함
    val r = (size.minDimension / 2f).coerceAtLeast(1f)

    // ─────────────────────────────────────────────────────────────
    // 2) 테두리 쪽에만 그림자를 남기기 위한 "내부 링 두께"를 px로 변환
    // ─────────────────────────────────────────────────────────────
    // edgeThickness(Dp): 사용자 입력 두께(논리 단위) → toPx()로 "픽셀" 변환
    //
    // t: 테두리 내부 그림자가 차지할 두께(px)
    // - coerceIn(0f, r): 두께가 음수가 되거나 반지름보다 커지면 계산이 깨질 수 있어서 범위 제한
    //   * 0f: 그림자 없음
    //   * r: 반지름 전체(즉, 거의 전체가 그림자)까지 허용
    val t = edgeThickness.toPx().coerceIn(0f, r)

    // ─────────────────────────────────────────────────────────────
    // 3) Gradient에서 "투명 → 그림자"로 바뀌는 경계 비율 계산
    // ─────────────────────────────────────────────────────────────
    // radialGradient의 colorStops는 (0.0 ~ 1.0) 범위의 비율로 정의됨.
    //
    // (r - t) / r 의 의미:
    // - r: 전체 반지름
    // - r - t: "그림자가 시작되기 전"까지의 반지름(= 내부는 투명하게 두고 싶은 영역)
    //
    // 예) r=50px, t=5px 라면
    // - (r - t) / r = 45/50 = 0.9
    // - 즉, 중심~0.9 구간은 투명, 0.9~1.0 구간에서만 그림자로 변화
    //
    // coerceIn(0f, 1f): 비율이 범위를 벗어나면 gradient가 비정상 동작할 수 있어 제한
    val stop = ((r - t) / r).coerceIn(0f, 1f)

    // ─────────────────────────────────────────────────────────────
    // 4) 중심은 투명, 가장자리로 갈수록 shadowColor가 되는 Radial Gradient 생성
    // ─────────────────────────────────────────────────────────────
    // Brush.radialGradient:
    // - center: 그라디언트 중심점 (여기서는 컴포넌트 중앙)
    // - radius: 반지름(px)
    //
    // colorStops:
    // - 0.0f : 중심점(반지름 0) 위치
    // - stop : "여기까지는 투명" 위치 (내부 영역)
    // - 1.0f : 반지름 끝(가장자리) 위치
    //
    // 0.0f to Transparent + stop to Transparent:
    // - 내부 영역(0~stop)을 완전히 투명하게 고정해 "테두리만" 그림자가 남게 함
    //
    // 1.0f to shadowColor:
    // - 가장자리에서만 shadowColor가 적용되도록 하여,
    //   두 번째 사진처럼 "희미한 내부 테두리 그림자" 느낌을 만들 수 있음
    val brush = Brush.radialGradient(
        colorStops = arrayOf(
            0.0f to Color.Transparent,   // 중심은 완전 투명
            stop to Color.Transparent,   // stop 지점까지도 완전 투명(= 내부 깨끗)
            1.0f to shadowColor          // 가장자리에만 그림자 색이 나타남
        ),
        center = size.center,            // 컴포넌트 중앙 기준으로 퍼짐
        radius = r                       // 위에서 계산한 반지름(px)
    )

    // ─────────────────────────────────────────────────────────────
    // 5) 실제 그리기: 기존 컨텐츠를 그리고, 그 위에 그라디언트를 덮어씌움
    // ─────────────────────────────────────────────────────────────
    onDrawWithContent {

        // (1) 원래 컴포넌트 내용(배경, 텍스트 등) 먼저 그림
        drawContent()

        // (2) 위에서 만든 brush를 사각형 전체에 칠함
        //     - 원형으로만 보이게 하려면 반드시 바깥에서 clip(CircleShape) 같은 클립이 필요함!
        //     - clip이 없다면 "사각형 영역" 전체에 radialGradient가 적용됨.
        //
        // clip(CircleShape)가 이미 적용된 상태라면:
        // - drawRect로 칠해도 원 밖은 잘려서 결국 "원 테두리 내부"에만 그림자가 남음
        drawRect(brush = brush)
    }
}