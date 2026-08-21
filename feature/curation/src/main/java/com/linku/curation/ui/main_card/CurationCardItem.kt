package com.linku.curation.ui.main_card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.linku.curation.R
import com.linku.curation.ui.item.CurationCheckOutButton
import com.linku.curation.ui.mapper.resolveMonthlyCurationImage
import com.linku.design.component.SkeletonBox
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 큐레이션 메인 카드 아이템 (이미지 + 타이틀 + 설명 + 페이지 인디케이터 + 체크아웃 버튼)
 *
 * @param modifier 외부에서 전달하는 Modifier (기본값: Modifier)
 * @param imageUrl 카드 배경 이미지 URL. blank / "null" 문자열이면 fallbackImage로 대체됨.
 *                 단, page가 0(월간)/1(키워드)/2(리마인드)인 경우 이 값과 무관하게 고정 이미지가 표시됨 (추후 API 확장을 위해 파라미터는 유지)
 * @param title 카드에 표시할 제목 (서버 응답의 SectionItem.title 기준)
 * @param description 카드에 표시할 설명 (서버 응답의 SectionItem.description 기준)
 * @param month 0번(월간) 카드의 고정 이미지를 고르는 데 쓰이는 월(1~12). 서버가 내려주는 최신 큐레이션 월 기준.
 * @param page 현재 카드의 0-based 페이지 인덱스
 * @param totalPage 전체 카드 페이지 수 (페이지 인디케이터 표시용, 기본값: 3)
 * @param isLoading true면 카드 내용 대신 스켈레톤 + 쉬머를 표시
 * @param onCheckOutClick 카드 우측 하단 체크아웃 버튼 클릭 콜백
 * @param fallbackImage imageUrl이 유효하지 않을 때 표시할 기본 이미지 리소스
 */
@Composable
internal fun CurationCardItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    description: String,
    month: Int = 0,
    page: Int = 0,
    totalPage: Int = 3,
    isLoading: Boolean = false,
    onCheckOutClick: () -> Unit = {},
    @DrawableRes fallbackImage: Int = R.drawable.img_curation_example // 일단 프리뷰 테스트를 위해 기본 이미지 넣어두었습니다~!
) {
    val colorTheme = MaterialTheme.linkuColors

    // 0번(월간) 카드는 month가 큐레이션 메인 API 응답으로 내려오는데, 응답 도착 전에는
    // month가 0이라 resolveMonthlyCurationImage(0)가 투명 이미지를 반환해 카드가 비어 보인다.
    // API 응답 대기 중에는 카드 전체를 SkeletonBox(gray300)로 대체해
    // 쉬머가 흐르며 로딩 중임을 보여준다.
    if (isLoading) {
        Box(
            modifier = modifier.clip(RoundedCornerShape(24.scaler))
        ) {
            SkeletonBox(
                modifier = Modifier.matchParentSize(),
                shape = RoundedCornerShape(24.scaler),
                color = colorTheme.gray[300]
            )
        }
        return
    }
    val resolvedImageUrl = imageUrl.takeIf { it.isNotBlank() && it != "null" }

    // 1~3번째 카드는 API 이미지 대신 고정 이미지 사용 (imageUrl 파라미터는 추후 API 확장을 위해 유지)
    // 0번(월간)은 서버가 내려주는 최신 큐레이션 월(month) 기준 고정 이미지
    val fixedImagePainter = when (page) {
        0 -> resolveMonthlyCurationImage(month)
        1 -> painterResource(R.drawable.img_curation_keyword)
        2 -> painterResource(R.drawable.img_curation_remind)
        else -> null
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                ambientColor = colorTheme.black.copy(alpha = 0.4f),
                spotColor = colorTheme.black.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(24.scaler))
            .background(colorTheme.curationCardBackground)
    ) {
        if (fixedImagePainter != null) {
            Image(
                painter = fixedImagePainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else if (resolvedImageUrl == null) {
            Image(
                painter = painterResource(id = fallbackImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            SubcomposeAsyncImage(
                model = resolvedImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                loading = {
                    SkeletonBox(
                        modifier = Modifier.matchParentSize(),
                        shape = RoundedCornerShape(24.scaler),
                        color = colorTheme.gray[300]
                    )
                }
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colorTheme.curationCardOverlayGradient)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = colorTheme.white,
                modifier = Modifier
                    .padding(start = 35.scaler)
                    .offset(y = 5.scaler)
            )

            Spacer(modifier = Modifier.height(14.scaler))

            Text(
                text = description,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(400),
                color = colorTheme.white,
                modifier = Modifier.padding(start = 35.scaler)
            )

            Spacer(modifier = Modifier.height(18.scaler))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.scaler)
            ) {
                val pageIndicatorStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight(500),
                    color = colorTheme.gray[300],
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 35.scaler),
                    horizontalArrangement = Arrangement.spacedBy(12.scaler),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (page + 1).toString().padStart(2, '0'),
                        style = pageIndicatorStyle
                    )
                    Text(
                        text = "|",
                        style = pageIndicatorStyle
                    )
                    Text(
                        text = totalPage.toString().padStart(2, '0'),
                        style = pageIndicatorStyle
                    )
                }

                CurationCheckOutButton(
                    onClick = onCheckOutClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 33.scaler)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationCardItem() {
    LinkuPreview {
        CurationCardItem(
            modifier = Modifier.size(width = 346.scaler, height = 432.scaler),
            imageUrl = "",
            title = "2026\n월간 큐레이션 8월호",
            description = "이번 달을 위한 링크, 링큐가 준비했어요",
            month = 8
        )
    }
}
