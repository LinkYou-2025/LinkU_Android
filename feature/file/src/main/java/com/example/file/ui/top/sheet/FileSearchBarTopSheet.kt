package com.example.file.ui.top.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.theme.Black
import com.example.file.ui.theme.DefaultFont
import com.example.file.ui.theme.Gray100
import com.example.file.ui.theme.Gray500
import com.example.file.ui.theme.Gray600
import com.example.file.ui.theme.White

@Composable
fun FileSearchBarTopSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // (1) 뒷 배경(클릭시 닫기)
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .noRippleClickable { onDismiss() }
            )
        }

        // (2) 위에서 내려오는 Top Sheet
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it } // 위에서 시작!
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it } // 위로 사라짐!
            )
        ) {
            Surface(
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                tonalElevation = 8.dp,
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp) // 원하는 높이
            ) {
                Box(
                    Modifier
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 46.dp),
                        horizontalArrangement = Arrangement.spacedBy(21.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            modifier = Modifier
                                .width(10.dp),
                            tint = Gray600,
                            painter = painterResource(R.drawable.left_arrow_head_icon_img),
                            contentDescription = null
                        )
                        // 검색창 전체 바탕(틀)
                        Surface(
                            modifier = Modifier
                                // 가로 341dp, 세로 42dp로 전체 크기 지정
                                .size(width = 341.dp, height = 42.dp),

                            // 배경색 (Gray100)
                            color = Gray100,

                            // 모서리 둥글게 (18dp)
                            shape = RoundedCornerShape(18.dp),
                        ) {

                            // 내부 요소 가로 정렬
                            Row(
                                // 전체 영역을 가득 채우도록
                                modifier = Modifier.fillMaxSize(),

                                // 세로 정렬: 세로 중앙 정렬
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(13.dp)
                            ) {

                                // 왼쪽 링크 로고 아이콘
                                Icon(
                                    // 왼쪽 여백 (18.51dp)
                                    modifier = Modifier.padding(start = 18.51.dp),

                                    // 아이콘 색상 (Gray500)
                                    tint = Gray500,

                                    // 사용할 아이콘 이미지 리소스 (drawable/linku_logo.xml)
                                    painter = painterResource(id = R.drawable.linku_logo),

                                    // 이미지 설명 ("링큐 로고")
                                    contentDescription = "링큐 로고"
                                )

                                var text by remember { mutableStateOf("") }
                                BasicTextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    modifier = Modifier
                                        .weight(1F),
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontFamily = DefaultFont,
                                        fontWeight = FontWeight.Normal,
                                        color = Black,
                                    ),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            // 입력값이 없을 때만 placeholder 보임!
                                            if (text.isEmpty()) {
                                                Text(
                                                    text = " 빠른 링크 검색",
                                                    fontSize = 15.sp,
                                                    lineHeight = 22.sp,
                                                    fontFamily = DefaultFont,
                                                    fontWeight = FontWeight.Normal,
                                                    color = Black.copy(alpha = 0.4f) // placeholder는 살짝 연하게
                                                )
                                            }
                                            innerTextField() // 실제 입력창
                                        }
                                    }
                                )

                                Image(
                                    modifier = Modifier
                                        .padding(end = 18.dp)
                                        .size(18.dp)
                                        .noRippleClickable{ onDismiss() },
                                    painter = painterResource(R.drawable.clear_icon_img),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileSearchBarTopSheetTest(){
    FileSearchBarTopSheet(true) { }
}