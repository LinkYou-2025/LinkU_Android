package com.linku.design.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.network.HttpException
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import java.net.URI

/** release 기기에서 링크 썸네일의 임시 진단 결과를 찾기 위한 Logcat 태그입니다. */
private const val LINK_THUMBNAIL_LOG_TAG = "LinkThumbnail"

/**
 * 썸네일의 로딩 결과를 release에서도 기록하되 주소의 경로, 쿼리, 인증 정보는 제외합니다.
 *
 * 예외 메시지와 스택 트레이스에는 원본 URL이 포함될 수 있어 예외 종류만 기록합니다.
 * 원인 예외는 최대 8개까지만 확인하여 순환 참조에 의한 무한 순회를 방지합니다.
 *
 * @param event 로딩 단계 또는 결과를 나타내는 고정 문자열입니다.
 * @param imageUrl 이미지 로더에 전달한 주소입니다.
 * @param failure 이미지 로더가 반환한 실패 원인입니다.
 */
private fun logLinkThumbnail(event: String, imageUrl: String, failure: Throwable? = null) {
    // 엄격한 URI 파싱에 실패하면 원본 주소를 출력하지 않고 누락된 메타데이터로 표시합니다.
    val uri = runCatching { URI(imageUrl) }.getOrNull()
    val causes = generateSequence(failure) { it.cause }.take(8).toList()
    val httpStatus = causes.filterIsInstance<HttpException>().firstOrNull()?.response?.code
    val exceptionTypes = causes.joinToString(",") { it.javaClass.simpleName }
        .ifEmpty { "none" }

    // Log.i를 사용해 debug 전용 로거에 의해 release 진단 로그가 생략되지 않도록 합니다.
    Log.i(
        LINK_THUMBNAIL_LOG_TAG,
        "event=$event blank=${imageUrl.isBlank()} " +
            "scheme=${uri?.scheme ?: "unknown"} host=${uri?.host ?: "unknown"} " +
            "exceptions=$exceptionTypes httpStatus=${httpStatus ?: "none"}",
    )
}


/**
 * 저장 링크의 썸네일, 분류 태그, 도메인 정보와 선택 가능한 메뉴를 카드로 표시합니다.
 *
 * [onCardClick]이 `null`이면 카드 본문에 클릭 modifier를 부착하지 않습니다. 더보기 버튼과
 * 삭제 메뉴의 표시 상태는 호출 화면이 관리하며, 이 컴포넌트는 전달받은 콜백만 실행합니다.
 *
 * @param hasAiSummary AI 요약이 존재해 북마크를 표시할지 여부
 * @param linkTitle 카드에 표시할 링크 제목
 * @param modifier 카드 외부 레이아웃에 적용할 modifier
 * @param tags 카드에 표시할 분류 태그 목록
 * @param domainName 링크의 도메인 이름
 * @param isExternalLink 외부 링크 아이콘을 표시할지 여부
 * @param linkImageUrl 링크 대표 이미지 URL
 * @param domainImageUrl 도메인 이미지 URL
 * @param isMoreVisible 우측 더보기 버튼을 표시할지 여부
 * @param isDeleteMenuVisible 카드 위에 삭제 메뉴를 표시할지 여부
 * @param onMoreClick 더보기 버튼을 눌렀을 때 호출되는 콜백
 * @param onCardClick 카드 본문을 눌렀을 때 호출되는 콜백. `null`이면 카드 본문은 클릭할 수 없음
 * @param onDeleteClick 삭제 메뉴를 눌렀을 때 호출되는 콜백
 */
@Composable
fun LinkCardItem(
    hasAiSummary: Boolean,
    linkTitle: String,
    modifier: Modifier = Modifier,
    tags: List<String> = emptyList(),
    domainName: String = "",
    isExternalLink: Boolean,
    linkImageUrl: String = "",
    domainImageUrl: String = "",
    isMoreVisible: Boolean = true,
    isDeleteMenuVisible: Boolean = false,
    onMoreClick: () -> Unit = { },
    onCardClick: (() -> Unit)? = null,
    onDeleteClick: () -> Unit = {},
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .then(
                onCardClick?.let { cardClick ->
                    Modifier.noRippleClickable(onClick = cardClick)
                } ?: Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = linkImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.img_link_default),
                error = painterResource(R.drawable.img_link_default),
                // 임시 진단: 원본 주소나 예외 메시지 없이 로딩 단계와 실패 종류만 기록합니다.
                onLoading = { logLinkThumbnail("loading", linkImageUrl) },
                onSuccess = { logLinkThumbnail("success", linkImageUrl) },
                onError = { state ->
                    logLinkThumbnail("error", linkImageUrl, state.result.throwable)
                },
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 최소 높이만 20dp로 맞추고, 폰트 크게 설정 등으로 더 필요하면 늘어나게 둠(글자 짤림 방지)
                        .heightIn(min = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isExternalLink) {
                        Image(
                            painter = painterResource(R.drawable.ic_out_link),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = linkTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black,
                        lineHeight = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        // 폰트 기본 여백 때문에 아이콘이랑 세로 중앙이 안 맞아서 제거함
                        style = LocalTextStyle.current.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                val tagChipModifier = Modifier
                    .background(
                        color = colors.gray[100],
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)

                Row(
                    // 태그가 없어도 도메인 행 위치가 밀리지 않도록 최소 높이만 20dp로 고정
                    modifier = Modifier.heightIn(min = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.gray[600],
                            lineHeight = 14.sp,
                            modifier = tagChipModifier
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(9.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = domainImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_domain_default),
                        error = painterResource(R.drawable.ic_domain_default),
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = domainName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[800],
                        lineHeight = 14.sp,
                    )
                }
            }

            if (isMoreVisible) {
                Box(
                    modifier = Modifier
                        .height(85.dp)
                        .width(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .padding(top = 8.dp, end = 5.dp)
                            .noRippleClickable(onClick = onMoreClick),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_more),
                            contentDescription = stringResource(
                                R.string.link_card_more_content_description,
                            ),
                            tint = colors.gray[400],
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        if (hasAiSummary) {
            Image(
                painter = painterResource(R.drawable.ic_ai_bookmark),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 18.dp)
                    .size(20.dp, 26.dp)
            )
        }

        if (isDeleteMenuVisible) {
            DeleteLinkItemModal(
                onDeleteClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 49.dp, end = 15.dp),
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkCardItem_HasAiSummary() {
    ThemeProvider {
        LinkCardItem(
            hasAiSummary = true,
            linkTitle = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
            tags = listOf("생산성·툴", "평온"),
            isExternalLink = false,
            linkImageUrl = "",
            domainImageUrl = "",
            domainName = "BLOG",
            onDeleteClick = { }
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkCardItem_NoAiSummary() {
    ThemeProvider {
        LinkCardItem(
            hasAiSummary = false,
            linkTitle = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
            tags = listOf("생산성·툴", "평온"),
            isExternalLink = false,
            linkImageUrl = "",
            domainImageUrl = "",
            domainName = "BLOG",
            onDeleteClick = { }
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkCardItem_HasOutLink() {
    ThemeProvider {
        LinkCardItem(
            hasAiSummary = true,
            linkTitle = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
            tags = listOf("생산성·툴", "평온"),
            isExternalLink = true,
            linkImageUrl = "",
            domainImageUrl = "",
            domainName = "BLOG",
            onCardClick = { },
            onDeleteClick = { }
        )
    }
}
