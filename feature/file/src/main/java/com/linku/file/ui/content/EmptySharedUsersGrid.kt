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
import com.linku.file.R


/**
 * 공유 중인 폴더가 없을 때 표시되는 빈 화면 컴포저블입니다.
 *
 * 사용자가 공유 중인 폴더가 없음을 알리는 이미지와 안내 문구를 화면 중앙에 배치합니다.
 *
 * @param modifier 이 컴포저블의 레이아웃에 적용할 [Modifier]
 */
@Composable
internal fun EmptySharedUsersGrid(
    modifier: Modifier = Modifier
) {
    /** 섹션 제목과 삭제 모달 문구에 사용할 LinkU 테마 색상 팔레트입니다. */
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_empty_shared_users_grid),
                contentDescription = null,
                modifier = Modifier.size(width = 85.dp, height = 60.55f.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "아직 공유중인 폴더가 없어요!",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                color = colors.gray[800],
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "지금 폴더를 공유해보세요",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                color = colors.gray[600],
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptySharedUsersGridPreview() {
    LinkuPreview {
        EmptySharedUsersGrid()
    }
}
