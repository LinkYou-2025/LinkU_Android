package com.example.linku_android.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TermsAgreementContent(
    onAgreeAllChecked: (Boolean) -> Unit = {},
    onNextClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // ✅ 약관 전체동의 + 설명
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = false,
                onCheckedChange = onAgreeAllChecked
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "약관 전체동의",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "선택항목에 대한 동의 포함",
                fontSize = 12.sp,
                color = Color(0xFFB7B9BF)
            )
        }

        Divider(
            color = Color(0xFFE5E5E5),
            modifier = Modifier.padding(vertical = 4.dp) // 텀 줄임!
        )

        AgreementItem(
            title = "이용약관",
            suffix = "(필수)",
            suffixColor = Color(0xFF2C6FFF)
        )
        AgreementItem(
            title = "개인정보 처리방침",
            suffix = "(필수)",
            suffixColor = Color(0xFF2C6FFF)
        )
        AgreementItem(
            title = "마케팅 수신 동의",
            suffix = "(선택)",
            suffixColor = Color(0xFFB7B9BF)
        )

        Spacer(modifier = Modifier.height(20.dp)) // 아래 버튼과 간격도 살짝 줄임

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFC2D1FF),
                            Color(0xFFE6D5F8)
                        )
                    ),
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AgreementItem(
    title: String,
    suffix: String,
    suffixColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { /* TODO */ }
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = suffix,
            fontSize = 12.sp,
            color = suffixColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TermsAgreementContentPreview() {
    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        TermsAgreementContent()
    }
}