package com.example.linku_android.auth


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
import com.example.linku_android.R

@Preview(showBackground = true)
@Composable
fun PasswordResetScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 80.dp),
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
                modifier = Modifier.size(width = 105.dp, height = 105.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(-8.dp))

            // 타이틀
            Text(
                "비밀번호 재설정",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 서브텍스트
            Text(
                "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF757575),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 이메일 입력 필드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2C6FFF),
                                Color(0xFFC800FF)
                            )
                        ),
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
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF757575)
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
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF9BCBFF), // 연파랑
                            Color(0xFFF4AFFF)  // 연핑크
                            //**이후 제대로 입력하면 컬러를  Color(0xFF2C6FFF),Color(0xFFC800FF)로 변경을 해야함.**
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "임시 비밀번호 받기",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}