package com.example.curation.ui.item

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curation.R
import com.example.design.theme.font.Paperlogy

@Composable
fun CurationCheckOutButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(106.dp)
            .height(40.dp)
            .background(
                color = Color(0x4DFFFFFF),
                shape = RoundedCornerShape(size = 23.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "보러가기",
            style = TextStyle(
                fontSize = 13.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.width(7.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right), // 화살표 아이콘
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1451D5)
@Composable
fun PreviewCurationCheckOutButton() {
    CurationCheckOutButton()
}