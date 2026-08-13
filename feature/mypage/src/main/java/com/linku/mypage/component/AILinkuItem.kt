package com.linku.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.linku.core.model.AiArticleLink
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R
import com.linku.design.R as Res

/**
 * AI 요약이 생성된 저장 링크 한 건을 카드 형태로 표시합니다.
 *
 * 카드 전체를 누르면 [onClick]에 링크 ID를 전달합니다. 삭제 메뉴는 노출하지 않으며, 서버
 * 이미지가 비어 있거나 로드에 실패하면 기존 기본 이미지를 표시합니다.
 *
 * @param link 표시할 AI 요약 링크
 * @param onClick 선택한 링크의 [AiArticleLink.userLinkuId]를 전달하는 콜백. 서버 전환 중 ID가
 * 누락된 항목은 클릭 동작을 제공하지 않습니다.
 * @param modifier 카드 외부 레이아웃에 적용할 modifier
 */
@Composable
fun AILinkuItem(
    link: AiArticleLink,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    AILinkuItemContent(
        linkTitle = link.title,
        displayCategoryName = link.displayCategoryName,
        emotionName = link.emotionType?.tagName,
        linkImageUrl = link.linkuImageUrl,
        domainImageUrl = link.domainImageUrl,
        domainName = link.domain,
        onClick = aiLinkuClickAction(link.userLinkuId, onClick),
        modifier = modifier,
    )
}

/** 유효한 사용자 저장 링크 ID가 있을 때만 상세 이동 동작을 만듭니다. */
internal fun aiLinkuClickAction(
    userLinkuId: Long?,
    onClick: (Long) -> Unit,
): (() -> Unit)? = userLinkuId
    ?.takeIf { it > 0L }
    ?.let { validUserLinkuId ->
        { onClick(validUserLinkuId) }
    }

/**
 * AI 요약 링크 카드의 순수 표시 영역입니다.
 *
 * 도메인 모델 생성 없이도 프리뷰할 수 있도록 화면에 필요한 값만 전달받습니다.
 */
@Composable
private fun AILinkuItemContent(
    linkTitle: String,
    displayCategoryName: String,
    emotionName: String?,
    linkImageUrl: String?,
    domainImageUrl: String?,
    domainName: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val tags = listOfNotNull(
        displayCategoryName.takeIf { it.isNotBlank() },
        emotionName?.takeIf { it.isNotBlank() },
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .then(
                if (onClick != null) {
                    Modifier.noRippleClickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = linkImageUrl,
                contentDescription = stringResource(
                    R.string.ai_linku_thumbnail_content_description,
                    linkTitle,
                ),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.img_link_default),
                error = painterResource(R.drawable.img_link_default),
                fallback = painterResource(R.drawable.img_link_default),
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = linkTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp),
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    tags.forEachIndexed { index, tag ->
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.gray[600],
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .background(
                                    color = colors.gray[100],
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )

                        if (index != tags.lastIndex) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(9.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = domainImageUrl,
                        contentDescription = stringResource(
                            R.string.ai_linku_domain_image_content_description,
                            domainName,
                        ),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(Res.drawable.ic_domain_default),
                        error = painterResource(Res.drawable.ic_domain_default),
                        fallback = painterResource(Res.drawable.ic_domain_default),
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = domainName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[600],
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_ai_bookmark),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 18.dp)
                .size(20.dp, 26.dp),
        )
    }
}

/** AI 요약 링크 카드의 서버 이미지 실패 상태를 확인하는 프리뷰입니다. */
@Preview(showBackground = false)
@Composable
private fun PreviewAILinkuItem() {
    ThemeProvider {
        AILinkuItemContent(
            linkTitle = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
            displayCategoryName = "생산성·툴",
            emotionName = "평온",
            linkImageUrl = null,
            domainImageUrl = null,
            domainName = "BLOG",
            onClick = {},
        )
    }
}
