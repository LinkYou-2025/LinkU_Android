package com.example.login.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens

//어차피.. 수정되니까.. 리펙X
@Preview(showBackground = true)
@Composable
fun PasswordResetScreen() {

    // 1. 디자인 시스템 토큰 및 반응형 유틸 가져오기
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()
    val paperlogyFamily = Paperlogy.font

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white) // 배경 시스템 화이트 적용
            .padding(horizontal = w(16f), vertical = h(80f)),
        verticalArrangement = Arrangement.SpaceBetween, // 상단/하단 나눠서
        horizontalAlignment = Alignment.Start // 왼쪽 정렬
    ) {


        Column(
            horizontalAlignment = Alignment.Start // 왼쪽 정렬
        ) {
            // 로고
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo_whiteback),
                contentDescription = "Logo",
                modifier = Modifier.size(width = w(105f), height = h(105f)),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(h(-8f)))

            // 타이틀
            Text(
                "비밀번호 재설정",
                fontSize = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(h(4f)))

            // 서브텍스트
            Text(
                "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
                fontSize = 13.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Normal,
                color = colorTheme.gray[600]!!,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(h(32f)))

            // 이메일 입력 필드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(h(56f))
                    .background(
                        brush = colorTheme.maincolor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            "이메일 주소를 입력해주세요.",
                            fontSize = 13.sp,
                            fontFamily = paperlogyFamily,
                            fontWeight = FontWeight.Normal,
                            color = colorTheme.gray[400]!!
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // 하단 버튼 (화면 아래)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(h(48f))
                .background(
                    brush = colorTheme.inactiveColor,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "임시 비밀번호 받기",
                color = Color.White,
                fontFamily = paperlogyFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}