package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.mypage.R

@Composable
fun AccountSettingScreen() {
    val username = "세나"  // 외부에서 정의한 초기 이름
    val userjob = "대학생"
    var name by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }

    // 변경 사항이 있는지 여부 확인
    val isModified = name != "" && name != username || job != "" && job != userjob

    // 비활성화용 그라데이션 브러시
    val inactiveBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFFD4E1FF),
            Color(0xFFF2CCFF)
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.width(10.dp)
                )

                Spacer(modifier = Modifier.width(135.dp))

                Text(
                    text = "계정 설정",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black
                )
            }

            Spacer(modifier = Modifier.height(41.75.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "닉네임",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 텍스트 입력 필드
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = LocalColorTheme.current.black,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (name.isEmpty()) {
                                    Text(
                                        text = username,
                                        style = TextStyle(
                                            color = LocalColorTheme.current.gray[400],
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_delete_gray),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(18.dp)
                                .then(
                                    if (name.isNotEmpty()) Modifier.clickable { name = "" }
                                    else Modifier // 클릭 불가 상태
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                    ) {
                Text(
                    text = "현재 하고 계신 일이나 활동",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 추후 토글로 수정
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 텍스트 입력 필드
                        BasicTextField(
                            value = job,
                            onValueChange = { job = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = LocalColorTheme.current.black,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (job.isEmpty()) {
                                    Text(
                                        text = userjob,
                                        style = TextStyle(
                                            color = LocalColorTheme.current.gray[400],
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_toggle_gray),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(18.dp)
                                .then(
                                    if (job.isNotEmpty()) Modifier.clickable { name = "" }
                                    else Modifier // 클릭 불가 상태
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "링큐 활용 목적",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = LocalColorTheme.current.black
                    )

                    Text(
                        text = "복수선택",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.blue[200],
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // 선택




            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "관심 콘텐츠",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = LocalColorTheme.current.black
                    )

                    Text(
                        text = "복수선택",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.blue[200],
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isModified) Basic.maincolor else inactiveBrush
                    )
                    .clickable(enabled = isModified) {
                        // TODO: 변경 로직
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "변경하기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun Tag

@Preview(showBackground = true)
@Composable
fun PreviewAccountSettingScreen() {
    AccountSettingScreen()
}