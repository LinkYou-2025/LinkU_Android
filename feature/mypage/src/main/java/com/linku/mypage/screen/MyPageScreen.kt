package com.linku.mypage.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.mypage.R
import com.linku.mypage.component.LogoutModal
import com.linku.mypage.component.ServiceQuitModal
import com.linku.design.R as Res

@Composable
fun MyPageScreen(
    navController: NavController,
    nickname: String,
    email: String,
    gender: String,   // "MALE" | "FEMALE"
    jobName: String,
    myLinku: Long,
    myFolder: Long,
    myAiLinku: Long,
    onNavigateAccount: () -> Unit = {},
    onNavigateAlarm: () -> Unit = {},
    onNavigateQuit: () -> Unit = {},
    onRequestLogout: () -> Unit = {}
) {
    val TopBarHeightExpanded = 319.dp // InfoCard 보일 때 TopBar 전체 높이
    val TopBarHeightCollapsed = 235.dp // InfoCard 숨겨질 때 남기는 TopBar 높이

    // 스크롤 상태 추적!
    val listState = rememberLazyListState()
    val infoCardVisible = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 60

    val topPadding = if (infoCardVisible) TopBarHeightExpanded else TopBarHeightCollapsed

//    var showQuitDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        TopBar(
            infoCardVisible = infoCardVisible,
            nickname = nickname,
            email = email,
            gender = gender,
            jobName = jobName,
            myLinku = myLinku,
            myFolder = myFolder,
            myAiLinku = myAiLinku
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.gray[100])
                .padding(top = topPadding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalColorTheme.current.gray[100])
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // 설정
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "설정",
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
                            color = LocalColorTheme.current.black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[500]
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.clickable { onNavigateAccount() }
                            ) {
                                Text(
                                    text = "계정 설정",
                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
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

                            Row(
                                modifier = Modifier.clickable { onNavigateAlarm() }
                            ) {
                                Text(
                                    text = "알림 설정",
                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
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
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[500]
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "1 : 1 문의",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[800],
                                modifier = Modifier.padding(start = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "공지사항",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
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
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[500]
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "회원탈퇴",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[800],
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clickable {
//                                        showQuitDialog = true
                                        onNavigateQuit()
                                    }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "로그아웃",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                                color = LocalColorTheme.current.gray[800],
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clickable { showLogoutDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(74.dp))
                    }
                }
            }
        }
    }


//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .background(LocalColorTheme.current.gray[100])
//        ) {
//            // 탑 바
//            TopBar(infoCardVisible)
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // 설정
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp)
//            ) {
//                Text(
//                    text = "설정",
//                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
//                    color = LocalColorTheme.current.black
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // 서비스 설정
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(15.dp))
//                        .background(LocalColorTheme.current.white)
//                        .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
//                ) {
//                    Text(
//                        text = "서비스 설정",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[500]
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Row(
//                        modifier = Modifier.clickable { onNavigateAccount() }
//                    ) {
//                        Text(
//                            text = "계정 설정",
//                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                            color = LocalColorTheme.current.gray[800],
//                            modifier = Modifier.padding(start = 6.dp)
//                        )
//
//                        Spacer(modifier = Modifier.weight(1f))
//
//                        Icon(
//                            painter = painterResource(id = Res.drawable.ic_detail),
//                            contentDescription = null,
//                            tint = LocalColorTheme.current.gray[500]
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Row(
//                        modifier = Modifier.clickable { onNavigateAlarm() }
//                    ) {
//                        Text(
//                            text = "알림 설정",
//                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                            color = LocalColorTheme.current.gray[800],
//                            modifier = Modifier.padding(start = 6.dp)
//                        )
//
//                        Spacer(modifier = Modifier.weight(1f))
//
//                        Icon(
//                            painter = painterResource(id = Res.drawable.ic_detail),
//                            contentDescription = null,
//                            tint = LocalColorTheme.current.gray[500]
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.padding(top = 15.dp))
//
//                // 고객 센터
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(15.dp))
//                        .background(LocalColorTheme.current.white)
//                        .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
//                ) {
//                    Text(
//                        text = "고객 센터",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[500]
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Text(
//                        text = "1 : 1 문의",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[800],
//                        modifier = Modifier.padding(start = 6.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Text(
//                        text = "공지사항",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[800],
//                        modifier = Modifier.padding(start = 6.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.padding(top = 15.dp))
//
//                // 기타
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(15.dp))
//                        .background(LocalColorTheme.current.white)
//                        .padding(start = 28.dp, top = 26.dp, end = 26.dp, bottom = 25.dp)
//                ) {
//                    Text(
//                        text = "기타",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[500]
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Text(
//                        text = "회원탈퇴",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[800],
//                        modifier = Modifier
//                            .padding(start = 6.dp)
//                            .clickable {
//                                showQuitDialog = true
//                                onNavigateQuit()
//                            }
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Text(
//                        text = "로그아웃",
//                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
//                        color = LocalColorTheme.current.gray[800],
//                        modifier = Modifier
//                            .padding(start = 6.dp)
//                            .clickable { showLogoutDialog = true }
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(74.dp))
//            }
//        }
//    }

//    if (showQuitDialog) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0x66000000)) // 40% 투명한 검정색 배경
//                .zIndex(1f)
//                .clickable(enabled = false) {}, // 외부 클릭 막기
//            contentAlignment = Alignment.Center
//        ) {
//            Box(
//                modifier = Modifier.padding(horizontal = 20.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                ServiceQuitModal(
//                    onDismiss = { showQuitDialog = false },
//                    onConfirm = {
//                        showQuitDialog = false
//                        // 실제 탈퇴 로직 호출
//                    }
//                )
//            }
//        }
//    }

    if (showLogoutDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)) // 40% 투명한 검정색 배경
                .zIndex(1f)
                .clickable(enabled = false) {}, // 외부 클릭 막기
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
                        // 실제 로그아웃 로직 호출
                        onRequestLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    infoCardVisible: Boolean,
    nickname: String,
    email: String,
    gender: String,
    jobName: String,
    myLinku: Long,
    myFolder: Long,
    myAiLinku: Long
) {
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
            .fillMaxWidth()
            .zIndex(1f)
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
                        text = nickname,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = email,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
                        color = LocalColorTheme.current.gray[400]
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (gender == "FEMALE") "여성" else "남성",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                            color = LocalColorTheme.current.purple[200]
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "·",
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                            color = LocalColorTheme.current.gray[400]
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = jobName,
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
                            color = LocalColorTheme.current.blue[200]
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        AnimatedVisibility(
            visible = infoCardVisible,
            enter = slideInVertically(
                initialOffsetY = { it } // 아래에서 위로 등장
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it } // 위로 사라짐
            ) + fadeOut()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                InfoCard(title = "나의 링크", count = myLinku.toString())
                Spacer(modifier = Modifier.width(8.dp))

                InfoCard(title = "나의 폴더", count = myFolder.toString())
                Spacer(modifier = Modifier.width(8.dp))

                InfoCard(title = "AI 요약 링크", count = myAiLinku.toString(), borderBrush = buttonBrush)
            }
        }
    }
}

@Composable
fun InfoCard(title: String, count: String, borderBrush: Brush? = null) {
    Box(
        modifier = Modifier
            .width(115.dp)
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
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = TextStyle(fontSize = 13.sp, fontFamily = LocalFontTheme.current.font),
                color = LocalColorTheme.current.gray[700]
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = count,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
                color = LocalColorTheme.current.gray[700]
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewMyPageScreen() {
//    val navController = rememberNavController()
//    MyPageScreen(navController = navController)
//}