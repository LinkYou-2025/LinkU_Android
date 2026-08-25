package com.linku.link.screen

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.linku.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import kotlinx.coroutines.launch

/**
 * 공유폴더 링크의 읽기 전용 상세 화면입니다.
 *
 * 공유폴더 링크는 소유자 상세 API(`GET linku/{userLinkuId}`)를 호출할 수 없어(다른 사용자
 * 소유라 404가 남), 폴더 내부 링크 목록 조회 시점에 이미 받아온 값만으로 화면을 구성합니다.
 * 메모·감정·상황·카테고리·AI 요약처럼 목록 응답에 없는 정보와 수정·삭제 동작은 제공하지 않습니다.
 *
 * @param linkTitle 링크 제목
 * @param linkUrl 원본 링크 URL
 * @param imageUrl 링크 썸네일 URL
 * @param tags 링크 키워드 태그 목록
 * @param onBack 뒤로가기 콜백
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharedLinkDetailScreen(
    linkTitle: String,
    linkUrl: String,
    imageUrl: String?,
    tags: List<String>,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val detailScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
    ) {
        val visibleTags = tags
            .filter { it.isNotBlank() }
            .take(4)
            .map { tag -> if (tag.startsWith("#")) tag else "#$tag" }

        // 저장된 링크 상세(SaveLinkResultScreen/LinkDetailScreen)와 동일한 헤더 규격(패딩·spacer
        // 높이·폰트 크기)을 그대로 맞춰서 화면 전환 시 헤더 크기가 흔들리지 않게 합니다.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                .background(colors.blue[200])
        ) {
            Image(
                painter = painterResource(R.drawable.linku_logo_transparent),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 9.dp)
            )

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 59.dp, start = 20.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                        Image(
                            painter = painterResource(R.drawable.ic_back_white),
                            contentDescription = "뒤로가기",
                            modifier = Modifier
                                .size(width = 10.dp, height = 16.25.dp)
                                .noRippleClickable { onBack() }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "공유된 링크",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.white,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // 공유받은 링크는 수정할 수 없어 기존 "수정"·더보기 자리는 비워 둡니다.
                    Spacer(modifier = Modifier.width(40.dp))
                }

                Spacer(modifier = Modifier.height(34.75.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = linkTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.white,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(19.dp))

                // Figma(#24-1) 기준 제목 바로 아래, 태그 칩과 같은 줄 오른쪽 끝에 원본 링크
                // 바로가기 아이콘이 위치합니다. 공유폴더 상세는 태그를 헤더에 두지 않으므로
                // 아이콘만 같은 위치에 남깁니다.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_link_go),
                        contentDescription = "원본 링크 열기",
                        modifier = Modifier
                            .size(20.dp)
                            .noRippleClickable { uriHandler.openUri(linkUrl) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(detailScrollState)
                .padding(top = 25.dp, start = 20.dp, end = 20.dp, bottom = 50.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.img_link_detail_default),
                error = painterResource(R.drawable.img_link_detail_default),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = colors.gray[200],
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
                        color = colors.gray[200],
                        shape = RoundedCornerShape(18.dp)
                    )
                    .background(colors.white)
                    .padding(top = 7.5.dp, start = 22.dp, end = 8.5.dp, bottom = 7.5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = linkUrl,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    color = colors.black,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "복사",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.gray[600],
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.gray[200])
                        .noRippleClickable {
                            coroutineScope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(ClipData.newPlainText("linkUrl", linkUrl))
                                )
                            }
                        }
                        .padding(horizontal = 13.5.dp, vertical = 7.dp)
                )
            }

            if (visibleTags.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    Text(
                        text = "태그",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        visibleTags.forEach { tag ->
                            Text(
                                text = tag,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colors.black,
                                lineHeight = 20.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, colors.gray[200], RoundedCornerShape(20.dp))
                                    .background(colors.white)
                                    .padding(horizontal = 15.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSharedLinkDetailScreen() {
    ThemeProvider {
        SharedLinkDetailScreen(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            linkUrl = "https://blog.naver.com/linkU/1234567890",
            imageUrl = null,
            tags = listOf("오픽", "AL", "영어회화", "자격증"),
            onBack = {},
        )
    }
}
