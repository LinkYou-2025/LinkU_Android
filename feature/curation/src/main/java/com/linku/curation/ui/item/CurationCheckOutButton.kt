package com.linku.curation.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.linku.curation.R
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

@Composable
internal fun CurationCheckOutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val colorTheme = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .height(40.scaler)
            .clip(RoundedCornerShape(size = 23.dp))
            .background(
                color = colorTheme.curationCheckOutButtonBackground,
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "보러가기",
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = colorTheme.white,
            modifier = Modifier.padding(end = 5.dp),
        )

        Spacer(modifier = Modifier.width(7.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right), // 화살표 아이콘
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1451D5)
@Composable
fun PreviewCurationCheckOutButton() {

    LinkuPreview {
        CurationCheckOutButton()
    }
}