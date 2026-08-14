package com.linku.file.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.file.R

/**
 * 공유폴더 그룹 또는 폴더 목록 조회가 성공했지만 결과가 비어 있을 때 표시하는 화면입니다.
 *
 * loading과 error 판단은 상태 소유자가 담당하며, 이 컴포넌트는 호출자가 전달한 문구만
 * 렌더링합니다.
 */
@Composable
internal fun SharedFolderEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_empty_shared_users_grid),
                contentDescription = null,
                modifier = Modifier.size(width = 85.dp, height = 60.55.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontFamily = font,
                    fontWeight = FontWeight.Medium,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
                color = colors.gray[800],
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = font,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
                color = colors.gray[600],
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedFolderEmptyStatePreview() {
    LinkuPreview {
        SharedFolderEmptyState(
            title = "아직 공유중인 폴더가 없어요!",
            subtitle = "지금 폴더를 공유해보세요",
        )
    }
}
