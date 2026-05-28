package com.linku.home.screen

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.home.R
import com.linku.home.component.LinkDetailCustomDropdown
import com.linku.home.ui.home.bar.LinkDetailTopBar
import kotlinx.coroutines.launch

@Composable
fun LinkDetailScreen(
    linkTitle: String,
    category: String,
    emotion: String,
    linkUrl: String,
    memo: String,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,  // 드롭다운으로 변경 예정
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var isDropdownVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LinkDetailTopBar(
                linkTitle = linkTitle,
                category = category,
                emotion = emotion,
                onBack = { onBack() },
                onMoreClick = {
                    isDropdownVisible = !isDropdownVisible
                },
                onLinkGoClick = { uriHandler.openUri(linkUrl) },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 25.dp, start = 20.dp, end = 20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_default),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(LocalColorTheme.current.white)
                        .padding(top = 7.5.dp, start = 22.dp, end = 8.5.dp, bottom = 7.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = linkUrl,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "복사",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[600],
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LocalColorTheme.current.gray[200])
                            .padding(horizontal = 13.5.dp, vertical = 7.dp)
                            .noRippleClickable {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText("linkUrl", linkUrl)
                                        )
                                    )
                                }
                            }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    Text(
                        text = "메모",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = memo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        color = LocalColorTheme.current.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(LocalColorTheme.current.gray[100])
                            .padding(horizontal = 22.dp, vertical = 15.5.dp)
                    )
                }
            }
        }

        if (isDropdownVisible) {
            LinkDetailCustomDropdown(
                onEditClick = {
                    isDropdownVisible = false
                    // 수정 화면 이동 로직 추가 예정
                },
                onDeleteClick = {
                    isDropdownVisible = false
                    // 삭제 로직 추가 예정
                },
                onShareClick = {
                    isDropdownVisible = false
                    // 공유 로직 추가 예정
                },
                onGoClick = {
                    isDropdownVisible = false
                    // 링크 Open 로직 추가 예정
                },
                onDismiss = {
                    isDropdownVisible = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 20.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(18.dp))
                .background(LocalColorTheme.current.maincolor)
                .padding(vertical = 15.dp)
                .noRippleClickable {  },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sparkles),
                contentDescription = null,
                modifier = Modifier.height(17.51.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "AI 요약",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColorTheme.current.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinkDetailScreen() {
    ThemeProvider {
        LinkDetailScreen(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            category = "어학",
            emotion = "평온",
            linkUrl = "https://blog.naver.com/linkU/1234",
            memo = "오픽 시험 준비시 도움이 되는 내용 정리, AI 활용한 공부법 정리 및 다양한 내용이 포함된 링크!!",
            onBack = { },
            onMoreClick = { },
        )
    }
}