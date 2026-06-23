package com.linku.design.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.R

@Composable
fun LinkCardItem(
    hasAiSummary: Boolean,
    linkTitle: String,
    tags: List<String> = emptyList(),
    domainName: String? = null,
    isExternalLink: Boolean,
    linkImageUrl: String? = null,
    domainImageUrl: String? = null,
    onDeleteClick: () -> Unit
) {
    var isMenuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (linkImageUrl.isNullOrBlank()) {
                Image(
                    painter = painterResource(R.drawable.img_link_default),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = linkImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp),
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
                        color = MaterialTheme.linkuColors.black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalColorTheme.current.gray[600],
                            modifier = Modifier
                                .background(
                                    color = LocalColorTheme.current.gray[100],
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(9.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!domainImageUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = domainImageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_domain_default),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = domainName ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[600]
                    )
                }
            }

            Box(
                modifier = Modifier.height(85.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .padding(end = 5.dp)
                        .noRippleClickable { isMenuVisible = !isMenuVisible },
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = null,
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

        if (isMenuVisible) {
            DeleteLinkItemModal(
                onDeleteClick = {
                    isMenuVisible = false
                    onDeleteClick()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 36.dp, end = 12.dp)
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
            linkImageUrl = null,
            domainImageUrl = null,
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
            linkImageUrl = null,
            domainImageUrl = null,
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
            linkImageUrl = null,
            domainImageUrl = null,
            domainName = "BLOG",
            onDeleteClick = { }
        )
    }
}