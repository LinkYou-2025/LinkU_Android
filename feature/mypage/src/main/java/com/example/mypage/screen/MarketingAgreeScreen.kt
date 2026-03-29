package com.example.mypage.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.ThemeProvider
import com.example.mypage.R

@Composable
fun MarketingAgreeScreen(
    navController: NavController
) {
    val cardShape = RoundedCornerShape(22.dp)
    var isChecked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.white)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                    .height(24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(10.dp)
                        .noRippleClickable { navController.popBackStack() }
                )

                Text(
                    text = "마케팅 수신 동의",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalFontTheme.current.font,
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(26.75.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(LocalColorTheme.current.gray[100])
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "링큐(LINK:U) 마케팅 수신동의",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.black
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = "(선택)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.gray[600]
                            )
                        }

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("""
                                    링큐(LINK:U)(이하 “회사”)는 서비스와 관련된 이벤트, 혜택, 신규 기능 안내 등 마케팅 정보를 제공하기 위하여 아래와 같이 개인정보를 수집·이용합니다.
                                    본 동의는 
                                """.trimIndent())
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("선택 사항")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("""
                                    이며, 이용자는 동의를 거부할 권리가 있습니다. 동의를 거부하더라도 서비스의 기본적인 이용에는 제한이 없습니다.
                                """.trimIndent())
                                }
                            }
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("""
                                    1. 수집 및 이용 항목
                                     · 이메일 주소
                                     · 앱 푸시 알림 토큰

                                    2. 이용 목적
                                     · 이벤트 및 프로모션 안내
                                     · 신규 서비스 및 기능 안내
                                     · 서비스 관련 혜택 및 업데이트 정보 제공

                                    3. 보유 및 이용 기간
                                    마케팅 정보 수신 동의 시점부터 다음 중 하나의 시점까지 보관 및 이용됩니다.
                                     · 이용자가 수신 동의를 철회한 경우
                                     · 회원 탈퇴 시

                                    4. 동의 거부 및 철회 안내
                                    이용자는 언제든지 마케팅 정보 수신 동의를 거부하거나 철회할 수 있습니다.
                                    동의 철회 방법은 다음과 같습니다.
                                     · 앱 내 
                                """.trimIndent())
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("설정 메뉴에서 마케팅 수신 동의 해제")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 14.sp,
                                        color = LocalColorTheme.current.black
                                    )
                                ) {
                                    append("""
                                    
                                     · 고객센터 이메일을 통한 요청
                                    동의 철회 시 이후부터는 마케팅 정보가 발송되지 않습니다.

                                    5. 발송 방법
                                    회사는 다음의 방법으로 마케팅 정보를 발송할 수 있습니다.
                                     · 이메일
                                     · 앱 푸시 알림
                                """.trimIndent())
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 11.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(22.dp))
                .graphicsLayer {
                    shadowElevation = 4.dp.toPx()
                    shape = cardShape
                    clip = true
                }
                .background(LocalColorTheme.current.white)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "마케팅 수신에 동의합니다.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = LocalColorTheme.current.black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(
                    if (isChecked) R.drawable.ic_checkbox_checked_purple
                    else R.drawable.ic_checkbox_unchecked
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .noRippleClickable { isChecked = !isChecked }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMarketingAgreeScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        MarketingAgreeScreen(navController = navController)
    }
}