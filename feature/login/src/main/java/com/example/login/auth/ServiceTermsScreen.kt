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
                shape = RoundedCornerShape(18.dp)
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
   서비스 이용약관
   ───────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceTermsScreen(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isAtBottom by remember { derivedStateOf { scrollState.value >= scrollState.maxValue } }

    val serviceTermsBody = """
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
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp) // ⬅️ 좌우 20dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(18.dp)) // 라운드 빼먹음. 수정함.
                    .padding(16.dp) // ⬅️ 내부 16dp (마케팅과 동일)
            ) {
                Column {
                    // 제목만 16sp / Medium
                    Text(
                        text = "앱 서비스 이용약관",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Paperlogy,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(4.dp))  // 제목-본문 간격 8dp

                    // 본문
                    Text(
                        text = serviceTermsBody,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy
                    )
                }
            }

            // 풋터와 겹침 방지
            Spacer(Modifier.height(FOOTER_HEIGHT + FOOTER_BOTTOM + EXTRA_GAP))
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

