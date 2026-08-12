package com.linku.mypage.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R

/**
 * 정적 공지 한 건을 접고 펼칠 수 있는 카드로 표시합니다.
 *
 * [contents] 안의 `**`로 감싼 구간은 실제 굵은 글씨로 변환되며 마커 자체는 노출하지
 * 않습니다.
 *
 * @param category 공지 유형
 * @param title 공지 제목
 * @param contents 줄바꿈과 굵은 글씨 마커를 포함한 공지 본문
 * @param expanded 본문 펼침 여부
 * @param onToggle 카드 펼침 상태 변경 요청
 */
@Composable
fun NoticeItem(
    category: String,
    title: String,
    contents: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val annotatedContents = remember(contents) { contents.toNoticeAnnotatedString() }

    var hasEverExpanded by remember { mutableStateOf(false) }
    var hasBeenRead by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded) {
            hasEverExpanded = true
        } else if (hasEverExpanded) {
            hasBeenRead = true
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "notice_arrow_rotation"
    )

    val cardShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = cardShape,
                ambientColor = colors.black.copy(alpha = 0.02f),
                spotColor = colors.black.copy(alpha = 0.02f)
            )
            .clip(cardShape)
            .background(colors.white)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    if (hasBeenRead) R.drawable.ic_notice_gray else R.drawable.ic_notice
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(19.dp)
                    .align(Alignment.Top)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (hasBeenRead) {
                        colors.gray[300]
                    } else {
                        colors.gray[600]
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (hasBeenRead) {
                        colors.gray[400]
                    } else {
                        colors.black
                    }
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Image(
                painter = painterResource(R.drawable.ic_arrow_down_gray500),
                contentDescription = null,
                modifier = Modifier
                    .height(6.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        transformOrigin = TransformOrigin.Center
                    }
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(13.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.gray[200])
                )

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = annotatedContents,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[700],
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }
    }
}

/**
 * 공지 본문의 `**...**` 구간을 굵게 표시하는 [AnnotatedString]으로 변환합니다.
 *
 * 줄바꿈을 포함한 강조 구간을 지원하며 닫는 마커가 없으면 남은 문자열을 원문 그대로
 * 보존합니다.
 */
internal fun String.toNoticeAnnotatedString(): AnnotatedString {
    val source = this

    return buildAnnotatedString {
        var currentIndex = 0

        while (currentIndex < source.length) {
            val boldStart = source.indexOf(BOLD_MARKER, startIndex = currentIndex)
            if (boldStart < 0) {
                append(source.substring(currentIndex))
                break
            }

            val boldEnd = source.indexOf(
                BOLD_MARKER,
                startIndex = boldStart + BOLD_MARKER.length,
            )
            if (boldEnd < 0) {
                append(source.substring(currentIndex))
                break
            }

            append(source.substring(currentIndex, boldStart))
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(source.substring(boldStart + BOLD_MARKER.length, boldEnd))
            }
            currentIndex = boldEnd + BOLD_MARKER.length
        }
    }
}

/** 공지 본문에서 굵은 글씨 구간을 표시하는 마커입니다. */
private const val BOLD_MARKER = "**"

/** 공지 카드의 펼침 상태와 부분 강조 본문을 확인하는 프리뷰입니다. */
@Preview(showBackground = false)
@Composable
fun PreviewNoticeItem() {
    ThemeProvider {
        NoticeItem(
            category = "시스템/공지",
            title = "개인정보 이용제공·내역 안내",
            contents = """
안녕하세요. 링큐입니다.
링큐는 **개인정보보호법 제20조의 2**에 따라 회원님들께 개인정보 이용·제공 내역을 확인 가능한 방법을 안내드리고 있습니다.

개인정보 이용·제공내역 확인 방법
 • 메인[홈] > 화면 하단 내 [개인정보 처리방침] 클릭 > [개인정보의 처리목적], [개인정보의 제3자 제공] 클릭

앞으로도 회원님들의 개인정보 보호를 위해 최선을 다하겠습니다. 감사합니다.

해당 안내는 링큐 회원님들 대상으로 발송되며, 여러 개의 계정 보유 시 중복으로 발송될 수 있습니다. 문의 사항은 고객행복센터(1670-6250)를 이용해 주시기 바랍니다.
            """.trimIndent(),
            expanded = true,
            onToggle = {},
        )
    }
}
