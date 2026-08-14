package com.linku.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.ThemeProvider

@Composable
fun NoticeTitleSection(
    modifier: Modifier = Modifier,
    title: String,
    noticeDate: String
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight(500),
                lineHeight = 20.sp
            )
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = noticeDate,
            style = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight(300),
                lineHeight = 20.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoticeTitleSectionPreview() {
    ThemeProvider {
        NoticeTitleSection(
            title = "개인정보 이용제공·내역 안내",
            noticeDate = "26.03.31"
        )
    }
}