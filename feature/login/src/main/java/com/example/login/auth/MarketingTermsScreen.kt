package com.example.login.auth

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/* ─────────────────────────────
   공통 풋터 버튼 (양식 통일용)
   ───────────────────────────── */
private val FOOTER_HEIGHT = 50.dp
private val FOOTER_BOTTOM = 0.dp   // AgreeFooterButton 내부 .padding(..., bottom = 30.dp)
private val EXTRA_GAP = 0.dp       // 버튼 바로 위에 살짝 여유


@Composable
private fun AgreeFooterButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "약관에 동의합니다",
    applyNavPadding: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
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
   마케팅 수신 동의
   ───────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingTermsScreenComposable(
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
                        text = "마케팅 수신 동의",
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
                    containerColor = Color.White,          // 필요 시 투명 대신 흰색
                    scrolledContainerColor = Color.White
                )
            )
        },
        bottomBar = {
            // ✅ Scaffold가 안전영역을 이미 처리하므로 navPadding은 끄기
            AgreeFooterButton(
                enabled = isAtBottom,
                onClick = onAgreeClicked,
                applyNavPadding = false
            )
        },
        containerColor = Color.White,
        // (선택) Scaffold가 주는 기본 content inset을 그대로 사용
        // contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)            // ✅ 앱바/풋터 영역 피해서 배치
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp)
                //.padding(horizontal = 20.dp, top = 24.dp) // 앱바 바로 아래 여백
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(8.dp)) // ✅ 배경색 추가
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "마케팅 수신 동의서",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Paperlogy,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = """
링큐(LINK:U)는 이용자에게 맞춤혜택 및 유용한 정보를 전달하기 위해 아래와 같이 마케팅 목적의 개인정보 수집·이용 및 광고성 정보 수신에 대한 동의를 요청드립니다.

1. 수집 및 이용 목적
• 이벤트, 혜택, 프로모션 등 마케팅 정보 제공
• 앱 기능 안내 및 맞춤형 콘텐츠 추천
• 설문조사 및 사용자 참여 프로그램 안내

2. 수집항목
""".trimIndent(),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )

                    Spacer(Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_marketing_table),
                        contentDescription = "개인정보 수집 항목 표",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = """
※ 본 항목은 마케팅 정보 제공을 위해 별도로 수집되며, 동의하지 않아도 서비스 이용에는 제한이 없습니다.

3. 보유 및 이용 기간
• 동의 철회 또는 회원 탈퇴 시까지
• 단, 관련 법령에 따른 보존 의무가 있는 경우 해당기간까지 보관

4. 전송 방법
• 이메일, 앱 푸시 알림, 인앱 메시지 등

5. 동의 거부 권리 및 불이익
• 본 동의는 선택 사항이며, 동의하지 않더라도 서비스 이용에 제한은 없습니다.
""".trimIndent(),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )
                }
            }

            // ✅ 맨 아래 여유를 명시적으로 추가(풋터와 겹침 방지)
            Spacer(Modifier.height(FOOTER_HEIGHT + FOOTER_BOTTOM + EXTRA_GAP))
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MarketingTermsScreenComposable(
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
//                // ⬇️ 버튼(50) + 버튼 하단(30) + 여유(16) 만큼 확보
//                .padding(bottom = FOOTER_HEIGHT + FOOTER_BOTTOM + EXTRA_GAP),
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Column {
//                    Text(
//                        text = "마케팅 수신 동의서",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium,
//                        fontFamily = Paperlogy,
//                        color = Color.Black
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = """
//링큐(LINK:U)는 이용자에게 맞춤혜택 및 유용한 정보를 전달하기 위해 아래와 같이 마케팅 목적의 개인정보 수집·이용 및 광고성 정보 수신에 대한 동의를 요청드립니다.
//
//1. 수집 및 이용 목적
//• 이벤트, 혜택, 프로모션 등 마케팅 정보 제공
//• 앱 기능 안내 및 맞춤형 콘텐츠 추천
//• 설문조사 및 사용자 참여 프로그램 안내
//
//2. 수집항목
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_marketing_table),
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
//※ 본 항목은 마케팅 정보 제공을 위해 별도로 수집되며, 동의하지 않아도 서비스 이용에는 제한이 없습니다.
//
//3. 보유 및 이용 기간
//• 동의 철회 또는 회원 탈퇴 시까지
//• 단, 관련 법령에 따른 보존 의무가 있는 경우 해당기간까지 보관
//
//4. 전송 방법
//• 이메일, 앱 푸시 알림, 인앱 메시지 등
//
//5. 동의 거부 권리 및 불이익
//• 본 동의는 선택 사항이며, 동의하지 않더라도 서비스 이용에 제한은 없습니다.
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
//                    text = "마케팅 수신 동의",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = Paperlogy,
//                    modifier = Modifier.padding(horizontal = 20.dp)
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

@Preview(showBackground = true)
@Composable
fun MarketingTermsPreview() {
    MaterialTheme {
        MarketingTermsScreenComposable(onAgreeClicked = {}, onBackClicked = {})
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MarketingTermsScreenComposable(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 스크롤 상태 추적
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember {
//        derivedStateOf { scrollState.value >= scrollState.maxValue }
//    }
//
//    // 전체 레이아웃
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 스크롤 가능한 약관 본문
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(top = 56.dp + 12.dp, bottom = 100.dp) // 앱바 높이 + 버튼 여유
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
//링큐(LINK:U)는 이용자에게 맞춤혜택 및 유용한 정보를 전달하기 위해 아래와 같이 마케팅 목적의 개인정보 수집·이용 및 광고성 정보 수신에 대한 동의를 요청드립니다.
//
//1. 수집 및 이용 목적
//• 이벤트, 혜택, 프로모션 등 마케팅 정보 제공
//• 앱 기능 안내 및 맞춤형 콘텐츠 추천
//• 설문조사 및 사용자 참여 프로그램 안내
//
//2. 수집항목
//""".trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_marketing_table),
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
//※ 본 항목은 마케팅 정보 제공을 위해 별도로 수집되며, 동의하지 않아도 서비스 이용에는 제한이 없습니다.
//
//3. 보유 및 이용 기간
//• 동의 철회 또는 회원 탈퇴 시까지
//• 단, 관련 법령에 따른 보존 의무가 있는 경우 해당기간까지 보관
//
//4. 전송 방법
//• 이메일, 앱 푸시 알림, 인앱 메시지 등
//
//5. 동의 거부 권리 및 불이익
//• 본 동의는 선택 사항이며, 동의하지 않더라도 서비스 이용에 제한은 없습니다.
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
//                    text = "마케팅 수신 동의",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = Paperlogy,
//                    modifier = Modifier.padding(horizontal = 20.dp)
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
//        // ✅ 하단 고정 버튼 (기존 위치)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp) // 좌우/하단 여백 동일
//                .offset(y = -16.dp) // 🔽 버튼 위치를 더 아래로 (기기별 보정)
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
//fun MarketingTermsPreview() {
//    MaterialTheme {
//        MarketingTermsScreenComposable(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MarketingTermsScreenComposable(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 1) 스크롤 상태
//    val scrollState = rememberScrollState()
//    val isAtBottom by remember {
//        derivedStateOf { scrollState.value >= scrollState.maxValue }
//    }
//
//    // 2) Scaffold로 앱바 인셋 처리 + 앱바 완전 투명
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "마케팅 수신 동의",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium,
//                        fontFamily = Paperlogy,
//                        modifier = Modifier.padding(horizontal = 20.dp)
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
//                },
//                // ✅ 앱바 배경 컬러 제거 (스크롤 시도 포함 완전 투명)
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = Color.Transparent,
//                    scrolledContainerColor = Color.Transparent,
//                    navigationIconContentColor = LocalContentColor.current,
//                    titleContentColor = LocalContentColor.current,
//                    actionIconContentColor = LocalContentColor.current
//                )
//            )
//        }
//    ) { innerPadding ->
//        // 3) 콘텐츠: 앱바 아래에서 시작 + 16dp 간격, 좌우 20dp
//        Box(modifier = Modifier
//            .fillMaxSize()
//            .padding(innerPadding)) {
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(scrollState)
//                    .padding(top = 12.dp, bottom = 120.dp) // ⬅️ 앱바 아래 16dp, 하단 버튼 여유로 120dp
//                    .padding(horizontal = 20.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Column {
//                        Text(
//                            text = """
//링큐(LINK:U)는 이용자에게 맞춤혜택 및 유용한 정보를 전달하기 위해 아래와 같이 마케팅 목적의 개인정보 수집·이용 및 광고성 정보 수신에 대한 동의를 요청드립니다.
//
//1. 수집 및 이용 목적
//• 이벤트, 혜택, 프로모션 등 마케팅 정보 제공
//• 앱 기능 안내 및 맞춤형 콘텐츠 추천
//• 설문조사 및 사용자 참여 프로그램 안내
//
//2. 수집항목
//""".trimIndent(),
//                            fontSize = 14.sp,
//                            lineHeight = 22.sp,
//                            fontFamily = Paperlogy
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Image(
//                            painter = painterResource(id = R.drawable.img_marketing_table),
//                            contentDescription = "개인정보 수집 항목 표",
//                            contentScale = ContentScale.FillWidth,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp)
//                                .clip(RoundedCornerShape(4.dp))
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Text(
//                            text = """
//※ 본 항목은 마케팅 정보 제공을 위해 별도로 수집되며, 동의하지 않아도 서비스 이용에는 제한이 없습니다.
//
//3. 보유 및 이용 기간
//• 동의 철회 또는 회원 탈퇴 시까지
//• 단, 관련 법령에 따른 보존 의무가 있는 경우 해당기간까지 보관
//
//4. 전송 방법
//• 이메일, 앱 푸시 알림, 인앱 메시지 등
//
//5. 동의 거부 권리 및 불이익
//• 본 동의는 선택 사항이며, 동의하지 않더라도 서비스 이용에 제한은 없습니다.
//""".trimIndent(),
//                            fontSize = 14.sp,
//                            lineHeight = 22.sp,
//                            fontFamily = Paperlogy
//                        )
//                    }
//                }
//            }
//
//            // 4) 하단 고정 버튼: 화면 맨 아래, 키보드/내비게이션 대응
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .navigationBarsPadding()
//                    .imePadding()
//                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp)
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
//@Preview(showBackground = true)
//@Composable
//fun MarketingTermsPreview() {
//    MaterialTheme {
//        MarketingTermsScreenComposable(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MarketingTermsScreenComposable(
//    onAgreeClicked: () -> Unit,
//    onBackClicked: () -> Unit
//) {
//    // 스크롤 상태 추적 변수
//    val scrollState = rememberScrollState()
//
//    // 스크롤이 맨 아래에 도달했는지를 나타내는 상태 변수
//    val isAtBottom by remember {
//        derivedStateOf {
//            scrollState.value >= scrollState.maxValue
//        }
//    }
//
//    // 전체 화면을 감싸는 박스 레이아웃
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 스크롤 가능한 약관 본문
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
//                    .padding(16.dp)
//            ) {
//                Column {
//                    Text(
//                        text = """
//                            링큐(LINK:U)는 이용자에게 맞춤혜택 및 유용한 정보를 전달하기 위해 아래와 같이 마케팅 목적의 개인정보 수집·이용 및 광고성 정보 수신에 대한 동의를 요청드립니다.
//
//                            1. 수집 및 이용 목적
//                            • 이벤트, 혜택, 프로모션 등 마케팅 정보 제공
//                            • 앱 기능 안내 및 맞춤형 콘텐츠 추천
//                            • 설문조사 및 사용자 참여 프로그램 안내
//
//                            2. 수집항목
//                        """.trimIndent(),
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontFamily = Paperlogy
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.img_marketing_table),
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
//                            ※ 본 항목은 마케팅 정보 제공을 위해 별도로 수집되며, 동의하지 않아도 서비스 이용에는 제한이 없습니다.
//
//                            3. 보유 및 이용 기간
//                            • 동의 철회 또는 회원 탈퇴 시까지
//                            • 단, 관련 법령에 따른 보존 의무가 있는 경우 해당기간까지 보관
//
//                            4. 전송 방법
//                            • 이메일, 앱 푸시 알림, 인앱 메시지 등
//
//                            5. 동의 거부 권리 및 불이익
//                            • 본 동의는 선택 사항이며, 동의하지 않더라도 서비스 이용에 제한은 없습니다.
//                        """.trimIndent(),
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
//                    text = "마케팅 수신 동의",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = Paperlogy,
//                    modifier = Modifier.padding(horizontal = 20.dp)
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
//
//        // 하단 고정 버튼 (PrivacyTermsScreenFixed)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp) // 마케팅과 동일
//                .offset(y = -16.dp) // ⬅️ 더 아래로 내림 (기기 따라 12~18dp 사이 미세조정 가능)
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
//fun MarketingTermsPreview() {
//    MaterialTheme {
//        MarketingTermsScreenComposable(
//            onAgreeClicked = {},
//            onBackClicked = {}
//        )
//    }
//}