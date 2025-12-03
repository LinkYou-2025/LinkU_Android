package com.example.curation.ui.top_bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.Paperlogy
import com.example.curation.ui.util.rememberScaleFactor
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.design.R as Res

@Composable
fun CurationTopBar(
    onClickSearch: () -> Unit = {}
) {
    val scaleFactor = rememberScaleFactor()
    val gap = (15 * scaleFactor).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .background(LocalColorTheme.current.white)
    ) {

        /** ─── 로고 ─── */
        Image(
            painter = painterResource(id = Res.drawable.ic_linkukor),
            contentDescription = "링큐 로고",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 35.dp, top = 44.dp)
                .size(width = 48.dp, height = 24.dp)
        )

        /** ─── 알림 아이콘 ─── */
        Icon(
            painter = painterResource(id = Res.drawable.ic_alarm),
            contentDescription = "알림",
            tint = LocalColorTheme.current.gray[300],
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 29.8.dp, top = 44.dp)
                .size(width = 22.dp, height = 27.dp)
        )

        /** ─── 빠른 링크 검색바 ─── */
        Box(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 44.dp + 24.dp + gap

                )
                .align(Alignment.TopCenter)
                .height(48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(brush = Basic.maincolor)
                .clickable { onClickSearch() }
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(id = Res.drawable.ic_logo_white),
                contentDescription = null,
                tint = LocalColorTheme.current.white,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "빠른 링크 검색",
                color = LocalColorTheme.current.white,
                fontFamily = Paperlogy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewCurationTopBar() {
    CurationTopBar()
}
