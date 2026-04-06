package com.linku.home.ui.top.bar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.file.R
import com.linku.file.ui.theme.DefaultFont
import com.linku.file.ui.theme.White


@Composable
fun HomeSearchBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(brush = LocalColorTheme.current.maincolor),
            horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(start = 18.51.dp),
                tint = White,
                painter = painterResource(id = R.drawable.linku_logo),
                contentDescription = "링큐 로고"
            )

            Text(
                text = "빠른 링크 검색",
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = DefaultFont,
                fontWeight = FontWeight(500),
                color = White,
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewHomeSearchBar() {
    HomeSearchBar()
}