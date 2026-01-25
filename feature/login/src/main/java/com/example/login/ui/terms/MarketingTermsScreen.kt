package com.example.login.ui.terms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.design.theme.font.Paperlogy

//이거는 ui 자체가 바뀔 예정. 리펙하지 않음.
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

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val paperlogyFamily = Paperlogy.font

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
            fontFamily = paperlogyFamily
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

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val paperlogyFamily = Paperlogy.font

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
                        fontFamily = paperlogyFamily,
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
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(18.dp)) // ✅ 배경색 추가
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "마케팅 수신 동의서",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = paperlogyFamily,
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
                        fontFamily = paperlogyFamily
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
                        fontFamily = paperlogyFamily
                    )
                }
            }

            // ✅ 맨 아래 여유를 명시적으로 추가(풋터와 겹침 방지)
            Spacer(Modifier.height(FOOTER_HEIGHT + FOOTER_BOTTOM + EXTRA_GAP))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarketingTermsPreview() {
    MaterialTheme {
        MarketingTermsScreenComposable(onAgreeClicked = {}, onBackClicked = {})
    }
}
