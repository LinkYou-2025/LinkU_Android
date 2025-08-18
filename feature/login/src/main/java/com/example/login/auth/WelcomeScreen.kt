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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun WelcomeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    //  signUpSuccess(Boolean?) 사용
    val signUpSuccess by signUpViewModel.signUpSuccess.collectAsState()
    var isSignUpRequested by remember { mutableStateOf(false) } //중복 호출 방자용 상태 추가
    // 화면 진입 시 회원가입 요청
//    LaunchedEffect(Unit) {
//        signUpViewModel.signUp()
//    }

    // 서버 응답 감지
    LaunchedEffect(signUpSuccess) {
        when (signUpSuccess) {
            true -> {
                Log.d("WelcomeScreen", " 회원가입 성공")
                //navigator.navigate("home")  // 회원가입 후 홈으로 이동
                // 수정
                navigator.navigate("email_login") {
                    popUpTo("welcome") { inclusive = true } // WelcomeScreen 제거
                }
                isSignUpRequested = false
            }
            //false -> Log.e("WelcomeScreen", " 회원가입 실패")
            false -> {
                Log.e("WelcomeScreen", "회원가입 실패")
                isSignUpRequested = false //  실패 시 버튼 다시 활성화
            }
            null -> {} // 아직 응답 없음

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
                painter = painterResource(id = R.drawable.img_logo_white),
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
                if (!isSignUpRequested) { // 한 번만 호출되도록 체크
                    isSignUpRequested = true
                    Log.d("WelcomeScreen", "회원가입 API 호출 시도")
                    signUpViewModel.signUp()
                }
            },
            enabled = !isSignUpRequested, // 요청 중일 때 비활성화
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "회원가입 완료하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
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