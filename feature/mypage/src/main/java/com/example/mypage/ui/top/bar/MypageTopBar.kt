package com.example.mypage.ui.top.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.top.bar.AlarmButton
import com.example.mypage.R
import com.example.design.R as Res

@Composable
fun MypageTopBar(
    isNoticeExist: Boolean,  // 알림 존재 여부
    onAlarmClick: () -> Unit,
    nickname: String,
    email: String,
    myLinku: Long,
    myFolder: Long,
    myAiLinku: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            )
            .background(color = LocalColorTheme.current.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_linkukor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp, 24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // 알림
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onAlarmClick() }
                ) {
                    AlarmButton(
                        isNoticeExist = isNoticeExist,
                        modifier = Modifier
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 사용자 프로필
                Image(
                    painter = painterResource(R.drawable.ic_profile_default),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                )

                Spacer(modifier = Modifier.width(18.dp))

                // 사용자 이름 및 이메일
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nickname,
                        color = LocalColorTheme.current.black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LocalFontTheme.current.font,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = email,
                        color = LocalColorTheme.current.gray[600],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 나의 링크
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LocalColorTheme.current.gray[100])
                        .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(14.dp))
                        .padding(horizontal = 35.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "나의 링크",
                        color = LocalColorTheme.current.gray[700],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = myLinku.toString(),
                        color = LocalColorTheme.current.black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LocalFontTheme.current.font
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                // 나의 폴더
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LocalColorTheme.current.gray[100])
                        .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(14.dp))
                        .padding(horizontal = 35.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "나의 폴더",
                        color = LocalColorTheme.current.gray[700],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = myFolder.toString(),
                        color = LocalColorTheme.current.black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LocalFontTheme.current.font
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                // AI 요약 링크
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LocalColorTheme.current.backgroundmaincolor)
                        .border(1.dp, LocalColorTheme.current.inactiveColor, RoundedCornerShape(14.dp))
                        .padding(horizontal = 35.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_sparkle),
                            contentDescription = null,
                            modifier = Modifier.size(11.31.dp, 13.21.dp)
                        )

                        Spacer(modifier = Modifier.width(4.56.dp))

                        Text(
                            text = "AI 요약 링크",
                            color = LocalColorTheme.current.gray[700],
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = LocalFontTheme.current.font,
                        )
                    }

                    Text(
                        text = myAiLinku.toString(),
                        color = LocalColorTheme.current.black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LocalFontTheme.current.font
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewMypageTopBar() {
    MypageTopBar(
        isNoticeExist = true,
        onAlarmClick = {},
        nickname = "세나",
        email = "linkU2025@gmail.com",
        myLinku = 63,
        myFolder = 5,
        myAiLinku = 48
    )
}