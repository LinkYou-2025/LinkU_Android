package com.linku.design.component

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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors


@Composable
fun LinkCardItem(
    hasAiSummary: Boolean,
    linkTitle: String,
    tags: List<String> = emptyList(),
    domainName: String = "",
    isExternalLink: Boolean,
    linkImageUrl: String = "",
    domainImageUrl: String = "",
    isDeleteMenuVisible: Boolean = false,
    onMoreClick: () -> Unit = { },
    onCardClick: () -> Unit = { },  // TODO: 머지 이후 기본값 제거 예정
    onDeleteClick: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .noRippleClickable(onClick = onCardClick)
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        // 폰트 기본 여백 때문에 아이콘이랑 세로 중앙이 안 맞아서 제거함
                        style = LocalTextStyle.current.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 1.dp)
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                val tagChipModifier = Modifier
                    .background(
                        color = colors.gray[100],
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)

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
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = domainName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[600]
                    )
                }
            }

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
                        contentDescription = "더보기",
                        tint = colors.gray[400],
                        modifier = Modifier.size(17.dp)
                    )
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
                    .padding(top = 36.dp, end = 12.dp),
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