package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.mypage.R
import com.linku.mypage.component.QuitReasonItem
import com.linku.mypage.component.ServiceQuitModal

private val quitReasons = listOf(
    "다른 유사 서비스를 이용해요.",
    "사용을 잘 안하게 돼요.",
    "잦은 오류와 장애가 발생해요.",
    "새 계정을 만들고 싶어요.",
    "기타"
)

@Composable
fun ServiceQuitScreen(
    navController: NavController,
    onDimmedChange: (Boolean) -> Unit = {},
    onRequestQuit: (reason: String) -> Unit
) {
    val colors = MaterialTheme.linkuColors

    var reasonText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // 탈퇴 확인 다이얼로그가 떠있는 동안 MainScreen 전체(하단 탭바 포함)를 딤 처리.
    LaunchedEffect(showDialog) { onDimmedChange(showDialog) }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var isAgreeChecked by remember { mutableStateOf(false) }

    val isEtcSelected = selectedReason == "기타"
    // "기타"는 직접 입력한 reasonText를, 그 외엔 선택한 사유 문구 자체를 실제 전송값으로 사용.
    val effectiveReason = if (isEtcSelected) reasonText else selectedReason.orEmpty()
    val isQuitEnabled = selectedReason != null && effectiveReason.isNotBlank() && isAgreeChecked

    val reasonFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // "기타" 선택 시 사유 입력창에 자동으로 커서(포커스) 활성화
    LaunchedEffect(isEtcSelected) {
        if (isEtcSelected) {
            reasonFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.white)
        ) {
            // 헤더는 고정 - 스크롤 영역 밖에 둠
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
                        .width(11.dp)
                        .noRippleClickable { navController.popBackStack() }
                )

                Text(
                    text = "회원 탈퇴",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(28.25.dp))

                Text(
                    text = "그동안 링큐를 이용해주셔서\n감사합니다.",
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "링큐를 이용하며 느끼신 불편함을 공유해주시면\n더욱 발전된 서비스를 제공할 수 있도록 노력하겠습니다.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[700],
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.gray[100])
                        .padding(horizontal = 22.dp, vertical = 13.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (reasonText.isEmpty()) {
                        Text(
                            text = "탈퇴 사유를 적어주세요.",
                            color = colors.gray[600],
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
                        modifier = Modifier.focusRequester(reasonFocusRequester),
                        textStyle = LocalTextStyle.current.copy(
                            color = colors.black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(46.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(colors.gray[100])
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
                            color = colors.black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "탈퇴 아이디는 복구와 재사용이 불가합니다.",
                                "삭제된 데이터는 복구되지 않습니다.",
                                "소셜 로그인 회원의 경우 서비스에서 관리하는 모든 정보가 삭제되며, 같은 소셜 아이디로 재가입 시 신규 회원으로 가입됩니다."
                            ).forEachIndexed { index, text ->
                                Row {
                                    Text(
                                        text = "${index + 1}.",
                                        fontSize = 15.sp,
                                        lineHeight = 24.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.gray[800]
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = text,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 15.sp,
                                        lineHeight = 24.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.gray[800]
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .noRippleClickable { isAgreeChecked = !isAgreeChecked },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "위 안내 사항을 확인했으며 이에 동의합니다.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[800]
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Image(
                            painter = painterResource(
                                if (isAgreeChecked) R.drawable.ic_checkbox_checked
                                else R.drawable.ic_checkbox_empty
                            ),
                            contentDescription = null,
                            modifier = Modifier.noRippleClickable {
                                isAgreeChecked = !isAgreeChecked
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(68.dp))
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
                        else Modifier.background(colors.gray[300])
                    )
                    .noRippleClickable(enabled = isQuitEnabled) { showDialog = true }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "탈퇴하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.white
                )
            }
        }

    }

    if (showDialog) {
        // Compose Dialog는 별도의 시스템 창(Window)에 그려지므로, 하단 탭바를 포함한 화면 전체를
        // 딤 처리할 수 있음(Scaffold content 영역 내부 Box로는 탭바 아래까지 못 가림).
        // 딤 자체는 MainScreen에서 onDimmedChange로 이미 켜져 있으므로, 이 다이얼로그는 그 위에
        // 배경 없이 외부 클릭만 막고 콘텐츠만 그림.
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            // 다이얼로그 기본 딤을 끔 - MainScreen의 딤을 그대로 통해서 보이게 함.
            SideEffect { dialogWindow?.setDimAmount(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable(enabled = false) {}, // 외부 클릭 막기
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
                            onRequestQuit(effectiveReason)
                        }
                    )
                }
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