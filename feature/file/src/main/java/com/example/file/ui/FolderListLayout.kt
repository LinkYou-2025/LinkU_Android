// 폴더 목록을 보이는 탭의 레이아웃

package com.example.file.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.file.R

@Composable
fun FolderListLayout(){
    Row(
        modifier = Modifier
            .width(360.dp)
            .height(50.dp)
            .background(color = Color(0x4DCCCCCC)),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(
            modifier = Modifier
                .width(331.dp)
                .padding(start = 12.dp),
            text = "나의 폴더",
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,

                /* TODO: 폰트 추가
                * fontFamily = FontFamily(Font(R.font.roboto)),
                */

                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
            )
        )
        Image(
            modifier = Modifier
                .width(12.dp)
                .height(24.dp),
            painter = painterResource(id = R.drawable.check_img),
            contentDescription = "image description"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FolderListLayoutTest() {
    FolderListLayout()
}