package com.example.login.auth


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

/* ─────────────────────────────
   공통 풋터 버튼 (양식 통일용)
   ───────────────────────────── */
private val FOOTER_HEIGHT = 50.dp
private val FOOTER_BOTTOM = 0.dp
private val EXTRA_GAP = 0.dp

@Composable
private fun AgreeFooterButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "약관에 동의합니다",
    applyNavPadding: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (applyNavPadding) Modifier.navigationBarsPadding() else Modifier)
            .imePadding()
            .padding(start = 20.dp, end = 20.dp, bottom = FOOTER_BOTTOM)
            .height(FOOTER_HEIGHT)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled)
                        listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                    else
                        listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy
        )
    }
}

/* ─────────────────────────────
   개인정보 수집∙이용 동의
   ───────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyTermsScreen(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isAtBottom by remember { derivedStateOf { scrollState.value >= scrollState.maxValue } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "개인정보 처리방침",
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
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                )
            )
        },
        bottomBar = {
            // 마케팅과 동일한 풋터 버튼 배치
            AgreeFooterButton(
                enabled = isAtBottom,
                onClick = onAgreeClicked,
                applyNavPadding = true
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)               // 앱바/바텀바 inset
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp) // ⬅️ 마케팅과 동일 (좌우 20dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)                  // ⬅️ 마케팅과 동일 (내부 16dp)
            ) {
                Column {
                    // 제목 16sp + Medium (마케팅과 동일 타이틀 스타일)
                    Text(
                        text = "개인정보 수집 및 이용 동의서",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Paperlogy,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(4.dp))    // ⬅️ 제목-본문 간격 동일

                    Text(
                        text = """
1. 개인정보의 수집 및 이용 목적
링큐는 회원에게 원활한 서비스 제공을 위해 아래와 같은 목적의 개인정보를 수집 및 이용합니다.
• 서비스 회원가입 및 로그인
• 본인확인 및 회원정보 변경
• 저장한 링크 데이터의 백업 제공
• 서비스 이용 기록 분석 및 맞춤형 콘텐츠 제공
• 고객 문의 응대 및 공지사항 전달

2. 수집하는 개인정보 항목
""".trimIndent(),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )

                    Spacer(Modifier.height(12.dp))   // ⬅️ 본문 단락 간격 동일

                    Image(
                        painter = painterResource(id = R.drawable.img_personal_table),
                        contentDescription = "개인정보 수집 항목 표",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp) // ⬅️ 이미지 상하 8dp
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(Modifier.height(12.dp))   // ⬅️ 다음 본문과의 간격

                    Text(
                        text = """
※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.

3. 개인정보의 보유 및 이용 기간
회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
 • 회원 탈퇴 시 즉시 삭제
 • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)

4. 개인정보의 제3자 제공 
회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.

5. 개인정보 처리 위탁 
회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.

6. 개인정보 보호를 위한 권리 
회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.

7. 동의 거부 권리 및 불이익 안내 
회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.

본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.

[부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
""".trimIndent(),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )
                }
            }

            // 풋터와 겹침 방지 (마케팅과 동일 계산식)
            Spacer(Modifier.height(FOOTER_HEIGHT + FOOTER_BOTTOM + EXTRA_GAP))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PrivacyTermsScreenPreview() {
    MaterialTheme {
        PrivacyTermsScreen(onAgreeClicked = {}, onBackClicked = {})
    }
}

//
///* ─────────────────────────────
//   공통 풋터 버튼 (양식 통일용)
//   ───────────────────────────── */
//@Composable
//private fun AgreeFooterButton(
//    enabled: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    text: String = "약관에 동의합니다"
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .navigationBarsPadding()
//            .imePadding()
//            .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
//            .height(50.dp)
//            .background(
//                brush = Brush.horizontalGradient(
//                    colors = if (enabled)
//                        listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                    else
//                        listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
//                ),
//                shape = RoundedCornerShape(12.dp)
//            )
//            .clickable(enabled = enabled, onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            color = if (enabled) Color.White else Color.Gray,
//            fontWeight = FontWeight.Bold,
//            fontFamily = Paperlogy
//        )
//    }
//}
//
///* ─────────────────────────────
//   개인정보 수집∙이용 동의
//   ───────────────────────────── */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PrivacyTermsScreen(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember { derivedStateOf { scrollState.value >= scrollState.maxValue } }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(top = 56.dp + 24.dp, bottom = 100.dp)
//                .padding(horizontal = 20.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Column {
//                    Text(
//                        text = """
//개인정보 수집 및 이용 동의서
//
//1. 개인정보의 수집 및 이용 목적
//링큐는 회원에게 원활한 서비스 제공을 위해 아래와 같은 목적의 개인정보를 수집 및 이용합니다.
//• 서비스 회원가입 및 로그인
//• 본인확인 및 회원정보 변경
//• 저장한 링크 데이터의 백업 제공
//• 서비스 이용 기록 분석 및 맞춤형 콘텐츠 제공
//• 고객 문의 응대 및 공지사항 전달
//
//2. 수집하는 개인정보 항목
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_personal_table),
//                        contentDescription = "개인정보 수집 항목 표",
//                        contentScale = ContentScale.FillWidth,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        text = """
//※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.
//
//3. 개인정보의 보유 및 이용 기간
//회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
// • 회원 탈퇴 시 즉시 삭제
// • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)
//
//4. 개인정보의 제3자 제공
//회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.
//
//5. 개인정보 처리 위탁
//회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.
//
//6. 개인정보 보호를 위한 권리
//회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.
//
//7. 동의 거부 권리 및 불이익 안내
//회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.
//
//본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.
//
//[부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//                }
//            }
//        }
//
//        CenterAlignedTopAppBar(
//            title = {
//                Text(
//                    text = "개인정보 처리방침",
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
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
//            modifier = Modifier.align(Alignment.TopStart),
//            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                containerColor = Color.Transparent,
//                scrolledContainerColor = Color.Transparent
//            )
//        )
//
//        /* ✅ 공통 풋터 사용 (양식 동일) */
//        AgreeFooterButton(
//            enabled = isAtBottom,
//            onClick = onAgreeClicked,
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PrivacyTermsScreenPreview() {
//    MaterialTheme {
//        PrivacyTermsScreen(onAgreeClicked = {}, onBackClicked = {})
//    }
//}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PrivacyTermsScreen(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 스크롤 상태 추적
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember {
//        derivedStateOf { scrollState.value >= scrollState.maxValue }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 스크롤 가능한 약관 본문
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(top = 56.dp + 24.dp, bottom = 100.dp)
//                .padding(horizontal = 20.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Column {
//                    Text(
//                        text = """
//개인정보 수집 및 이용 동의서
//
//1. 개인정보의 수집 및 이용 목적
//링큐는 회원에게 원활한 서비스 제공을 위해 아래와 같은 목적의 개인정보를 수집 및 이용합니다.
//• 서비스 회원가입 및 로그인
//• 본인확인 및 회원정보 변경
//• 저장한 링크 데이터의 백업 제공
//• 서비스 이용 기록 분석 및 맞춤형 콘텐츠 제공
//• 고객 문의 응대 및 공지사항 전달
//
//2. 수집하는 개인정보 항목
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_personal_table),
//                        contentDescription = "개인정보 수집 항목 표",
//                        contentScale = ContentScale.FillWidth,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        text = """
//※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.
//
//3. 개인정보의 보유 및 이용 기간
//회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
// • 회원 탈퇴 시 즉시 삭제
// • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)
//
//4. 개인정보의 제3자 제공
//회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.
//
//5. 개인정보 처리 위탁
//회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.
//
//6. 개인정보 보호를 위한 권리
//회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.
//
//7. 동의 거부 권리 및 불이익 안내
//회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.
//
//본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.
//
//[부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//                }
//            }
//        }
//
//        // 상단 앱바
//        CenterAlignedTopAppBar(
//            title = {
//                Text(
//                    text = "개인정보 처리방침",
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
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
//        // 하단 버튼
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp)
//                .offset(y = 16.dp)
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
//fun PrivacyTermsScreenPreview() {
//    MaterialTheme {
//        PrivacyTermsScreen(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}

@Composable
fun PrivacyTermsScreenFixed(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    PrivacyTermsScreen(
        onAgreeClicked = onAgreeClicked,
        onBackClicked = onBackClicked
    )
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PrivacyTermsScreenFixed(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // ✅ 마케팅과 동일한 방식: by 위임으로 상태 추적 (자동 재컴포지션)
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember {
//        derivedStateOf { scrollState.value >= scrollState.maxValue }
//    }
//
//    // ✅ 마케팅과 동일한 레이아웃: Box 루트 + 앱바를 TopStart에 고정
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // ✅ 앱바: 마케팅과 동일 포지션/스타일
//        CenterAlignedTopAppBar(
//            title = {
//                Text(
//                    text = "개인정보 처리방침",
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
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
//        // ✅ 스크롤 본문: 패딩(top=56, bottom=100, horizontal=20) 완전 동일
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(top = 56.dp, bottom = 100.dp)
//                .padding(horizontal = 20.dp)
//        ) {
//            // ✅ 내부 컨테이너 패딩 16dp로 통일 (마케팅과 동일)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp) // ⬅️ 20dp → 16dp로 변경
//            ) {
//                Column {
//                    Text(
//                        text = """
//                            개인정보 수집 및 이용 동의서
//
//                            1. 개인정보의 수집 및 이용 목적
//                            링큐는 회원에게 원활한 서비스 제공을 위해 아래와 같은 목적의 개인정보를 수집 및 이용합니다.
//                            • 서비스 회원가입 및 로그인
//                            • 본인확인 및 회원정보 변경
//                            • 저장한 링크 데이터의 백업 제공
//                            • 서비스 이용 기록 분석 및 맞춤형 콘텐츠 제공
//                            • 고객 문의 응대 및 공지사항 전달
//
//                            2. 수집하는 개인정보 항목
//                        """.trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_personal_table),
//                        contentDescription = "개인정보 수집 항목 표",
//                        contentScale = ContentScale.FillWidth,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        text = """
//                            ※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.
//
//                            3. 개인정보의 보유 및 이용 기간
//                            회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
//                             • 회원 탈퇴 시 즉시 삭제
//                             • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)
//
//                            4. 개인정보의 제3자 제공 회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.
//
//                            5. 개인정보 처리 위탁 회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.
//
//                            6. 개인정보 보호를 위한 권리 회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.
//
//                            7. 동의 거부 권리 및 불이익 안내 회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.
//
//                             본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.
//
//                             [부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
//                        """.trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//                }
//            }
//        }
//
//        // ✅ 하단 버튼: 패딩/오프셋/높이/라운드/그라데이션 모두 마케팅과 동일
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
//fun PrivacyTermsScreenFixedPreview() {
//    MaterialTheme {
//        PrivacyTermsScreenFixed(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PrivacyTermsScreenFixed(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 스크롤 상태를 추적하는 변수
//    val scrollState = rememberScrollState()
//
//    // 스크롤이 끝까지 내려갔는지를 추적하는 상태
//    val isAtBottom = remember {
//        derivedStateOf {
//            scrollState.value >= scrollState.maxValue
//        }
//    }.value
//
//    // 전체 화면을 Box로 감싸고 상단 앱바, 내용, 버튼을 각각 배치
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 스크롤 가능한 약관 내용
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
//                    //.border(1.dp, Color(0xFF3399FF), RoundedCornerShape(8.dp))
//                    .padding(20.dp)
//            ) {
//                Column {
//                    Text(
//                        text = """
//                            개인정보 수집 및 이용 동의서
//
//                            1. 개인정보의 수집 및 이용 목적
//                            링큐는 회원에게 원활한 서비스 제공을 위해 아래와 같은 목적의 개인정보를 수집 및 이용합니다.
//                            • 서비스 회원가입 및 로그인
//                            • 본인확인 및 회원정보 변경
//                            • 저장한 링크 데이터의 백업 제공
//                            • 서비스 이용 기록 분석 및 맞춤형 콘텐츠 제공
//                            • 고객 문의 응대 및 공지사항 전달
//
//                            2. 수집하는 개인정보 항목
//                        """.trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_personal_table),
//                        contentDescription = "개인정보 수집 항목 표",
//                        contentScale = ContentScale.FillWidth,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        text = """
//                            ※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.
//
//                            3. 개인정보의 보유 및 이용 기간
//                            회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
//                             • 회원 탈퇴 시 즉시 삭제
//                             • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)
//
//                            4. 개인정보의 제3자 제공 회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.
//
//                            5. 개인정보 처리 위탁 회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.
//
//                            6. 개인정보 보호를 위한 권리 회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.
//
//                            7. 동의 거부 권리 및 불이익 안내 회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.
//
//                             본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.
//
//                             [부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
//                        """.trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//                }
//            }
//        }
//
//        // 상단 앱바 고정
//        CenterAlignedTopAppBar(
//            title = {
//                Text(
//                    text = "개인정보 처리방침",
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                )
//            },
//            navigationIcon = {
//                IconButton(onClick = onBackClicked) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_back), // ⬅️ 커스텀 아이콘
//                        contentDescription = "뒤로가기",
//                        modifier = Modifier.size(16.dp) // ⬅️ Material 기본 ArrowBack 과 동일 사이즈
//                    )
//                }
//            },
//            modifier = Modifier.align(Alignment.TopStart)
//        )
//
//        // 하단 고정 버튼 (PrivacyTermsScreenFixed)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp) // 마케팅과 동일
//                .offset(y = 16.dp) // ⬅️ 더 아래로 내림 (기기 따라 12~18dp 사이 미세조정 가능)
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
//fun PrivacyTermsScreenFixedPreview() {
//    MaterialTheme {
//        PrivacyTermsScreenFixed(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}
//
//
