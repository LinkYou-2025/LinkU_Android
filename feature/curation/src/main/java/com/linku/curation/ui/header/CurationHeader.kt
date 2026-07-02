package com.linku.curation.ui.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.curation.R
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
fun CurationHeader(
    modifier: Modifier = Modifier,
    nickname: String,
) {
    val colorTheme = MaterialTheme.linkuColors

    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.img_curation_title),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 24.scaler)
                .size(width = 102.scaler, height = 15.scaler)
        )

        Spacer(modifier = Modifier.height(12.scaler))

        Text(
            text = "${nickname}님을 위한 링큐레이션",
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight(700),
            style = TextStyle(
                brush = colorTheme.curationGradient
            ),
            modifier = Modifier.padding(horizontal = 24.scaler)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurationHeader() {
    LinkuPreview {
        CurationHeader(nickname = "세나")
    }
}
