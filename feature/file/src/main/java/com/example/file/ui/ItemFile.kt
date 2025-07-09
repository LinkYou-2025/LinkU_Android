//파일 항목의 메인 레이아웃 파일

package com.example.file

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 파일 내 항목 중 파일 단위 아이템의 레이아웃
@Composable
fun ItemFile(name: String/*id: Int, date: String*/) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0x1A000000),
                shape = RoundedCornerShape(size = 6.dp)
            )
            .width(162.dp)
            .height(70.dp)
            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = Color(0x0D000000),
                    shape = CircleShape
                )
        )

        Text(
            text = name,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Test() {
    ItemFile("유지민민")
    //FileScreen()
}