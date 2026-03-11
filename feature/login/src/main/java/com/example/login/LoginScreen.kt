package com.example.login

//피그마에서 스플래쉬 다음으로 나오는 로그인 화면 입니다.


import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.login.constants.ServerConfig
import com.example.login.viewmodel.SocialAuthViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

private const val TAG = "LoginScreen"


// 카카오 로그인 로직 분리
// https://developers.kakao.com/docs/latest/ko/kakaologin/android 예제 코드 그대로 사용함.
private fun handleKakaoLogin(
    context: Context,
    viewModel: SocialAuthViewModel
) {
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            viewModel.loginWithKakao(token.accessToken) //뷰모델에서 로그인 상태 확인 함수 사용용으로 1줄 추가함.
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            //Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
        }
    }

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                viewModel.loginWithKakao(token.accessToken) // 로그인 성공시 호출함.
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                //Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}

@Composable
fun LoginScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel,
    onLoginSuccess: () -> Unit = {},
    logoOffsetY: Float = 0f,
    contentAlpha: Float = 1f,
    logoSlot: @Composable () -> Unit = {}, //로고가 들어갈 자리
    showLogo: Boolean = true, //로고 숨김(애니메이션 동안)
    
) {

    val colorTheme = LocalColorTheme.current
    val context = LocalContext.current

    //카카오 로그인 state 수집
    val kakaoLoginState by viewModel.kakaoLoginState.collectAsState()


    LaunchedEffect(kakaoLoginState) {
        when (kakaoLoginState) {
            is SocialAuthViewModel.KakaoLoginState.Success -> {
                val result = (kakaoLoginState as SocialAuthViewModel.KakaoLoginState.Success).result

                when (result.status) {
                    "ACTIVE" -> onLoginSuccess()  // 기존 유저 → 홈
                    "TEMP" -> navigator.navigate("social_login_gate") // 신규 유저 → 약관
                }
                viewModel.resetKakaoLoginState() // 로그인 성공 후 -> 뒤로 가기시 재실행되는 중복 호출 문제 방지. rest 하면 idle로 돌아감.
            }
            is SocialAuthViewModel.KakaoLoginState.Error -> {
                // TODO: 에러 메시지 표시
                viewModel.resetKakaoLoginState()
            }
            else -> {}
        }
    }

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
                textColor = Color.Black,
                onClick = {
                    handleKakaoLogin(context, viewModel)
                }
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
                textColor = Color.Black,
                onClick = {
                    val url = ServerConfig.GOOGLE_LOGIN_URL
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                }
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

@Preview(showBackground = true, device = Devices.PIXEL_6)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(
        navigator = navController,
        viewModel = viewModel() // 프리뷰용
    )
}
