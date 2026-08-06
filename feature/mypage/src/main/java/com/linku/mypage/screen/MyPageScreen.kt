package com.linku.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.linku.core.model.auth.LoginType
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.linkuColors
import com.linku.mypage.component.LogoutModal
import com.linku.mypage.ui.top.bar.MypageTopBar

@Composable
fun MyPageScreen(
    nickname: String,
    email: String,
    isUnreadAlarmExists: Boolean,
    myLinku: Long,
    myFolder: Long,
    myAiLinku: Long,
    loginType: LoginType,
    onNavigateAccount: () -> Unit,
    onNavigateAlarm: () -> Unit,
    onNavigateAlarmSetting: () -> Unit,
    onNavigateQuit: () -> Unit,
    onNavigateFAQ: () -> Unit,
    onNavigateNotice: () -> Unit,
    onNavigateTerms: () -> Unit,
    onNavigateAISummary: () -> Unit,
    onRequestLogout: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.gray[100])
    ) {
        MypageTopBar(
            isNoticeExist = isUnreadAlarmExists,
            nickname = nickname,
            email = email,
            myLinku = myLinku,
            myFolder = myFolder,
            myAiLinku = myAiLinku,
            socialLoginType = loginType.name.lowercase(),
            onAlarmClick = { onNavigateAlarm() },
            onAISummaryClick = { onNavigateAISummary() },
            modifier = Modifier.zIndex(1f)
        )

        // 설정
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.white)
                        .padding(start = 25.dp, top = 24.dp, end = 25.dp, bottom = 21.dp)
                ) {
                    Text(
                        text = "서비스 설정",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.gray[500]
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "계정 설정",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateAccount() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "알림 설정",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateAlarmSetting() }
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.white)
                        .padding(start = 25.dp, top = 24.dp, end = 25.dp, bottom = 21.dp)
                ) {
                    Text(
                        text = "고객 센터",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.gray[500]
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "FAQ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateFAQ() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "공지사항",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateNotice() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "서비스 약관",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateTerms() }
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.white)
                        .padding(start = 25.dp, top = 24.dp, end = 25.dp, bottom = 21.dp)
                ) {
                    Text(
                        text = "기타",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.gray[500]
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "회원 탈퇴",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { onNavigateQuit() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "로그아웃",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = LocalFontTheme.current.font,
                        color = colors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                            .noRippleClickable { showLogoutDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showLogoutDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .zIndex(1f)
                .noRippleClickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                LogoutModal(
                    onDismiss = { showLogoutDialog = false },
                    onConfirm = {
                        showLogoutDialog = false
                        onRequestLogout()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyPageScreen() {
    MyPageScreen(
        nickname = "세나",
        email = "linkU2025@gmail.com",
        isUnreadAlarmExists = true,
        myLinku = 63,
        myFolder = 5,
        myAiLinku = 48,
        loginType = LoginType.KAKAO,
        onNavigateAccount = {},
        onNavigateAlarm = {},
        onNavigateAlarmSetting = {},
        onNavigateQuit = {},
        onNavigateFAQ = {},
        onNavigateNotice = {},
        onNavigateTerms = {},
        onRequestLogout = {},
        onNavigateAISummary = {}
    )
}