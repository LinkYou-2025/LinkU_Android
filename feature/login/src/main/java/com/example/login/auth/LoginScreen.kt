package com.example.login.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
                .padding(horizontal = 32.dp)
                .graphicsLayer { alpha = contentAlpha }, // 전체 콘텐츠 페이드 인
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로고
            Image(
                painter = painterResource(R.drawable.logo_white),
                contentDescription = "Logo",
                modifier = Modifier
                    .offset(y = logoOffsetY.dp)
                    .size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 이메일 로그인 버튼
            Button(
                onClick = {
                    navigator.navigate("emailLogin")
                },
                colors = ButtonDefaults.buttonColors(containerColor = emailButtonColor),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        " ✉ 이메일로 로그인",
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
                    fontSize = 14.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable { }
                )
                Text("  |  ", color = Color.White, fontSize = 14.sp)
                Text(
                    "회원가입",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable {
                        onSignUpClick() // 회원가입 클릭 시 바텀시트 콜백 실행
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            // 카카오 / 네이버 로그인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Yellow, CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_kakao),
                        contentDescription = "Kakao",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Green, CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_naver),
                        contentDescription = "Naver",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val dummyNavController = rememberNavController()
    LoginScreen(navigator = dummyNavController)
}

//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalInspectionMode
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.unit.Dp
//import com.example.linku_android.R
//import com.example.linku_android.component.Paperlogy
//
////함수 파라미터 : 애니메이션 대응!
//
//
//
//@Composable
//fun LoginScreen() {
//    val isPreview = LocalInspectionMode.current
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFE93CFF),
//                        Color(0xFF5C6CFF)
//                    )
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 32.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//
//            // 로고
//            Image(
//                painter = painterResource(R.drawable.logo_white),
//                contentDescription = "Logo",
//                modifier = Modifier.size(160.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            //  이메일 로그인 버튼
//            Button(
//                onClick = { },
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0x66FFFFFF)),
//                shape = RoundedCornerShape(50),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
////                    Image(
////                        painter = painterResource(R.drawable.ic_email),
////                        contentDescription = "이메일 아이콘",
////                        modifier = Modifier
////                            .size(20.dp)
////                            .align(Alignment.CenterVertically)
////                    )
////
////                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Text(
//                        " ✉ 이메일로 로그인",
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            //  비밀번호 재설정 & 회원가입
//            Row {
//                Text(
//                    "비밀번호 재설정",
//                    color = Color.White,
//                    fontSize = 14.sp,
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.clickable { }
//                )
//                Text(
//                    "  |  ",
//                    color = Color.White,
//                    fontSize = 14.sp
//                )
//                Text(
//                    "회원가입",
//                    color = Color.White,
//                    fontSize = 14.sp,
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.clickable { }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            //  간편 로그인 텍스트 & 구분선
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    Modifier
//                        .weight(1f)
//                        .height(1.dp)
//                        .background(Color.White.copy(alpha = 0.5f))
//                )
//                Text(
//                    "  간편 로그인  ",
//                    color = Color.White,
//                    fontSize = 12.sp,
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Normal,
//                )
//                Box(
//                    Modifier
//                        .weight(1f)
//                        .height(1.dp)
//                        .background(Color.White.copy(alpha = 0.5f))
//                )
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // 간편 로그인 아이콘
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(24.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .background(Color.Yellow, CircleShape)
//                        .clickable { },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.ic_kakao),
//                        contentDescription = "Kakao",
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.size(100.dp)
//                    )
//                }
//
//                Box(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .background(Color.Green, CircleShape)
//                        .clickable { },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.ic_naver),
//                        contentDescription = "Naver",
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.size(100.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen()
//}