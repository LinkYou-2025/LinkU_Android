package com.example.login.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.login.R
import com.example.login.Paperlogy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun EmailLoginScreen(
    navigator: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by loginViewModel.loginState.collectAsState()

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid
    // 로그인 응답 감지
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            when {
                result.status == "INACTIVE" -> {
                    Log.w("Login", "⚠️ 계정이 탈퇴 예정 (inactiveDate=${result.inactiveDate})")
                    // 경고 다이얼로그는 아래 remember { mutableStateOf(false) }로 제어
                }
                result.userId != -1 -> {
                    Log.d("Login", "로그인 성공 → token=${result.token}")
                    navigator.navigate("home")
                }
                else -> {
                    Log.e("Login", "로그인 실패: 유효하지 않은 사용자")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로고
            Image(
                painter = painterResource(id = R.drawable.ic_email_login_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 이메일 입력
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            "아이디(이메일)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = Paperlogy,
                            color = Color(0xFFB7B9BF)
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

            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호 입력
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            "비밀번호",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = Paperlogy,
                            color = Color(0xFFB7B9BF)
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
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

            Spacer(modifier = Modifier.height(24.dp))

            //로그인 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isFormValid)
                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else
                                listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(enabled = isFormValid) {
                        loginViewModel.login(email, password) // ✅ StateFlow 업데이트만 실행
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인 하기",
                    fontFamily = Paperlogy,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 비밀번호 재설정 | 회원가입
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "비밀번호 재설정",
                    color = Color(0xFF87898F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Paperlogy,
                    modifier = Modifier.clickable {
                        navigator.navigate("resetPassword")
                    }
                )
                Text("  |  ", color = Color(0xFF87898F), fontSize = 14.sp)
                Text(
                    "회원가입",
                    color = Color(0xFF87898F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Paperlogy,
                    modifier = Modifier.clickable {
                        navigator.navigate("terms_agreement")
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}


@Preview(showBackground = true, name = "EmailLoginScreen Preview")
@Composable
fun EmailLoginScreenPreview() {
    val dummyNavController = rememberNavController()
    EmailLoginScreen(navigator = dummyNavController)
}
