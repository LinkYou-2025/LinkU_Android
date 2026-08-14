package com.linku.mypage.ui.top.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.BrushText
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.font.Taebaek
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.AlarmButton
import com.linku.mypage.R

@Composable
fun MypageTopBar(
    isNoticeExist: Boolean,  // 알림 존재 여부
    nickname: String,
    email: String,
    myLinku: Long,
    myFolder: Long,
    myAiLinku: Long,
    socialLoginType: String,
    onAlarmClick: () -> Unit,
    onAISummaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            )
            .background(color = colors.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 49.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 19.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 24.sp,
                                fontFamily = Taebaek.font,
                                fontWeight = FontWeight.Normal,
                                brush = colors.maincolor,
                            )
                        ) {
                            append("링큐")
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // 알림
                AlarmButton(
                    isNoticeExist = isNoticeExist,
                    onClick = onAlarmClick
                )
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
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconRes = when (socialLoginType.lowercase()) {
                            "kakao" -> R.drawable.ic_kakao_logo
                            "google" -> R.drawable.ic_google_logo  // TODO: 구글 로고 리소스 수정 필요 (다현이에게 svg 파일로 받기)
                            else -> null
                        }

                        if (iconRes != null) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = socialLoginType,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = nickname,
                            color = colors.black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = email,
                        color = colors.gray[600],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.gray[100])
                        .border(
                            1.dp,
                            colors.gray[200],
                            RoundedCornerShape(14.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "나의 링크",
                        color = colors.gray[700],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = myLinku.toString(),
                        color = colors.black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.gray[100])
                        .border(
                            1.dp,
                            colors.gray[200],
                            RoundedCornerShape(14.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "나의 폴더",
                        color = colors.gray[700],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = myFolder.toString(),
                        color = colors.black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.backgroundmaincolor)
                        .border(
                            1.dp,
                            colors.linkuInactiveGradient,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(vertical = 14.dp)
                        .noRippleClickable { onAISummaryClick() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_sparkle_blue),
                            contentDescription = null,
                            modifier = Modifier.size(11.31.dp, 13.21.dp)
                        )

                        Spacer(modifier = Modifier.width(4.56.dp))

                        BrushText(
                            text = "AI 요약 링크",
                            brush = colors.maincolor,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        )
                    }

                    BrushText(
                        text = myAiLinku.toString(),
                        brush = colors.maincolor,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewMypageTopBar() {
    ThemeProvider {
        MypageTopBar(
            isNoticeExist = true,
            onAlarmClick = {},
            onAISummaryClick = {},
            nickname = "세나",
            email = "linkU2025@gmail.com",
            myLinku = 63,
            myFolder = 5,
            myAiLinku = 48,
            socialLoginType = "google"
        )
    }
}