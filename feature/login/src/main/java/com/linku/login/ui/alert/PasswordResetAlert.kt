package com.linku.login.ui.alert

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R

@Composable
fun PasswordResetAlert(
    onDismissRequest: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    // 현재 디자인 테마의 컬러 스킴 가져오기
    val colorTheme = MaterialTheme.linkuColors

    // usePlatformDefaultWidth = false: 다이얼로그 창 너비 제한을 풀어서 아래 fillMaxWidth가 실제 화면 폭 기준으로 동작하게 함
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.scaler) // Figma: 화면 좌우 20px 여백
                .widthIn(max = 372.scaler) // 태블릿 등 큰 화면에서 카드가 과도하게 넓어지지 않도록 상한
                .background(
                    color = colorTheme.white,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 상단 간격 22
            Spacer(modifier = Modifier.height(22.dp))

            // 🔹 로고
            Image(
                painter = painterResource(id = R.drawable.ic_logo_color),
                contentDescription = null,
                modifier = Modifier
                    .width(35.25864.dp)
                    .height(25.00011.dp)
                    .alpha(0.5f)
            )

            // 🔹 로고 ↔ 타이틀 간격 23
            Spacer(modifier = Modifier.height(23.dp))

            // 타이틀
            Text(
                text = "비밀번호 재설정 메일 전송 완료!",
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = colorTheme.black,
                textAlign = TextAlign.Center

            )
            // 🔹 타이틀 ↔ 설명 간격 20
            Spacer(modifier = Modifier.height(20.dp))

            // 설명 텍스트
            Text(
                text = "비밀번호 재설정 메일을 발송했습니다.\n메일함을 확인해주세요!",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = colorTheme.gray[600],
                textAlign = TextAlign.Center

            )

            // 🔹 설명 ↔ 버튼 간격 24 (다이얼로그 높이를 고정하지 않으므로 weight(1f) 대신 고정 간격 사용)
            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 버튼 (높이를 고정하지 않고 텍스트 위아래 패딩으로 보장, 폰트 확대 시에도 안전)
            Box(
                modifier = Modifier
                    .width(316.dp)
                    .background(
                        brush = colorTheme.maincolor,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onConfirmClick() }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인 하러가기",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTheme.white,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Preview
@Composable
private fun PasswordResetAlertPreview() {
    LinkuPreview {
        PasswordResetAlert()
    }
}
