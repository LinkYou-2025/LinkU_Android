package com.example.linku_android.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun PasswordResetAlert(
    onDismissRequest: () -> Unit = {},  //기본값 추가
    onConfirmClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 타이틀
            Text(
                text = "임시 비밀번호 전송 완료!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 서브텍스트
            Text(
                text = "임시 비밀번호를 보내드렸습니다.\n메일함을 확인해주세요!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2C6FFF), // 피그마와 동일한 그라디언트
                                Color(0xFFC800FF)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .wrapContentHeight(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
                    .clickable { onConfirmClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인 하러가기",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}