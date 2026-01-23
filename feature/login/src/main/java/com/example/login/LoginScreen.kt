package com.example.login

//피그마에서 스플래쉬 다음으로 나오는 로그인 화면 입니다.


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import com.example.design.theme.LocalColorTheme
import com.example.design.util.DesignSystemBars
import com.example.login.ui.item.SocialLoginButton
import com.example.design.theme.font.Paperlogy
import com.example.design.util.scaler


@Composable
fun LoginScreen(
    navigator: NavHostController,
    logoOffsetY: Float = 0f,
    contentAlpha: Float = 1f,
    logoSlot: @Composable () -> Unit = {}, //로고가 들어갈 자리
    showLogo: Boolean = true, //로고 숨김(애니메이션 동안)
    
) {

    val colorTheme = LocalColorTheme.current

    // 스플래쉬 다음 화면도 역시 바텀바가 보이지 않도록 함.
    DesignSystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = false,
        immersive = true
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFC800FF),
                        Color(0xFF2C6FFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                )
            )
            .navigationBarsPadding()
    ) {

        /* =======================
         * 상단 로고 영역 (절대 수정 없음)
         * ======================= */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.scaler)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight(228f / 917f)
                        .height(logoOffsetY.scaler)
                )

                // 애니메이션 이후 로고가 들어올 자리
                logoSlot()
//                if (showLogo) {
//                    Image(
//                        painter = painterResource(id = R.drawable.img_login_logo),
//                        contentDescription = "LinkU Logo",
//                        modifier = Modifier
//                            .width(150.dp)
//                            .height(106.dp),
//                        contentScale = ContentScale.Fit
//                    )
//                }

                // 로고 아래 30dp 간격
                Spacer(modifier = Modifier.height(30.scaler))

                Column(
                    modifier = Modifier.alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Link U, Think You",
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontFamily = Paperlogy.font,
                        fontWeight = FontWeight(500),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // 문구 간 25dp 간격
                    Spacer(modifier = Modifier.height(25.scaler))

                    Text(
                        text = "링큐에 오신 것을 \n환영해요",
                        fontSize = 22.sp,
                        lineHeight = 30.sp,
                        fontFamily = Paperlogy.font,
                        fontWeight = FontWeight(700),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        /* =======================
         * 하단 버튼 그룹 (SNS + 이메일)
         * ======================= */
        val density = LocalDensity.current
        val imeBottom = WindowInsets.ime.getBottom(density)
        val navBottom = WindowInsets.navigationBars.getBottom(density)

        val bottomPadding = (when {
            imeBottom > 0 -> 20.dp
            navBottom > 0 -> 16.dp
            else -> 24.dp
        } + 70.dp).value.scaler//바텀 네비게이션 바 대신 그만큼 올리기!

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(contentAlpha)
                .padding(
                    start = 20.scaler,
                    end = 20.scaler,
                    bottom = bottomPadding
                ),
            verticalArrangement = Arrangement.spacedBy(10.scaler),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 카카오
            SocialLoginButton( //TODO 채윤지 : kakao sns api 로그인 나오면 연동하기
                backgroundColor = Color(0xFFFEE500),
                iconRes = R.drawable.icon_login_kakao,
                text = "카카오로 시작하기",
                textColor = Color.Black
            )

            // 네이버
            SocialLoginButton( //TODO 지현 : naver sns api 로그인 나오면 연동하기
                backgroundColor = Color(0xFF03C75A),
                iconRes = R.drawable.icon_login_naver,
                text = "네이버로 시작하기",
                textColor = Color.White
            )

            // 구글
            SocialLoginButton( //TODO 지민 : 구글 sns api 로그인 나오면 연동하기
                backgroundColor = Color.White,
                borderColor = Color(0xFFE0E0E0),
                iconRes = R.drawable.icon_login_google,
                text = "구글로 시작하기",
                textColor = Color.Black
            )

            // 이메일 기존 그대로 유지. //TODO 채윤지 : 서원에게 변경된 otp api 받으면 재연동하기
            SocialLoginButton(
                backgroundColor = Color.Transparent,
                borderColor = Color.White,
                iconRes = null,
                text = "이메일로 시작하기",
                textColor = Color.White,
                onClick = {
                    navigator.navigate("email_login")
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_6
)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navigator = navController)
}

