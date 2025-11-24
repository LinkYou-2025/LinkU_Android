package com.example.login.auth
//여기는 링큐 로그인 하는 스크린입니다.
//추후, 디자인이 나오면 쇼셜 로그인 구현이 필요합니다.

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController



@Composable
fun LoginScreen(
    navigator: NavHostController,
    logoOffsetY: Float = 0f,
    contentAlpha: Float = 1f,
    emailButtonColor: Color = Color(0x66FFFFFF),
    onSignUpClick: () -> Unit = {} // 회원가입 클릭 시 호출되는 콜백 함수
) {

    LaunchedEffect(Unit) {
        println(" LoginScreen Loaded")
    }
    val isPreview = LocalInspectionMode.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFC800FF), Color(0xFF2C6FFF)),
                    start = Offset(0f, 0f),           // 좌상단
                    end = Offset.Infinite             // 우하단 (대각선)
                )
            )
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
                //.graphicsLayer { alpha = contentAlpha }, // 전체 콘텐츠 페이드 인
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로고
            if (isPreview) {
                // 프리뷰에서만: 이미지 대신 실제 이미지 대신 레이아웃만 그림,
                //이유는 프리뷰에서 로고가 나오면 깨져서.. 그냥 공간만 차지하게 구현함.
                Box(
                    modifier = Modifier
                        .offset(y = logoOffsetY.dp)
                        .size(160.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.img_login_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .offset(y = logoOffsetY.dp)
                        .size(160.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(40.dp))

            // 이메일 로그인 버튼
            // 이메일 로그인 "버튼"(Surface + clickable)
            Surface(
                color = emailButtonColor,
                shape = RoundedCornerShape(32),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(
                        indication = null, // ✅ 리플/애니메이션 없음
                        interactionSource = remember { MutableInteractionSource() }
                    ) { navigator.navigate("email_login") }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp) // 기존 contentPadding 역할
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_email_png),
                        contentDescription = "이메일 로그인",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "이메일로 로그인",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


                Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호 재설정 | 회원가입
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "비밀번호 재설정",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navigator.navigate("resetPassword")
                    }
                )
                Text("  |  ", color = Color.White, fontSize = 14.sp)
                Text(
                    "회원가입",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        onSignUpClick()
//                        navigator.navigate("terms_agreement") {
//                            launchSingleTop = true // 동일 화면 중복 쌓임 방지
//                        }
                        //onSignUpClick() // 회원가입 클릭 시 바텀시트 콜백 실행
                    }
                )
            }

            Spacer(modifier = Modifier.height(72.dp))

            // 간편 로그인 안내
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                )
                Text(
                    "  간편 로그인  ",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Normal
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 카카오 / 네이버 로그인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 카카오
                Image(
                    painter = painterResource(R.drawable.ic_kakao),
                    contentDescription = "Kakao",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(46.dp)   // 아이콘 크기 줄임
                        .clickable { }
                )

                Spacer(modifier = Modifier.width(24.dp))

                // 네이버
                Image(
                    painter = painterResource(R.drawable.ic_naver),
                    contentDescription = "Naver",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(46.dp)   // 아이콘 크기 줄임
                        .clickable { }
                )
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val dummyNavController = rememberNavController()
    LoginScreen(navigator = dummyNavController, logoOffsetY = 0f)
}

