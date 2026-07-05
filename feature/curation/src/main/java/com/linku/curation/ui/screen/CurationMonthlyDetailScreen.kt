package com.linku.curation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.core.model.RecommendedLink
import com.linku.curation.ui.emotion.CurationEmotionSection
import com.linku.curation.ui.emotion.EmotionItem
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.recommend_list.CurationRecommendedLinksPager
import com.linku.curation.ui.util.CurationFixedGradientBackground
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 월간 큐레이션 1번 카드 상세(#42-1) 화면.
 *
 * [emotionItems]가 비어있으면 "이번 달은 선택하신 상황/감정이 없네요!" 예외 상태로 전환되어,
 * 감정 요약 아래 나머지 콘텐츠(본문 문구, 추천 링크, 하단 문구)는 숨기고
 * 대신 홈으로 이동하는 [BottomGradientButton]을 보여준다.
 *
 * @param nickname 상단 설명/본문 문구에 쓰일 사용자 닉네임
 * @param year 상단에 표시할 연도. ex) "2026"
 * @param monthTitle 상단에 표시할 회차 제목. ex) "월간 큐레이션 5월호"
 * @param emotionItems "N월 상황/감정 요약" 섹션에 표시할 감정 키워드 항목 (최대 3개). 비어있으면 예외 상태로 전환
 * @param recommendedLinks "추천 링크" 섹션에 표시할 링크 목록
 * @param onBack 백버튼 클릭 콜백
 * @param onLinkClick 추천 링크 카드 클릭 시 호출. 클릭된 링크의 url 전달
 * @param onLinkDeleteClick 추천 링크 카드의 "더보기" 메뉴에서 삭제 선택 시 호출
 * @param onGoHome 예외 상태의 "링크 저장하러 가기" 버튼 클릭 시 호출. 홈 화면으로 이동
 */
@Composable
internal fun CurationMonthlyDetailScreen(
    onBack: () -> Unit,
    nickname: String = "",
    year: String = "2026",
    monthTitle: String = "월간 큐레이션",
    emotionItems: List<EmotionItem> = emptyList(),
    recommendedLinks: List<RecommendedLink> = emptyList(),
    onLinkClick: (String) -> Unit = {},
    onLinkDeleteClick: (RecommendedLink) -> Unit = {},
    onGoHome: () -> Unit = {},
) {
    val colorTheme = MaterialTheme.linkuColors
    val isEmpty = emotionItems.isEmpty()

    BackHandler { onBack() }

    CurationFixedGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CurationTopHeader(
                onBackClick = onBack,
                contentTopOffset = 92.scaler,
                title = "$year\n$monthTitle",
                titleDescriptionGap = 12.scaler, // 피그마상 18.49f인데 아무리 봐도 피그마랑 다른데? 12로 했는데 디자이너와 조정해주세용
                description = "${nickname}님의 이번 달을 링큐가 분석했어요!"
            )

            Spacer(modifier = Modifier.height(if (isEmpty) 185.scaler else 78.scaler))

            Column(modifier = Modifier.padding(horizontal = 30.scaler)) {
                CurationEmotionSection(items = emotionItems)

                if (!isEmpty) {
                    Spacer(modifier = Modifier.height(40.scaler))

                    // api 연동해야 하는 문구 입니다. 일단 기본 피그마 내용으로 넣었습니다.
                    Text(
                        text = "생각은 많은데 정리가 안되죠.\n${nickname}님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = colorTheme.gray[800],
                    )

                    Spacer(modifier = Modifier.height(60.scaler))

                    CurationRecommendedLinksPager(
                        links = recommendedLinks,
                        onLinkClick = onLinkClick,
                        onDeleteClick = onLinkDeleteClick
                    )

                    Spacer(modifier = Modifier.height(60.scaler))

                    // api 연동해야 하는 문구 입니다. 일단 기본 피그마 내용으로 넣었습니다.
                    Text(
                        text = "지금 떠오르지 않아도 괜찮아요.\n영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요.",
                        style = TextStyle(
                            brush = colorTheme.emotionTitleGradient,
                            fontSize = 16.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight(700),
                        )
                    )

                    Spacer(modifier = Modifier.height(37.scaler))
                }
            }
        }

        if (isEmpty) {
            // 하단 영역 (EmailVerificationScreen과 동일한 방식으로 하단 고정)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomGradientButton(
                    text = "링크 저장하러 가기",
                    enabled = true,
                    activeGradient = colorTheme.maincolor,
                    inactiveGradient = colorTheme.inactiveColor,
                    onClick = onGoHome,
                    label = "이번 달은 선택하신 상황/감정이 없네요!"
                )
            }
        }
    }
}

@Preview(name = "월간 큐레이션 1번 카드 상세 (#42-1)", showBackground = true)
@Composable
private fun CurationMonthlyDetailScreenPreview() {
    val emotions = listOf(
        EmotionItem(progress = 1.0f, keyword = "#업무 준비 중"),
        EmotionItem(progress = 0.76f, keyword = "#커리어고민"),
        EmotionItem(progress = 0.09f, keyword = "#짜증")
    )
    val links = listOf(
        RecommendedLink(
            isInternal = true,
            userLinkuId = 1L,
            title = "오픽 AL 따는 꿀팁 얼른 보러오세요",
            url = "https://blog.naver.com/example",
            imageUrl = null,
            domain = "blog.naver.com",
            domainImageUrl = null,
            categories = listOf("생산성·툴", "평온")
        ),
        RecommendedLink(
            isInternal = true,
            userLinkuId = 2L,
            title = "오픽 공부할 때 문법도 공부해야할까?",
            url = "https://github.com/example",
            imageUrl = null,
            domain = "github.com",
            domainImageUrl = null,
            categories = listOf("여행", "행복")
        ),
        RecommendedLink(
            isInternal = false,
            userLinkuId = null,
            title = "글램핑 예약, 누구보다 싸게하기",
            url = "https://blog.naver.com/example2",
            imageUrl = null,
            domain = "blog.naver.com",
            domainImageUrl = null,
            categories = emptyList()
        )
    )

    LinkuPreview {
        CurationMonthlyDetailScreen(
            onBack = {},
            nickname = "세나",
            monthTitle = "월간 큐레이션 5월호",
            emotionItems = emotions,
            recommendedLinks = links
        )
    }
}

@Preview(name = "#42-1 예외_데이터 없는 경우", showBackground = true)
@Composable
private fun CurationMonthlyDetailScreenEmptyPreview() {
    LinkuPreview {
        CurationMonthlyDetailScreen(
            onBack = {},
            nickname = "세나",
            monthTitle = "월간 큐레이션 5월호",
            emotionItems = emptyList(),
            recommendedLinks = emptyList()
        )
    }
}
