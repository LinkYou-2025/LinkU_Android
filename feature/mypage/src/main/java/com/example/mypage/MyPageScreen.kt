package com.example.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.R as Res

@Composable
fun MyPageScreen() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 수동 스크롤 설정
            .background(LocalColorTheme.current.gray[100])
    ) {
        // 탑 바
        TopBar()

        // 설정
        Column(
            modifier = Modifier.padding(16.dp, 24.dp)
        ) {
            Text(
                text = "설정",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = LocalColorTheme.current.black
            )

            // 서비스 설정
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(LocalColorTheme.current.white)
                    .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
            ) {
                Text(
                    text = "서비스 설정",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[500]
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        text = "계정 설정",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.gray[800],
                        modifier = Modifier.padding(start = 6.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(id = Res.drawable.ic_detail),
                        contentDescription = null,
                        tint = LocalColorTheme.current.gray[500]
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        text = "알림 설정",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.gray[800],
                        modifier = Modifier.padding(start = 6.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(id = Res.drawable.ic_detail),
                        contentDescription = null,
                        tint = LocalColorTheme.current.gray[500]
                    )
                }
            }
            
            Spacer(modifier = Modifier.padding(top = 15.dp))

            // 고객 센터
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(LocalColorTheme.current.white)
                    .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
            ) {
                Text(
                    text = "고객 센터",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[500]
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "1 : 1 문의",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "공지사항",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.padding(top = 15.dp))

            // 기타
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(LocalColorTheme.current.white)
                    .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
            ) {
                Text(
                    text = "기타",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[500]
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "회원탈퇴",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "로그아웃",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TopBar() {
    val buttonBrush = Brush.horizontalGradient(
            listOf(
                Color(0xFF2C6FFF).copy(alpha = 0.2f),
                Color(0xFFC800FF).copy(alpha = 0.2f)
            )
        )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(LocalColorTheme.current.white)
            .padding(bottom = 17.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp, 17.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 33.38.dp)
            ) {
                Image(
                    painter = painterResource(id = Res.drawable.ic_linkukor),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 1.62.dp, start = 17.dp)
                        .height(24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = Res.drawable.ic_alarm),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 11.8.dp)
                        .height(27.18.dp),
                    tint = LocalColorTheme.current.gray[300]
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.img_user_default),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(start = 2.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = "세나",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "linkU2025@gmail.com",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        color = LocalColorTheme.current.gray[400]
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "여성",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = LocalColorTheme.current.purple[200]
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "·",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = LocalColorTheme.current.gray[400]
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "대학생",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                            color = LocalColorTheme.current.blue[200]
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            InfoCard(title = "나의 링크", count = "0")
            Spacer(modifier = Modifier.width(8.dp))

            InfoCard(title = "나의 폴더", count = "0")
            Spacer(modifier = Modifier.width(8.dp))

            InfoCard(title = "AI 요약 링크", count = "0", borderBrush = buttonBrush)
        }
    }
}

@Composable
fun InfoCard(title: String, count: String, borderBrush: Brush? = null) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .height(67.dp)
            .then(
                if (borderBrush != null) {
                    Modifier
                        .background(LocalColorTheme.current.white)
                        .border(
                            width = 1.dp,
                            brush = borderBrush,
                            shape = RoundedCornerShape(14.dp)
                        )
                } else {
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(LocalColorTheme.current.gray[100])
                }
            )
            .clip(RoundedCornerShape(14.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = TextStyle(fontSize = 13.sp),
            color = LocalColorTheme.current.gray[700]
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = count,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = LocalColorTheme.current.gray[700]
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyPageScreen() {
    MyPageScreen()
}