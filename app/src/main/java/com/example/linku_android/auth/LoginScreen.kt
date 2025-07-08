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
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 192.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로고
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.logo_whiteback),
            contentDescription = "Logo",
            modifier = Modifier.size(width = 105.dp, height = 105.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(2.dp))

        val DefaultTextStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        // 로고 텍스트
        Text("LINK U, THINK U", style = DefaultTextStyle)

        Spacer(modifier = Modifier.height(32.dp))

        // 아이디 입력 필드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2C6FFF), // 시작 파랑
                            Color(0xFFC800FF)  // 끝 핑크
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(1.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("아이디(이메일)", style = DefaultTextStyle) }, // 이걸로!
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력 필드
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
                placeholder = { Text("비밀번호", style = DefaultTextStyle) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
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

        Spacer(modifier = Modifier.height(32.dp))

        // 로그인하기 버튼
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
                        ) //dialog 뜰 수 있도록 구현하기.var showDialog by remember { mutableStateOf(false) } ->
                        //버튼 클릭 시 showDialog = true -> 화면에서 if (showDialog) { PasswordResetAlert(...) }
                    ),
                    shape = RoundedCornerShape(24.dp)
                    //
                ),
            contentAlignment = Alignment.Center // 텍스트 중앙!
        ) {
            Text(
                text = "로그인하기",
                color = Color.White,
                style = DefaultTextStyle.copy(color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  하단 메뉴
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("비밀번호 재설정", style = DefaultTextStyle)
            Text("  |  ", style = DefaultTextStyle)
            Text("회원가입", style = DefaultTextStyle)
        }
    }
}