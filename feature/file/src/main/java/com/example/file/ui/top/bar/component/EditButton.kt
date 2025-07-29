// 수정 버튼

package com.example.file.ui.top.bar.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.White

@Composable
fun EditButton() {

    // 수정 버튼
    Text(
        // 텍스트 내용 ("수정")
        text = "수정",

        // 텍스트 크기 (15sp)
        fontSize = 15.sp,

        // 한 줄 높이 (22sp)
        lineHeight = 22.sp,

        // 사용할 폰트 (paperlogy 폰트)
        fontFamily = DefaultFont,

        // 폰트 굵기 (보통)
        fontWeight = FontWeight.Normal,

        // 글자색 (White)
        color = White,

        // 텍스트 정렬 방식 (오른쪽 정렬)
        textAlign = TextAlign.Right,
    )
}

@Preview(showBackground = true)
@Composable
fun EditButtonTest() {
    EditButton()
}