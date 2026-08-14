package com.linku.curation.ui.bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Taebaek

/**
 * 큐레이션 화면 상단 로고 바. 홈 탑바([com.linku.home.ui.home.bar.HomeTopBar])와 동일한
 * 위치/스타일(패딩, 폰트, 그라데이션)의 "링큐" 로고를 사용한다.
 */
@Composable
fun CurationTopBar(
    modifier: Modifier = Modifier,
) {
    val colorTheme = LocalColorTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 49.dp, bottom = 13.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 19.dp, end = 12.dp)
                // 홈/마이페이지는 30dp 알림 아이콘이 있어 Row 높이가 30dp로 고정되고 로고가 그 안에서
                // 중앙 정렬됨. 큐레이션은 알림 아이콘이 없어 Row가 텍스트 높이로 줄어들면서 로고가
                // 미묘하게 위로 붙어 보였음 — 동일한 30dp를 강제해 세 화면의 로고 위치를 맞춘다.
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 24.sp,
                            fontFamily = Taebaek.font,
                            fontWeight = FontWeight.Normal,
                            brush = colorTheme.maincolor,
                        )
                    ) {
                        append("링큐")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationTopBar() {
    LinkuPreview {
        CurationTopBar()
    }
}
