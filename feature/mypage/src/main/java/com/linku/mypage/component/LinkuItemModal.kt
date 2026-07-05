package com.linku.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

@Composable
fun LinkuItemModal(
    onClickModal: () -> Unit = { }
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .width(120.dp)
            .graphicsLayer {
                shadowElevation = 10.dp.toPx()
                this.shape = shape
                clip = true
            }
            .clip(RoundedCornerShape(14.dp))
            .background(colors.white)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .noRippleClickable { onClickModal() }
    ) {
        Text(
            text = "삭제하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.gray[800],
            modifier = Modifier.width(90.dp)
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkuItemModal() {
    ThemeProvider {
        LinkuItemModal(onClickModal = { })
    }
}