package com.example.login.ui.terms



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.design.theme.font.Paperlogy
import com.example.login.ui.item.AgreeFooterButton
import com.example.login.ui.item.GradientButtonCore


private val EXTRA_GAP = 20.dp
private val FOOTER_HEIGHT = 50.dp  // 본문의 마지막 내용이 하단 버튼



/* ─────────────────────────────
   서비스 이용약관
   ───────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceTermsScreen(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val paperlogyFamily = Paperlogy.font

    val scrollState = rememberScrollState()
    val isAtBottom by remember {
        derivedStateOf {
            if (scrollState.maxValue > 0) {
                scrollState.value >= (scrollState.maxValue - 2) // 2px 정도 여유
            } else {
                // 콘텐츠가 너무 짧아 스크롤이 필요 없는 경우 (상황에 따라 true/false 선택)
                true
            }
        }
    }

    val serviceTermsBody = """
본 약관은 링큐(이하 “회사”)가 제공하는 링큐(LINK:U) 서비스의 이용과 관련하여 회사와 이용자 간의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.

제 1 조 (목적)
본 약관은 회사가 제공하는 링크 콘텐츠 저장 및 AI 기반 큐레이션 서비스 링큐(LINK:U)의 이용 조건 및 절차, 회사와 회원 간의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.

제 2 조 (용어의 정의)
본 약관에서 사용되는 용어의 정의는 다음과 같습니다.

1. “서비스”란 회사가 제공하는 링큐 모바일 애플리케이션 및 관련 제반 서비스를 의미합니다.
2. “회원”이란 본 약관에 동의하고 회사와 이용 계약을 체결한 자를 말합니다.
3. “콘텐츠”란 회원이 서비스 내에 저장·생성·공유하는 링크, 텍스트, 이미지 등 모든 정보를 의미합니다.
4. “큐레이션”이란 회원의 저장 데이터, 감정·상황 입력 등을 기반으로 AI가 추천하는 콘텐츠를 의미합니다.
5. “계정”이란 회원 식별 및 서비스 이용을 위하여 생성된 소셜 로그인·이메일 기반 식별 수단을 말합니다.

제 3 조 (약관의 효력 및 변경)
1. 본 약관은 회원가입 과정에서 게시함으로써 효력이 발생합니다.
2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 본 약관을 개정할 수 있으며, 약관이 변경될 경우 회사는 변경 내용과 시행일자를 적용일 7일 전(중대한 사항은 30일 전)부터 서비스 내 공지사항을 통해 회원에게 사전에 안내합니다.
3. 회원이 변경된 약관에 동의하지 않는 경우, 서비스 이용을 중단하고 회원 탈퇴를 요청할 수 있습니다. 
4. 회사가 변경된 약관의 내용을 명확히 공지한 이후에도 회원이 시행일로부터 7일 이내에 거부 의사를 표시하지 않고 서비스를 계속 이용하는 경우, 해당 회원은 변경된 약관에 동의한 것으로 봅니다.  다만, 회원의 권리 또는 의무에 중대한 영향을 미치는 변경 사항의 경우에는 별도의 동의 절차를 거칠 수 있습니다.

제 4 조 (이용계약의 체결)
1. 이용계약은 회원이 약관에 동의하고 회원가입을 완료함으로써 체결됩니다.
2. 회사는 다음 각 호에 해당하는 경우 이용계약 체결을 거부하거나 사후에 해지할 수 있습니다.
    · 타인의 정보를 도용한 경우
    · 서비스 운영을 고의로 방해한 경우
    · 관련 법령 또는 본 약관을 위반한 경우
    
제 5 조  (회원가입 및 계정 관리)
1. 회원가입은 카카오, 구글 소셜 로그인 또는 이메일 로그인을 통해 이루어집니다.
2. 회원은 계정 정보에 변경이 있을 경우 즉시 수정해야 합니다.
3. 회원은 본인의 계정을 제3자에게 양도하거나 공유할 수 없습니다.

제 6 조 (서비스의 제공 및 변경)
1. 회사는 다음과 같은 서비스를 제공합니다.
    · 링크 저장 및 관리 기능
    · AI 기반 콘텐츠 요약 및 카테고리 분류
    · 감정·상황 기반 콘텐츠 큐레이션
    · 콘텐츠 공유 기능
2. 회사는 서비스의 운영상 필요에 따라 서비스의 일부 또는 전부를 변경, 중단할 수 있으며, 이 경우 사전에 공지합니다.
3. 무료로 제공되는 서비스의 일부는 향후 유료로 전환될 수 있습니다.

제 7 조 (서비스 이용 제한)
회사는 다음 각 호에 해당하는 경우 사전 통지 또는 사후 통지를 통해 회원의 서비스 이용을 제한할 수 있습니다.
1. 관련 법령을 위반하거나 타인의 권리를 침해하는 방식으로 콘텐츠를 저장·공유한 경우
2. 서비스의 정상적인 운영을 고의 또는 중대한 과실로 방해한 경우

제 8 조 (콘텐츠의 권리 및 책임)
1. 회원이 서비스에 저장하거나 공유하는 콘텐츠의 저작권은 해당 콘텐츠의 저작권자에게 귀속됩니다.
2. 회원은 자신이 서비스에 저장하거나 공유하는 콘텐츠에 대해 관련 법령에 위반되지 않도록 이용하여야 합니다.
3. 회사는 서비스의 제공, 운영, 개선 및 기능 제공을 위하여 필요한 범위 내에서만 회원이 저장한 콘텐츠를 기술적으로 처리하거나 노출할 수 있으며, 이를 서비스 외의 목적으로 무단 활용하지 않습니다.
4. 회원이 관련 법령을 위반하거나 타인의 권리를 침해하는 방식으로 콘텐츠를 이용 또는 공유한 경우, 그에 대한 책임은 해당 회원에게 귀속됩니다.

제 9 조 (개인정보 보호)
1. 회사는 회원의 개인정보를 보호하며, 개인정보의 처리에 관한 사항은 개인정보 처리방침에 따릅니다.
2. 회사는 회원의 개인정보를 본인의 동의 없이 제3자에게 제공하지 않습니다.

제 10 조 (마케팅 정보 수신)
1. 회사는 회원의 선택적 동의에 따라 마케팅 정보를 발송할 수 있습니다.
2. 회원은 언제든지 마이페이지 설정에서 마케팅 정보 수신 동의를 철회할 수 있으며, 동의 거부로 인해 서비스 이용에 제한은 없습니다.

제 11 조 (계약 해지 및 회원 탈퇴)
1. 회원은 언제든지 서비스 내 설정을 통해 회원 탈퇴를 요청할 수 있습니다.
2. 회원 탈퇴 시 회원의 개인정보는 개인정보 처리방침에 따라 처리됩니다.

제 12 조 (면책 조항)
1. 회사는 천재지변, 시스템 장애 등 불가항력 사유로 인한 서비스 중단에 대해 책임을 지지 않습니다.
2. 회사는 회원이 서비스 내 콘텐츠를 이용하여 발생한 손해에 대해 책임을 지지 않습니다.
3. 회사가 제공하는 AI 기반 요약, 분류, 큐레이션 결과는 정보 제공 및 참고 목적이며, 그 정확성, 완전성, 최신성을 보장하지 않습니다. 회원은 해당 결과를 참고 자료로 활용하며, 이를 신뢰하여 발생한 판단 또는 행위에 대한 책임은 회원 본인에게 있습니다.
4. 회사는 회원 간 또는 회원과 제3자 간 분쟁에 개입하지 않습니다.

제 13 조 (준거법 및 관할)
본 약관은 대한민국 법령을 준거법으로 하며, 서비스 이용과 관련하여 회사와 회원 간 발생한 분쟁에 대해서는 민사소송법상 관할 법원을 제1심 관할 법원으로 합니다.


[부칙] 본 약관은 2025년 03월 03일부터 시행됩니다.  
""".trimIndent() //TODO : 나중에 날짜 변경하세요.

    Scaffold(
        topBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // 상단바 여백 확보를 위한 높이
                    .background(Color.White)
            ) {
                // 뒤로가기 버튼: 위 59dp, 좌 20dp
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 59.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(16.dp)
                    )
                }


                Text(
                    text = "서비스 이용약관",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = paperlogyFamily,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 62.dp)
                )
            }
        },
        bottomBar = {
            AgreeFooterButton(
                text = "약관에 동의합니다",
                enabled = isAtBottom,
                onClick = onAgreeClicked
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(18.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "링큐(LINK:U) 서비스 이용약관",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = paperlogyFamily,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = serviceTermsBody,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = paperlogyFamily
                    )
                }
            }
            Spacer(Modifier.height(FOOTER_HEIGHT + EXTRA_GAP))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceTermsScreenPreview() {
    MaterialTheme {
        ServiceTermsScreen(
            onAgreeClicked = {},
            onBackClicked = {}
        )
    }
}

