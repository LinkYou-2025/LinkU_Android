package com.example.login.auth



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceTermsScreen(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isAtBottom by remember { derivedStateOf { scrollState.value >= scrollState.maxValue } }

    val serviceTermsContent = """
앱 서비스 이용약관

제 1 조 (목적)
본 약관은 링큐가 제공하는 링크 저장 및 맞춤형 큐레이션 서비스의 이용과 관련하여 회사(이하 Team. 링큐)와 이용자(이하 “회원”) 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

제 2 조 (약관의 효력 및 변경)
1. 본 약관은 서비스를 이용하고자 하는 모든 회원에게 적용됩니다.
2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 본 약관을 변경할 수 있으며, 변경된 약관은 적용 7일 전(중대하거나 사용자에게 불리한 변경의 경우 30일 전)부터 회원에게 공지됩니다.
3. 회원이 변경된 약관에 동의하지 않을 경우 서비스 이용을 중단할 수 있으며, 지속적인 이용 시 변경된 약관에 동의한 것으로 간주됩니다.

제 3 조 (서비스 이용 및 제한)
1. 회사는 회원에게 링크 저장 및 공유, 사용자 맞춤형 큐레이션 서비스 제공을 포함한 다양한 콘텐츠 및 기능을 제공합니다.
2. 회사는 서비스의 운영상 필요에 따라 서비스의 일부 또는 전부를 변경, 중단할 수 있습니다.
3. 회원은 서비스 이용 시 관련 법령 및 본 약관을 준수해야하며, 타인의 권리를 침해하거나 공공질서를 해치는 행위를 해서는 안 됩니다.

제 4 조 (회원가입 및 개인정보)
1. 회원가입은 이메일 로그인을 통해 진행되며, 회원은 정확한 정보를 제공해야 합니다.
2. 회원 계정의 관리 책임은 회원 본인에게 있으며, 제3자에게 계정을 양도 또는 공유할 수 없습니다.
3. 회사는 회원이 허위 정보를 제공하거나, 타인의 정보를 도용하는 경우 계정을 제한하거나 삭제할 수 있습니다.

제 5 조 (개인정보 보호)
1. 회사는 관련 법령에 따라 회원의 개인정보를 보호하며, 개인정보의 수집 및 이용에 대한 사항은 개인정보 처리방침에 따릅니다.
2. 회사는 회원의 동의없이 개인정보를 제3자에게 제공하지 않으며, 서비스 운영을 위해 필요한 경우에만 최소한의 정보를 이용합니다.

제 6 조 (이용 제한 및 탈퇴)
1. 회원이 본 약관을 위반하거나 서비스 운영에 지장을 초래하는 경우, 회사는 사전 통지없이 회원의 서비스 이용을 제한하거나 계약을 해지할 수 있습니다.
2. 회원은 언제든지 서비스 이용을 중단하고 계정을 삭제할 수 있습니다.

제 7 조 (기타)
1. 회사는 천재지변, 기술적 장애 등 불가항력적인 사유로 서비스 제공이 불가능한 경우 이에 대한 책임을 지지 않습니다.
2. 회원이 본인의 부주의로 인해 발생한 손해에 대해 회사는 책임을 지지 않습니다.

제 8 조 (준거법 및 관할 법원)
본 약관과 관련된 분쟁은 대한민국 법을 준거법으로 하며, 관할 법원은 회사의 본사 소재지를 관할하는 법원으로 합니다.

[부칙] 본 약관은 2025년 08월 22일부터 시행됩니다.
""".trimIndent()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "서비스 이용약관",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Paperlogy,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ⬇️ 앱바 아래 12dp, 좌우 20dp, 하단 버튼 여유 120dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 12.dp, bottom = 120.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = serviceTermsContent,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontFamily = Paperlogy
                )
            }

            // 하단 고정 버튼 (마케팅 동일)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isAtBottom)
                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else
                                listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = isAtBottom, onClick = onAgreeClicked),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "약관에 동의합니다",
                    color = if (isAtBottom) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Paperlogy
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceTermsScreenPreview() {
    MaterialTheme {
        ServiceTermsScreen(onAgreeClicked = {}, onBackClicked = {})
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ServiceTermsScreen(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember {
//        derivedStateOf { scrollState.value >= scrollState.maxValue }
//    }
//
//    val serviceTermsContent = """
//        앱 서비스 이용약관
//
//        제 1 조 (목적)
//        본 약관은 링큐가 제공하는 링크 저장 및 맞춤형 큐레이션 서비스의 이용과 관련하여 회사(이하 Team. 링큐)와 이용자(이하 “회원”) 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
//
//        제 2 조 (약관의 효력 및 변경)
//        1. 본 약관은 서비스를 이용하고자 하는 모든 회원에게 적용됩니다.
//        2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 본 약관을 변경할 수 있으며, 변경된 약관은 적용 7일 전(중대하거나 사용자에게 불리한 변경의 경우 30일 전)부터 회원에게 공지됩니다.
//        3. 회원이 변경된 약관에 동의하지 않을 경우 서비스 이용을 중단할 수 있으며, 지속적인 이용 시 변경된 약관에 동의한 것으로 간주됩니다.
//
//        제 3 조 (서비스 이용 및 제한)
//        1. 회사는 회원에게 링크 저장 및 공유, 사용자 맞춤형 큐레이션 서비스 제공을 포함한 다양한 콘텐츠 및 기능을 제공합니다.
//        2. 회사는 서비스의 운영상 필요에 따라 서비스의 일부 또는 전부를 변경, 중단할 수 있습니다.
//        3. 회원은 서비스 이용 시 관련 법령 및 본 약관을 준수해야하며, 타인의 권리를 침해하거나 공공질서를 해치는 행위를 해서는 안 됩니다.
//
//        제 4 조 (회원가입 및 개인정보)
//        1. 회원가입은 이메일 로그인을 통해 진행되며, 회원은 정확한 정보를 제공해야 합니다.
//        2. 회원 계정의 관리 책임은 회원 본인에게 있으며, 제3자에게 계정을 양도 또는 공유할 수 없습니다.
//        3. 회사는 회원이 허위 정보를 제공하거나, 타인의 정보를 도용하는 경우 계정을 제한하거나 삭제할 수 있습니다.
//
//        제 5 조 (이용 요금)
//        1. 회사는 관련 법령에 따라 회원의 개인정보를 보호하며, 개인정보의 수집 및 이용에 대한 사항은 개인정보 처리방침에 따릅니다.
//        2. 회사는 회원의 동의없이 개인정보를 제3자에게 제공하지 않으며, 서비스 운영을 위해 필요한 경우에만 최소한의 정보를 이용합니다.
//
//        제 6 조 (면책 조항)
//        1. 회원이 본 약관을 위반하거나 서비스 운영에 지장을 초래하는 경우, 회사는 사전 통지없이 회원의 서비스 이용을 제한하거나 계약을 해지할 수 있습니다.
//        2. 회원은 언제든지 서비스 이용을 중단하고 계정을 삭제할 수 있습니다.
//
//        제 7 조 (기타)
//        1. 회사는 천재지변, 기술적 장애 등 불가항력적인 사유로 서비스 제공이 불가능한 경우 이에 대한 책임을 지지 않습니다.
//        2. 회원이 본인의 부주의로 인해 발생한 손해에 대해 회사는 책임을 지지 않습니다.
//
//        제 8조 (준거법 및 관할 법원)
//        본 약관과 관련된 분쟁은 대한민국 법을 준거법으로 하며, 관할 법원은 회사의 본사 소재지를 관할하는 법원으로 합니다.
//
//        [부칙] 본 약관은 2025년 08월 22일부터 시행됩니다.
//    """.trimIndent()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 상단 앱바 (마케팅 동일)
//        CenterAlignedTopAppBar(
//            title = {
//                Text(
//                    text = "서비스 이용약관",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = Paperlogy
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = onBackClicked) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_back),
//                        contentDescription = "뒤로가기",
//                        modifier = Modifier.size(16.dp)
//                    )
//                }
//            },
//            modifier = Modifier.align(Alignment.TopStart)
//        )
//
//        // 스크롤 본문 (마케팅 동일 패딩)
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(top = 56.dp, bottom = 100.dp)
//                .padding(horizontal = 20.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp) // 내부 여백은 16dp로 통일
//            ) {
//                Text(
//                    text = serviceTermsContent,
//                    fontSize = 14.sp,
//                    lineHeight = 22.sp,
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Normal
//                )
//            }
//        }
//
//        // 하단 고정 버튼 (마케팅과 완전 동일)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp)
//                .offset(y = -16.dp) // 기기별 12~18dp 미세조정 가능
//                .height(50.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (isAtBottom)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
//                    ),
//                    shape = RoundedCornerShape(12.dp)
//                )
//                .clickable(enabled = isAtBottom, onClick = onAgreeClicked),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "약관에 동의합니다",
//                color = if (isAtBottom) Color.White else Color.Gray,
//                fontWeight = FontWeight.Bold,
//                fontFamily = Paperlogy
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ServiceTermsScreenPreview() {
//    MaterialTheme {
//        ServiceTermsScreen(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ServiceTermsScreen(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 스크롤 상태를 추적하는 변수
//    val scrollState = rememberScrollState()
//
//    // 스크롤이 끝까지 내려갔는지를 추적하는 상태
//    val isAtBottom by remember {
//        derivedStateOf {
//            scrollState.value >= scrollState.maxValue
//        }
//    }
//
//    // 약관 본문 텍스트
//    val serviceTermsContent = """
//        앱 서비스 이용약관
//
//        제 1 조 (목적)
//        본 약관은 링큐가 제공하는 링크 저장 및 맞춤형 큐레이션 서비스의 이용과 관련하여 회사(이하 Team. 링큐)와 이용자(이하 “회원”) 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
//
//        제 2 조 (약관의 효력 및 변경)
//        1. 본 약관은 서비스를 이용하고자 하는 모든 회원에게 적용됩니다.
//        2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 본 약관을 변경할 수 있으며, 변경된 약관은 적용 7일 전(중대하거나 사용자에게 불리한 변경의 경우 30일 전)부터 회원에게 공지됩니다.
//        3. 회원이 변경된 약관에 동의하지 않을 경우 서비스 이용을 중단할 수 있으며, 지속적인 이용 시 변경된 약관에 동의한 것으로 간주됩니다.
//
//        제 3 조 (서비스 이용 및 제한)
//        1. 회사는 회원에게 링크 저장 및 공유, 사용자 맞춤형 큐레이션 서비스 제공을 포함한 다양한 콘텐츠 및 기능을 제공합니다.
//        2. 회사는 서비스의 운영상 필요에 따라 서비스의 일부 또는 전부를 변경, 중단할 수 있습니다.
//        3. 회원은 서비스 이용 시 관련 법령 및 본 약관을 준수해야하며, 타인의 권리를 침해하거나 공공질서를 해치는 행위를 해서는 안 됩니다.
//
//        제 4 조 (회원가입 및 개인정보)
//        1. 회원가입은 이메일 로그인을 통해 진행되며, 회원은 정확한 정보를 제공해야 합니다.
//        2. 회원 계정의 관리 책임은 회원 본인에게 있으며, 제3자에게 계정을 양도 또는 공유할 수 없습니다.
//        3. 회사는 회원이 허위 정보를 제공하거나, 타인의 정보를 도용하는 경우 계정을 제한하거나 삭제할 수 있습니다.
//
//        제 5 조 (이용 요금)
//        1. 회사는 관련 법령에 따라 회원의 개인정보를 보호하며, 개인정보의 수집 및 이용에 대한 사항은 개인정보 처리방침에 따릅니다.
//        2. 회사는 회원의 동의없이 개인정보를 제3자에게 제공하지 않으며, 서비스 운영을 위해 필요한 경우에만 최소한의 정보를 이용합니다.
//
//        제 6 조 (면책 조항)
//        1. 회원이 본 약관을 위반하거나 서비스 운영에 지장을 초래하는 경우, 회사는 사전 통지없이 회원의 서비스 이용을 제한하거나 계약을 해지할 수 있습니다.
//        2. 회원은 언제든지 서비스 이용을 중단하고 계정을 삭제할 수 있습니다.
//
//        제 7 조 (기타)
//        1. 회사는 천재지변, 기술적 장애 등 불가항력적인 사유로 서비스 제공이 불가능한 경우 이에 대한 책임을 지지 않습니다.
//        2. 회원이 본인의 부주의로 인해 발생한 손해에 대해 회사는 책임을 지지 않습니다.
//
//        제 8조 (준거법 및 관할 법원)
//        본 약관과 관련된 분쟁은 대한민국 법을 준거법으로 하며, 관할 법원은 회사의 본사 소재지를 관할하는 법원으로 합니다.
//
//        [부칙] 본 약관은 2025년 08월 22일부터 시행됩니다.
//    """.trimIndent()
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "서비스 이용약관",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium,
//                        fontFamily = Paperlogy
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClicked) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_back),
//                            contentDescription = "뒤로가기",
//                            modifier = Modifier.size(16.dp)
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding) // ✅ 앱바 높이를 자동 반영
//        ) {
//            // 스크롤 본문
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(scrollState)
//                    .padding(horizontal = 20.dp)
//                    .padding(bottom = 100.dp) // ✅ 버튼 영역만큼 바텀 여백
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(20.dp)
//                ) {
//                    Text(
//                        text = serviceTermsContent,
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.Normal
//                    )
//                }
//            }
//
//            // 하단 고정 버튼 (PrivacyTermsScreenFixed)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp) // 마케팅과 동일
//                    .offset(y = 16.dp) // ⬅️ 더 아래로 내림 (기기 따라 12~18dp 사이 미세조정 가능)
//                    .height(50.dp)
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = if (isAtBottom)
//                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            else
//                                listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
//                        ),
//                        shape = RoundedCornerShape(12.dp)
//                    )
//                    .clickable(enabled = isAtBottom, onClick = onAgreeClicked),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "약관에 동의합니다",
//                    color = if (isAtBottom) Color.White else Color.Gray,
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = Paperlogy
//                )
//            }
//        }
//    }
//}
//
////    // 전체 화면을 Box로 감싸고 상단 앱바, 내용, 버튼을 각각 배치
////    Box(modifier = Modifier.fillMaxSize()) {
////
////        // 스크롤 가능한 약관 내용
////        Column(
////            modifier = Modifier
////                .fillMaxSize()
////                .verticalScroll(scrollState)
////                //.padding(top = 56.dp, bottom = 100.dp) // 앱바 높이와 버튼 높이에 대한 여백 확보
////                .windowInsetsPadding(WindowInsets.statusBars)
////                .padding(top = 56.dp, bottom = 100.dp)
////                .padding(horizontal = 16.dp)
////        ) {
////            Box(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    //.border(1.dp, Color(0xFF3399FF), RoundedCornerShape(8.dp))
////                    .padding(16.dp)
////            ) {
////                Text(
////                    text = serviceTermsContent,
////                    fontSize = 14.sp,
////                    lineHeight = 22.sp,
////                    fontFamily = Paperlogy,
////                    fontWeight = FontWeight.Normal
////                )
////            }
////        }
////
////        // 상단 앱바
////        CenterAlignedTopAppBar(
////            title = {
////                Text(
////                    text = "서비스 이용약관",
////                    fontSize = 16.sp,
////                    fontWeight = FontWeight.Medium,
////                    fontFamily = Paperlogy
////                )
////            },
//////            navigationIcon = {
//////                IconButton(onClick = onBackClicked) {
//////                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
//////                }
//////            },
////            navigationIcon = {
////                IconButton(onClick = onBackClicked) {
////                    Icon(
////                        painter = painterResource(id = R.drawable.ic_back), // ⬅️ 커스텀 아이콘
////                        contentDescription = "뒤로가기",
////                        modifier = Modifier.size(16.dp) // ⬅️ Material 기본 ArrowBack 과 동일 사이즈
////                    )
////                }
////            },
////            modifier = Modifier
////                .align(Alignment.TopStart)
////                .statusBarsPadding()
////        )
////
////        // 하단 고정 버튼
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()
////                .align(Alignment.BottomCenter)
////                .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
////                //.padding(horizontal = 16.dp, vertical = 26.dp)
////                .height(50.dp)
////                .background(
////                    brush = Brush.horizontalGradient(
////                        colors = if (isAtBottom)
////                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
////                        else
////                            listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
////                    ),
////                    shape = RoundedCornerShape(12.dp)
////                )
////                .clickable(enabled = isAtBottom, onClick = onAgreeClicked),
////            contentAlignment = Alignment.Center
////        ) {
////            Text(
////                text = "약관에 동의합니다",
////                color = if (isAtBottom) Color.White else Color.Gray,
////                fontWeight = FontWeight.Bold,
////                fontFamily = Paperlogy
////            )
////        }
////    }
////}
//
//
//@Preview(showBackground = true)
//@Composable
//fun ServiceTermsScreenPreview() {
//    MaterialTheme {
//        ServiceTermsScreen(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}