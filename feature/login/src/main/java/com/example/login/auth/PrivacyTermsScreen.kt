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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyTermsScreenFixed(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    // 스크롤 상태를 추적하는 변수
    val scrollState = rememberScrollState()

    // 스크롤이 끝까지 내려갔는지를 추적하는 상태
    val isAtBottom = remember {
        derivedStateOf {
            scrollState.value >= scrollState.maxValue
        }
    }.value

    // 전체 화면을 Box로 감싸고 상단 앱바, 내용, 버튼을 각각 배치
    Box(modifier = Modifier.fillMaxSize()) {

        // 스크롤 가능한 약관 내용
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 56.dp, bottom = 100.dp)
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.border(1.dp, Color(0xFF3399FF), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = """
                            개인정보 수집 및 이용 동의서

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

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_personal_table),
                        contentDescription = "개인정보 수집 항목 표",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = """
                            ※ 선택 항목은 입력하지 않아도 서비스 이용에 제한이 없습니다.

                            3. 개인정보의 보유 및 이용 기간
                            회사는 개인정보의 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기하며, 관련 법령에 따라 일정 기간 보관해야 하는 경우는 예외로 합니다.
                             • 회원 탈퇴 시 즉시 삭제
                             • 단, 관계 법령에 따라 일정 기간 보관하는 정보는 아래와 같습니다.(전자상거래법, 통신비밀보호법 등)
                            
                            4. 개인정보의 제3자 제공 회사는 회원의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 의거하여 요구되는 경우에는 예외로 합니다.
                            
                            5. 개인정보 처리 위탁 회사는 서비스 운영을 위해 필요한 경우 개인정보 처리를 위탁할 수 있으며, 위탁 시 회원에게 사전에 고지하고 동의를 받습니다.
                            
                            6. 개인정보 보호를 위한 권리 회원은 언제든지 개인정보 열람, 수정, 삭제 요청을 할 수 있으며, 이에 대한 문의는 고객센터를 통해 가능합니다.
                            
                            7. 동의 거부 권리 및 불이익 안내 회원은 개인정보 수집 및 이용에 대한 동의를 거부할 수 있으며, 다만 동의하지 않을 경우 서비스 이용에 제한이 있을 수 있습니다.
                            
                             본 동의서에 동의함으로써, 링큐의 개인정보 처리 방침에 따라 개인정보를 제공하는 것에 동의하게 됩니다.
                             
                             [부칙] 본 동의서는 2025년 08월 22일부터 시행됩니다.
                        """.trimIndent(),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )
                }
            }
        }

        // 상단 앱바 고정
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "개인정보 처리방침",
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back), // ⬅️ 커스텀 아이콘
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(16.dp) // ⬅️ Material 기본 ArrowBack 과 동일 사이즈
                    )
                }
            },
            modifier = Modifier.align(Alignment.TopStart)
        )

        // 하단 고정 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
                //.padding(horizontal = 16.dp, vertical = 26.dp)
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

@Preview(showBackground = true)
@Composable
fun PrivacyTermsScreenFixedPreview() {
    MaterialTheme {
        PrivacyTermsScreenFixed(
            onAgreeClicked = {},
            onBackClicked = {}
        )
    }
}


