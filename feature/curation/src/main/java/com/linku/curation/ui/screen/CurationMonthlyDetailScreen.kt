package com.linku.curation.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.curation.LinkType
import com.linku.core.model.curation.RecommendLink
import com.linku.curation.ui.emotion.CurationEmotionSection
import com.linku.curation.ui.emotion.EmotionItem
import com.linku.curation.ui.header.CurationTopHeader
import com.linku.curation.ui.recommend_list.CurationRecommendedLinksPager
import com.linku.curation.ui.util.CurationFixedGradientBackground
import com.linku.curation.viewModel.CurationDetailViewModel
import com.linku.curation.viewModel.intent.CurationDetailedIntent
import com.linku.curation.viewModel.sideeffect.CurationDetailedSideEffect
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
fun CurationMonthlyDetailScreen(
    onBack: () -> Unit,
    onGoHome: () -> Unit = {},
    onNavigateToLinkDetail: (Long) -> Unit = {},
    viewModel: CurationDetailViewModel,
) {
    val context = LocalContext.current

    // 뷰모델 상태 구독
    val state by viewModel.curationDetailedState.collectAsStateWithLifecycle()

    // 사이드이펙트 수집
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CurationDetailedSideEffect.NavigateToLinkDetail ->
                    onNavigateToLinkDetail(effect.linkId)

                is CurationDetailedSideEffect.OpenBrowser -> {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(effect.url)))
                    }.onFailure {
                        Toast.makeText(context, "링크를 열 수 없어요.", Toast.LENGTH_SHORT).show()
                    }
                }

                is CurationDetailedSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    CurationMonthlyDetailScreenContent(
        onBack = onBack,
        onGoHome = onGoHome,
        nickname = state.monthlyCurationDetail?.nickname.orEmpty(),
        title = state.monthlyCurationDetail?.detail?.title.orEmpty(),
        emotionItems = state.monthlyCurationDetail?.topTags?.map { tag ->
            EmotionItem(progress = tag.percent / 100f, keyword = tag.name)
        } ?: emptyList(),
        recommendedLinks = state.monthlyCurationDetail?.recommendLink ?: emptyList(),
        headerMent = state.monthlyCurationDetail?.detail?.headerMent.orEmpty(),
        footerMent = state.monthlyCurationDetail?.detail?.footerMent.orEmpty(),
        onLinkClick = { link -> viewModel.handleIntent(CurationDetailedIntent.ClickLink(link)) },
    )
}

/**
 * 월간 큐레이션 1번 카드 상세(#42-1) 화면.
 *
 * [emotionItems]가 비어있으면 "이번 달은 선택하신 상황/감정이 없네요!" 예외 상태로 전환되어,
 * 감정 요약 아래 나머지 콘텐츠(본문 문구, 추천 링크, 하단 문구)는 숨기고
 * 대신 홈으로 이동하는 [BottomGradientButton]을 보여준다.
 *
 * @param nickname 상단 설명/본문 문구에 쓰일 사용자 닉네임
 * @param title 상단에 표시할 제목. ex) "2026\n월간 큐레이션 5월호"
 * @param headerMent 감정 섹션 아래 표시할 상단 본문 문구 (API `headerMent`)
 * @param footerMent 추천 링크 아래 표시할 하단 본문 문구 (API `footerMent`)
 * @param emotionItems "N월 상황/감정 요약" 섹션에 표시할 감정 키워드 항목 (최대 3개). 비어있으면 예외 상태로 전환
 * @param recommendedLinks "추천 링크" 섹션에 표시할 링크 목록
 * @param onBack 백버튼 클릭 콜백
 * @param onLinkClick 추천 링크 카드 클릭 시 호출. 클릭된 링크 전달
 * @param onLinkDeleteClick 추천 링크 카드의 "더보기" 메뉴에서 삭제 선택 시 호출
 * @param onGoHome 예외 상태의 "링크 저장하러 가기" 버튼 클릭 시 홈 화면으로 이동
 */
@Composable
private fun CurationMonthlyDetailScreenContent(
    onBack: () -> Unit,
    nickname: String = "",
    title: String = "2026\n월간 큐레이션 5월호",
    headerMent: String = "",
    footerMent: String = "",
    emotionItems: List<EmotionItem> = emptyList(),
    recommendedLinks: List<RecommendLink> = emptyList(),
    onLinkClick: (RecommendLink) -> Unit = {},
    onLinkDeleteClick: (RecommendLink) -> Unit = {},
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
                title = title,
                titleDescriptionGap = 12.scaler, // 피그마상 18.49f인데 아무리 봐도 피그마랑 다른데? 12로 했는데 디자이너와 조정해주세용
                description = "${nickname}님의 이번 달을 링큐가 분석했어요!"
            )

            Spacer(modifier = Modifier.height(if (isEmpty) 185.scaler else 78.scaler))

            Column(modifier = Modifier.padding(horizontal = 30.scaler)) {
                CurationEmotionSection(items = emotionItems)

                if (!isEmpty) {
                    Spacer(modifier = Modifier.height(40.scaler))

                    Text(
                        text = headerMent,
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

                    Text(
                        text = footerMent,
                        style = LocalTextStyle.current.copy(
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
        RecommendLink(
            userLinkuId = 1L,
            title = "오픽 AL 따는 꿀팁 얼른 보러오세요",
            url = "https://blog.naver.com/example",
            imageUrl = "",
            domain = "blog.naver.com",
            domainImageUrl = "",
            categories = listOf("생산성·툴", "평온"),
            type = LinkType.Internal(linkId = 1L)
        ),
        RecommendLink(
            userLinkuId = 2L,
            title = "오픽 공부할 때 문법도 공부해야할까?",
            url = "https://github.com/example",
            imageUrl = "",
            domain = "github.com",
            domainImageUrl = "",
            categories = listOf("여행", "행복"),
            type = LinkType.Internal(linkId = 2L)
        ),
        RecommendLink(
            userLinkuId = null,
            title = "글램핑 예약, 누구보다 싸게하기",
            url = "https://blog.naver.com/example2",
            imageUrl = "",
            domain = "blog.naver.com",
            domainImageUrl = "",
            categories = emptyList(),
            type = LinkType.External(url = "https://blog.naver.com/example2")
        )
    )

    LinkuPreview {
        CurationMonthlyDetailScreenContent(
            onBack = {},
            nickname = "세나",
            title = "2026\n월간 큐레이션 5월호",
            headerMent = "생각은 많은데 정리가 안되죠.\n세나님의 머릿속을 환기시켜줄 콘텐츠들을 모았어요!",
            footerMent = "지금 떠오르지 않아도 괜찮아요.\n영감은 가끔, 쉬고 있을 때 더 잘 찾아오거든요.",
            emotionItems = emotions,
            recommendedLinks = links
        )
    }
}

@Preview(name = "#42-1 예외_데이터 없는 경우", showBackground = true)
@Composable
private fun CurationMonthlyDetailScreenEmptyPreview() {
    LinkuPreview {
        CurationMonthlyDetailScreenContent(
            onBack = {},
            nickname = "세나",
            title = "2026\n월간 큐레이션 5월호",
            emotionItems = emptyList(),
            recommendedLinks = emptyList()
        )
    }
}
