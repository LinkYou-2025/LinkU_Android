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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R
import com.linku.design.R as Res

@Composable
fun AILinkuItem(
    linkTitle: String,
    tags: List<String>,
    domainImage: Int? = null,
    domainName: String? = null,
    onClickDelete: () -> Unit = {}
) {
    var isMenuVisible by remember { mutableStateOf(false) }

    val displayTitle = if (linkTitle.length >= 17) {
        "${linkTitle.take(16)}..."
    } else {
        linkTitle
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(30.dp)
                .padding(start = 18.dp)
                .zIndex(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_ai_bookmark),
                contentDescription = null,
                modifier = Modifier.size(20.dp, 26.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.img_link_default),
                contentDescription = null,
                modifier = Modifier.size(85.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = displayTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp)
                )

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
                    if (domainImage != null) {
                        Image(
                            painter = painterResource(domainImage),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = domainName ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[600]
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(85.dp)
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

        if (isMenuVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 36.dp, end = 12.dp)
            ) {
                LinkuItemModal(
                    onClickModal = {
                        isMenuVisible = false
                        onClickDelete()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewAILinkuItem() {
    ThemeProvider {
        AILinkuItem(
            linkTitle = "요즘 대학생들이 진짜 쓰는 앱 TOP10",
            tags = listOf("생산성·툴", "평온"),
            domainImage = Res.drawable.ic_domain_blog_naver_logo,
            domainName = "BLOG",
            onClickDelete = { }
        )
    }
}