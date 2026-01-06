package com.example.login.auth

//피그마에서 스플래쉬 다음으로 나오는 로그인 화면 입니다.

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.text.style.TextAlign
import com.example.login.Paperlogy
import com.example.login.ui.item.SocialLoginButton
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Devices
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat



@Composable
fun LoginScreen(
    navigator: NavHostController,
    logoOffsetY: Float = 0f,
    contentAlpha: Float = 1f,
    logoSlot: @Composable () -> Unit = {}, //로고가 들어갈 자리
    showLogo: Boolean = true, //로고 숨김(애니메이션 동안)
    //emailButtonColor: Color = Color(0x66FFFFFF),
    //onSignUpClick: () -> Unit = {} //기존에 이메일로 시작하기 버튼이 반짝이인데 유지할지 말지 물어보기.
) {


    val isPreview = LocalInspectionMode.current
    val view = LocalView.current

    if (!isPreview) {
        val activity = view.context as Activity
        val window = activity.window

        SideEffect {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            WindowInsetsControllerCompat(window, view).apply {
                hide(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
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
                .padding(horizontal = 32.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight(228f / 917f)
                        .height(logoOffsetY.dp)
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
                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier.alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Link U, Think You",
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight(500),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // 문구 간 25dp 간격
                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "링큐에 오신 것을 \n환영해요",
                        fontSize = 22.sp,
                        lineHeight = 30.sp,
                        fontFamily = Paperlogy,
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

        val bottomPadding = when {
            imeBottom > 0 -> 20.dp
            navBottom > 0 -> 16.dp
            else -> 24.dp
        } + 70.dp //바텀 네비게이션 바 대신 그만큼 올리기!

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(contentAlpha)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = bottomPadding
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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

            // 이메일 기존 그대로 유지. //TODO 채윤지 : 하진 언니로부터 샌드 그리드에서 변경된 api 발생시 재연동 작업 하기....
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

