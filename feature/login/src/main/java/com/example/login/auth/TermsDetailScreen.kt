package com.example.login.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/* 3개의 약관 상세 화면
(서비스 이용약관, 개인정보 처리방침, 마케팅 수신 동의)의
"공통 UI 구조"를 담는 재사용 가능한 베이스 컴포저블*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsDetailScreen(
    title: String,
    content: String,
    isChecked: Boolean,
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onAgreeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                )
            ) {
                Text(text = "약관에 동의합니다")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsDetailPreview() {
    TermsDetailScreen(
        title = "서비스 이용약관",
        content = """
            제 1 조 (목적)
            본 약관은 회사(이하 "회사")가 제공하는 모든 서비스의 이용조건 및 절차, 이용자와 회사의 권리, 의무, 책임사항 등을 규정함을 목적으로 합니다.
            
            제 2 조 (정의)
            이 약관에서 사용하는 용어의 정의는 다음과 같습니다. ...
            """.trimIndent(),
        isChecked = false,
        onAgreeClicked = {},
        onBackClicked = {}
    )
}

