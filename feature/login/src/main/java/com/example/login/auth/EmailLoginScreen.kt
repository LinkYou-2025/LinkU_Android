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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.core.repository.UserRepository

//이메일로 로그인하는 곳.
@Composable
fun EmailLoginScreen(
    navigator: NavHostController,
    loginViewModel: LoginViewModel? = null  //  nullable
    //loginViewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginRequested by remember { mutableStateOf(false) }
    //val loginResult by loginViewModel.loginState.collectAsState()
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    // ViewModel 상태 수집
    // ✅ 프리뷰에서는 ViewModel 생성 안 함
    val ui = loginViewModel?.loginState?.collectAsState()?.value
        ?: LoginViewModel.LoginState()  // 기본 빈 상태
    val isLoading = ui.loading
    val loginResult = ui.result

    // 고정 에러 문구 매핑 (비번 필드 아래 표시)
    val passwordErrorText: String? = when (ui.errorTag) {
        "INVALID_CREDENTIALS" -> "이메일 주소 또는 비밀번호를 다시 확인하세요."
        "SERVER_ERROR"        -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        else                  -> null
    }

    //  로그인 성공 시 이동 (Repo가 토큰 저장했고 여기선 이동만)
    LaunchedEffect(loginResult) {
        if (loginResult != null && loginResult.userId != -1) {
            navigator.navigate("home") {
                popUpTo(navigator.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
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
                painter = painterResource(id = R.drawable.ic_email_login_logo_change),
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
                            "이메일",
                            fontSize = 14.sp,
                            fontWeight = FontWeight(400),
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

            Spacer(modifier = Modifier.height(12.dp))

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
                            fontWeight = FontWeight(400),
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

            passwordErrorText?.let { err ->
                Spacer(Modifier.height(6.dp))
                Text(
                    text = err,
                    color = Color(0xFFFF5E5E),
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(x = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            //로그인 버튼
            val canLogin = !isLoading && isFormValid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (canLogin)
                                listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else
                                listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(enabled = canLogin) {
                        loginViewModel?.login(email.trim(), password.trim())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인하기",
                    fontFamily = Paperlogy,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clickable { navigator.navigate("resetPassword") }
                )

                // 구분선 |
                Text(
                    text = " | ",
                    color = Color(0xFF87898F),
                    fontSize = 15.sp,
                    fontFamily = Paperlogy,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)   // 🔥 이게 간격 맞춰줌
                )

                // 회원가입
                Text(
                    text = "회원가입",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF87898F),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clickable {
                            // TODO: 회원가입 로직 나중에 연결
                        }
                )

                //TODO : 여기 로직에 회원가입 빠져있음. 수정하기!!! => 데모데이때, 로직 꼬여서 뺐는데,
                //이전에 로직이 꼬였어서, 추후 리펙하면서 수정해볼게요..^^

            }

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}
@Preview(showBackground = true, name = "EmailLoginScreen Preview")
@Composable
fun EmailLoginScreenPreview() {
    val dummyNavController = rememberNavController()

    // 그냥 ViewModel 빼고 호출
    EmailLoginScreen(navigator = dummyNavController)
}

