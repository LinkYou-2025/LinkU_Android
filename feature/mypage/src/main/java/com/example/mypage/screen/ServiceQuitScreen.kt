package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.ThemeProvider
import com.example.design.theme.color.Basic
import com.example.mypage.R
import com.example.mypage.component.QuitReasonItem
import com.example.mypage.component.ServiceQuitModal

@Composable
fun ServiceQuitScreen(
    navController: NavController,
    onRequestQuit: (reason: String) -> Unit
) {
    var reasonText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var isAgreeChecked by remember { mutableStateOf(false) }

    val quitReasons = listOf(
        "다른 유사 서비스를 이용해요.",
        "사용을 잘 안하게 돼요.",
        "잦은 오류와 장애가 발생해요.",
        "새 계정을 만들고 싶어요.",
        "기타"
    )

    val isEtcSelected = selectedReason == "기타"
    val isQuitEnabled = selectedReason != null && isAgreeChecked

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColorTheme.current.white)
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 59.dp)
                            .height(24.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(10.dp)
                                .clickable { navController.popBackStack() }
                        )

                        Text(
                            text = "회원 탈퇴",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalColorTheme.current.black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.25.dp))

                    Text(
                        text = "그동안 링큐를 이용해주셔서\n감사합니다.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalColorTheme.current.black,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "링큐를 이용하며 느끼신 불편함을 공유해주시면\n더욱 발전된 서비스를 제공할 수 있도록 노력하겠습니다.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.gray[700],
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        quitReasons.forEach { reason ->
                            QuitReasonItem(
                                text = reason,
                                selected = selectedReason == reason,
                                onClick = {
                                    selectedReason = reason
                                    if (reason != "기타") {
                                        reasonText = ""
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LocalColorTheme.current.gray[100])
                            .padding(horizontal = 22.dp, vertical = 20.dp)
                    ) {
                        if (reasonText.isBlank()) {
                            Text(
                                text = "탈퇴 사유를 적어주세요.",
                                color = LocalColorTheme.current.gray[600],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        BasicTextField(
                            value = reasonText,
                            onValueChange = {
                                if (isEtcSelected) {
                                    reasonText = it
                                }
                            },
                            textStyle = TextStyle(
                                color = LocalColorTheme.current.black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(54.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .background(LocalColorTheme.current.gray[100])
                            .padding(horizontal = 21.dp, vertical = 16.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "탈퇴 안내 및 유의사항",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalColorTheme.current.black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = """
                                    1. 탈퇴 아이디는 복구와 재사용이 불가합니다.
                                    2. 삭제된 데이터는 복구되지 않습니다.
                                    3. 소셜 로그인 회원의 경우 서비스에서 관리하는 모든 정보가 삭제되며, 같은 소셜 아이디로 재가입시 신규 회원으로 가입됩니다.
                                """.trimIndent(),
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.gray[800]
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { isAgreeChecked = !isAgreeChecked },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "위 안내 사항을 확인했으며 이에 동의합니다.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.gray[800]
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Image(
                                painter = painterResource(
                                    if (isAgreeChecked) R.drawable.ic_checkbox_checked
                                    else R.drawable.ic_checkbox_empty
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable { isAgreeChecked = !isAgreeChecked }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(68.dp))
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
                    .clip(RoundedCornerShape(18.dp))
                    .then(
                        if (isQuitEnabled) Modifier.background(Basic.maincolor)
                        else Modifier.background(LocalColorTheme.current.gray[300])
                    )
                    .clickable(enabled = isQuitEnabled) { showDialog = true }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "탈퇴하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

    }

    if (showDialog) {
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
                ServiceQuitModal(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        showDialog = false
                        // 실제 탈퇴 로직 호출
                        onRequestQuit(reasonText)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewServiceQuitScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        ServiceQuitScreen(
            navController = navController,
            onRequestQuit = { }
        )
    }
}