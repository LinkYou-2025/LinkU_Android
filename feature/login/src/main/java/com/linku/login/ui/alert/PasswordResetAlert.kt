package com.linku.login.ui.alert

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.R

@Composable
fun PasswordResetAlert(
    onDismissRequest: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    // 현재 디자인 테마의 컬러 스킴 가져오기
    val colorTheme = MaterialTheme.linkuColors

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .width(372.dp)
                .height(246.dp)
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
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium, // 500은 Medium입니다.
                    color = colorTheme.black,
                    textAlign = TextAlign.Center
                )
            )
            // 🔹 타이틀 ↔ 설명 간격 20
            Spacer(modifier = Modifier.height(20.dp))

            // 설명 텍스트
            Text(
                text = "비밀번호 재설정 메일을 발송했습니다.\n메일함을 확인해주세요!",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal, // 400은 Normal입니다.
                    color = colorTheme.gray[600],
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 🔹 버튼
            Box(
                modifier = Modifier
                    .width(316.dp)
                    .height(50.dp)
                    .background(
                        brush = colorTheme.maincolor,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onConfirmClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인 하러가기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold, // 700은 Bold입니다.
                        color = colorTheme.white,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF2F2F2
)
@Composable
private fun PasswordResetAlertPreview() {
    LinkuPreview {
        PasswordResetAlert()
    }
}
