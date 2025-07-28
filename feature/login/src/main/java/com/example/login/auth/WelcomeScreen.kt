package com.example.login.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun WelcomeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    // StateFlow → Compose State 변환
    val signUpResponseState = signUpViewModel.signUpState.collectAsState()
    val signUpResponse = signUpResponseState.value

    // 화면 진입 시 API 한 번 호출
    LaunchedEffect(Unit) {
        signUpViewModel.signUp()
    }

    // 서버 응답 감지 후 로깅
    LaunchedEffect(signUpResponse) {
        signUpResponse?.let {
            if (it.isSuccess) {
                Log.d("WelcomeScreen", "회원가입 성공: ${it.message}")
            } else {
                Log.e("WelcomeScreen", "회원가입 실패: ${it.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C6FFF),
                        Color(0xFFC800FF)
                    )
                )
            )
    ) {
        // 중앙 콘텐츠 (Column)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_white),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(0.dp))

            Text(
                text = "링큐에 오신 걸 환영해요!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "당신을 위한 링크, 링큐가 기억하고 연결해줄게요!",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // 버튼을 Box의 직접 자식으로 두고, 하단 정렬
        Button(
            onClick = {
                navigator.navigate("email_login") // 경로는 실제 화면 이름으로 변경
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter) // 여기가 핵심!
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "로그인 하러가기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2C6FFF), // 파랑
                            Color(0xFFC800FF)  // 분홍
                        )
                    )
                ),
                fontFamily = Paperlogy
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val fakeNavController = rememberNavController()
    WelcomeScreen(navigator = fakeNavController)
}