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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingTermsScreenComposable(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    // 스크롤 상태 추적 변수
    val scrollState = rememberScrollState()

    // 스크롤이 맨 아래에 도달했는지를 나타내는 상태 변수
    val isAtBottom by remember {
        derivedStateOf {
            scrollState.value >= scrollState.maxValue
        }
    }

    // 전체 화면을 감싸는 박스 레이아웃
    Box(modifier = Modifier.fillMaxSize()) {

        // 스크롤 가능한 약관 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 56.dp, bottom = 100.dp)
                .padding(horizontal = 20.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_marketing_table),
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
        }

        // 상단 앱바
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
                        painter = painterResource(id = R.drawable.ic_back), // ⬅️ 커스텀 아이콘
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(16.dp) // ⬅️ Material 기본 ArrowBack 과 동일 사이즈
                    )
                }
            },
            modifier = Modifier.align(Alignment.TopStart)
        )


        // 하단 고정 버튼 (PrivacyTermsScreenFixed)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp) // 마케팅과 동일
                .offset(y = -16.dp) // ⬅️ 더 아래로 내림 (기기 따라 12~18dp 사이 미세조정 가능)
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
fun MarketingTermsPreview() {
    MaterialTheme {
        MarketingTermsScreenComposable(
            onAgreeClicked = {},
            onBackClicked = {}
        )
    }
}