package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R
import com.linku.mypage.component.NoticeItem
import com.linku.mypage.model.noticeList

/** 마이페이지의 정적 공지 목록과 펼침 상태를 표시합니다. */
@Composable
fun NoticeScreen(
    onBackClick: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    var expandedNoticeId by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray[100])
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.notice_back_content_description),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(11.dp)
                    .noRippleClickable { onBackClick() }
            )

            Text(
                text = stringResource(R.string.notice_screen_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = colors.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.5.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = noticeList,
                key = { notice -> notice.id },
            ) { notice ->
                NoticeItem(
                    category = stringResource(notice.categoryResId),
                    title = stringResource(notice.titleResId),
                    contents = stringResource(notice.contentResId),
                    expanded = expandedNoticeId == notice.id,
                    onToggle = {
                        expandedNoticeId = if (expandedNoticeId == notice.id) {
                            null
                        } else {
                            notice.id
                        }
                    },
                )

                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

/** 두 정적 공지를 표시하는 전체 화면 프리뷰입니다. */
@Preview(showBackground = true)
@Composable
fun PreviewNoticeScreen() {
    ThemeProvider {
        NoticeScreen(onBackClick = {})
    }
}
