package com.linku.curation.ui.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.linku.curation.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 월별 큐레이션 계열 화면(모아보기 #41-1, 상세 #42-1 등)에서 공통으로 쓰는 상단 헤더.
 *
 * 백버튼은 모든 화면에서 피그마 기준 고정 위치(top 59 / start 20)라 파라미터로 받지 않고,
 * 트윙클 이미지 + [title]/[description] 영역만 화면마다 상단 여백(y)이 달라서
 * [contentTopOffset]으로 조절한다. 나머지(좌우 20 여백, 가로 중앙 정렬)는 화면 공통이라 고정.
 *
 * [title], [description]은 화면에 따라 없을 수 있는데, null 대신 빈 문자열("")을 기본값으로 둔다.
 * 트윙클 이미지도 화면에 따라 안 보일 수 있어 [showTwinkle]로 표시 여부를 받는다.
 * [title]과 [description] 사이 간격도 화면마다 달라서(모아보기 12 / 상세 18.49 등) [titleDescriptionGap]으로 받는다.
 *
 * @param onBackClick 백버튼 클릭 콜백
 * @param contentTopOffset 트윙클+텍스트 영역의 상단 여백 (피그마 px에 [scaler]를 적용해서 전달)
 * @param showTwinkle 트윙클 이미지 노출 여부
 * @param title 큐레이션 제목. ex) "월간 큐레이션 5월호". 없으면 빈 문자열
 * @param description 큐레이션 설명. ex) "세나님의 이번 달을 링큐가 분석했어요!". 없으면 빈 문자열
 * @param titleDescriptionGap title과 description 사이 간격 (피그마 px에 [scaler]를 적용해서 전달)
 */
@Composable
internal fun CurationTopHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    contentTopOffset: Dp, // 로고 + 제목 + 설명이 세트가 y좌표가 유동적이라 추가했습니다(다현아 살려줘)
    showTwinkle: Boolean = true, // 기본적으로 true인데, 일부 false 있음. 그래서 기본값은 true
    title: String, // 빈 값 불가함.
    titleDescriptionGap: Dp = 18.49f.scaler, // 제목이랑 묘사 사이는 대부분 18.49f인데, 일부 12가 있어서.. 추가함..
    description: String, // 빈 값 불가함.
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.ic_curation_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 59.scaler, start = 20.scaler)
                .noRippleClickable(onClick = onBackClick)
                .width(10.scaler)
                .height(16.25f.scaler)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = contentTopOffset, start = 20.scaler, end = 20.scaler)
        ) {
            if (showTwinkle) {
                Image(
                    painter = painterResource(id = R.drawable.ic_curation_twinkle),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(18.49f.scaler))
            }

            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight(600),
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.merge
                        (TextStyle(brush = colorTheme.curationGradient)),
                )
            }

            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(titleDescriptionGap))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                    color = colorTheme.gray[600],
                )

            }
        }
    }
}


@Preview(name = "월별 큐레이션 상세 (#42-1)", showBackground = true)
@Composable
private fun CurationTopHeaderDetailPreview() {
    LinkuPreview {
        CurationTopHeader(
            onBackClick = {},
            contentTopOffset = 68.scaler,
            title = "2026\n 월간 큐레이션 5월호",
            description = "세나님의 이번 달을 링큐가 분석했어요!",
            titleDescriptionGap = 18.49f.scaler
        )
    }
}
