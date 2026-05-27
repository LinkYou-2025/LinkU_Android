package com.linku.home.ui.home.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.home.R

@Composable
fun LinkDetailTopBar(
    linkTitle: String,
    category: String,
    emotion: String,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onLinkGoClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .background(LocalColorTheme.current.blue[200])
    ) {
        Image(
            painter = painterResource(R.drawable.linku_logo_transparent),
            contentDescription = null,
            modifier = Modifier
                .height(110.dp)
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp, end = 24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back_white),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(11.dp)
                        .noRippleClickable { onBack() }
                )

                Text(
                    text = "새로운 링크",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.white,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterEnd)
                        .noRippleClickable {
                            onMoreClick()
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "더보기",
                        modifier = Modifier
                            .height(18.dp)
                            .align(AbsoluteAlignment.TopRight)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 29.dp, start = 24.dp, end = 24.dp, bottom = 23.dp)  // 편집 모드에서는 top = 20.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)  // 편집 모드에서는 bottom = 11.dp
                ) {
                    Text(
                        text = linkTitle,
                        fontSize = 24.sp,  // 편집모드에서는 22.sp
                        fontWeight = FontWeight.Bold,
                        color = LocalColorTheme.current.white
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LocalColorTheme.current.purple[50])
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        )

                        Text(
                            text = emotion,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LocalColorTheme.current.purple[50])
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .noRippleClickable {
                                onLinkGoClick()
                            },
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_link_go),
                            contentDescription = null,
                            modifier = Modifier.height(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkDetailDropdownItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = text,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.gray[800]
                )
            }
        },
        onClick = onClick,
        modifier = Modifier
            .height(64.dp)
            .padding(horizontal = 12.dp)
    )
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailTopBar() {
    ThemeProvider {
        LinkDetailTopBar(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            category = "어학",
            emotion = "평온",
            onBack = { },
            onMoreClick = { },
            onLinkGoClick = { },
        )
    }
}