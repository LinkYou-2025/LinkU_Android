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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import java.util.Calendar

// 카드 컨텐츠 데이터 클래스(api 연동XX) -> 현우 오빠 화이팅 🎉
data class CurationCardContent(
    val title: String,
    val description: String
)

// 큐레이션은 항상 "지난달" 기준으로 보여준다 (1번 카드 멘트 + 이미지가 공유하는 기준월)
private fun getPreviousMonth(): Int {
    val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
    return cal.get(Calendar.MONTH) + 1
}

// 카드별 고정 컨텐츠 (1번은 동적으로 연도/월 계산) -> 이건 멘트 고정이라고 합니다!(돈 워리)
internal fun getCurationCardContents(): List<CurationCardContent> {
    val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
    val year = cal.get(Calendar.YEAR).toString()
    val month = cal.get(Calendar.MONTH) + 1

    return listOf(
        CurationCardContent(
            title = "${year}\n월간 큐레이션 ${month}월호",
            description = "이번 달을 위한 링크, 링큐가 준비했어요"
        ),
        CurationCardContent(
            title = "나와 비슷한 사람들은\n이런 링크를 봤어요",
            description = "이번 달, 같은 일상을 사는 모두의 관심 키워드"
        ),
        CurationCardContent(
            title = "잊고 있던 '나중에 보기',\n오늘이 그날이에요!",
            description = "저장만 해두기엔 아까운 링크들이 기다려요"
        )
    )
}

/**
 * 큐레이션 메인 카드 아이템 (이미지 + 타이틀 + 설명 + 페이지 인디케이터 + 체크아웃 버튼)
 *
 * @param modifier 외부에서 전달하는 Modifier (기본값: Modifier)
 * @param imageUrl 카드 배경 이미지 URL. blank / "null" 문자열이면 fallbackImage로 대체됨.
 *                 단, page가 0(월간, 지난달 기준)/1(키워드)/2(리마인드)인 경우 이 값과 무관하게 고정 이미지가 표시됨 (추후 API 확장을 위해 파라미터는 유지)
 * @param page 현재 카드의 0-based 페이지 인덱스. getCurationCardContents()에서 해당 인덱스의 콘텐츠를 가져오는 데도 사용됨
 * @param totalPage 전체 카드 페이지 수 (페이지 인디케이터 표시용, 기본값: 3)
 * @param onCheckOutClick 카드 우측 하단 체크아웃 버튼 클릭 콜백
 * @param fallbackImage imageUrl이 유효하지 않을 때 표시할 기본 이미지 리소스
 */
@Composable
internal fun CurationCardItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    page: Int = 0,
    totalPage: Int = 3,
    onCheckOutClick: () -> Unit = {},
    @DrawableRes fallbackImage: Int = R.drawable.img_curation_example // 일단 프리뷰 테스트를 위해 기본 이미지 넣어두었습니다~!
) {
    val colorTheme = MaterialTheme.linkuColors
    val resolvedImageUrl = imageUrl.takeIf { it.isNotBlank() && it != "null" }
    val contents = getCurationCardContents()
    val content = contents.getOrNull(page) ?: contents[0]

    // 1~3번째 카드는 API 이미지 대신 고정 이미지 사용 (imageUrl 파라미터는 추후 API 확장을 위해 유지)
    // 1번(월간)은 큐레이션이 항상 보여주는 "지난달" 기준 이미지
    val previousMonth = remember { getPreviousMonth() }
    val fixedImagePainter = when (page) {
        0 -> resolveMonthlyCurationImage(previousMonth)
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
                modifier = Modifier.matchParentSize()
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colorTheme.curationCardOverlayGradient)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart) // 하단 고정 앵커: 폰마다 카드 높이/글자 비율이 달라져도 잘리지 않도록 상단 offset 대신 하단 여백으로 배치. 실제 하단 간격은 인디케이터/버튼이 각자 지정
        ) {
            Text(
                text = content.title,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = colorTheme.white,
                modifier = Modifier.padding(start = 35.scaler)
            )

            Spacer(modifier = Modifier.height(18.scaler))

            Text(
                text = content.description,
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
                    Text(text = (page + 1).toString().padStart(2, '0'), style = pageIndicatorStyle)
                    Text(text = "|", style = pageIndicatorStyle)
                    Text(text = totalPage.toString().padStart(2, '0'), style = pageIndicatorStyle)
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
        )
    }
}
