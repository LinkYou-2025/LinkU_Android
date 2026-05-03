package com.linku.login

//피그마에서 스플래쉬 다음으로 나오는 로그인 화면 입니다.


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.DesignSystemBars
import com.linku.design.util.scaler
import com.linku.login.auth.GoogleAuthHelper
import com.linku.login.auth.findActivity
import com.linku.login.ui.item.SocialLoginButton
import com.linku.login.viewmodel.SocialAuthViewModel
import kotlinx.coroutines.launch


private const val TAG = "LoginScreen"


// 카카오 로그인 로직 분리
// https://developers.kakao.com/docs/latest/ko/kakaologin/android 예제 코드 그대로 사용함.
private fun handleKakaoLogin(
    context: Context,
    viewModel: SocialAuthViewModel
) {
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    // callback 나중에 결과가 오면 실행해줘 등록하는 함수임.
    // OAuthToken? → 성공하면 토큰이 올 자리 (실패하면 null)  Throwable? → 실패하면 에러가 올 자리 (성공하면 null)
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            // 실패 시
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            // 카카오 sdk가 토큰을 받아오면 밑에 뷰모델 호출
            viewModel.loginWithKakao(token.accessToken) //뷰모델에서 로그인 상태 확인 함수 사용용으로 1줄 추가함.
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            //Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
        }
    }

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        // 카카오톡 앱이 설치된 경우
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            // 실패하면 -> 앱으로 로그인 & 성공하면 viewModel.loginWithKakao() 호출
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)
                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk // 사용자가 의도적으로 취소 -> 그냥 맘춤
                }
                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
            // 카카오톡 없음 -> 바로 카카오계정
            else if (token != null) {
                viewModel.loginWithKakao(token.accessToken) // 로그인 성공시 호출함.
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                //Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
            }
        }
    } else {
        // 카카오톡 앱이 없는 경우
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
    buttonsEnabled: Boolean = true, // 중복 로그인 방지.

) {

    val colorTheme = MaterialTheme.linkuColors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthHelper = remember {
        GoogleAuthHelper(webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID)
    }

    //카카오 로그인 state 수집
    val kakaoLoginState by viewModel.kakaoLoginState.collectAsStateWithLifecycle()

    // 구글 로그인 state 수집
    val googleLoginState by viewModel.googleLoginState.collectAsStateWithLifecycle()

    LaunchedEffect(kakaoLoginState) {
        when (val state = kakaoLoginState) {
            is SocialAuthViewModel.SocialLoginState.Success -> {
                val result = state.result
                when (result.status) {
                    "ACTIVE" -> onLoginSuccess()  // 기존 유저 → 홈
                    "TEMP" -> {
                        navigator.navigate("social_login_gate")
                        // navigate 후 social_auth_graph의 savedStateHandle에 토큰 저장
                        navigator.getBackStackEntry("social_auth_graph")
                            .savedStateHandle["socialToken"] = result.accessToken
                    }
                }
                viewModel.resetKakaoLoginState() // 로그인 성공 후 -> 뒤로 가기시 재실행되는 중복 호출 문제 방지. rest 하면 idle로 돌아감.
            }

            is SocialAuthViewModel.SocialLoginState.Error -> {
                // TODO: 에러 메시지 표시
                viewModel.resetKakaoLoginState()
            }

            else -> {}
        }
    }
    // 구글 로그인 상태 처리 추가함.
    LaunchedEffect(googleLoginState) {
        when (val state = googleLoginState) {
            is SocialAuthViewModel.SocialLoginState.Success -> {
                val result = state.result
                when (result.status) {
                    "ACTIVE" -> onLoginSuccess()
                    "TEMP" -> {
                        navigator.navigate("social_login_gate")
                        navigator.getBackStackEntry("social_auth_graph")
                            .savedStateHandle["socialToken"] = result.accessToken
                    }
                }
                viewModel.resetGoogleLoginState()
            }

            is SocialAuthViewModel.SocialLoginState.Error -> {
                Log.e(
                    "GoogleLogin",
                    "구글 로그인 에러: ${(googleLoginState as SocialAuthViewModel.SocialLoginState.Error).message}"
                )
                viewModel.resetGoogleLoginState() // 리셋 추가.
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
            .background(brush = colorTheme.linearMainColor)
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
                        fontWeight = FontWeight(500),
                        color = colorTheme.white,
                        textAlign = TextAlign.Center
                    )

                    // 문구 간 25dp 간격
                    Spacer(modifier = Modifier.height(25.scaler))

                    Text(
                        text = "링큐에 오신 것을 \n환영해요",
                        fontSize = 22.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(700),
                        color = colorTheme.white,
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
            SocialLoginButton(
                backgroundColor = Color(0xFFFEE500), // 다크모드여도 그대로 유지되어야 하니 하드 코딩 유지
                iconRes = R.drawable.icon_login_kakao,
                text = "카카오로 시작하기",
                textColor = colorTheme.black,
                onClick = {
                    if (buttonsEnabled) handleKakaoLogin(context, viewModel)
                    //약관 화면에서 중복 로그인 방지.
                }
            )


            // 구글
            SocialLoginButton(
                backgroundColor = colorTheme.white,
                borderColor = Color(0xFFE0E0E0),
                iconRes = R.drawable.icon_login_google,
                text = "구글로 시작하기",
                textColor = colorTheme.black,
                onClick = {
                    if (!buttonsEnabled) return@SocialLoginButton
                    val activity = context.findActivity() ?: run {
                        Toast.makeText(context, "간편 로그인 실패. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                        return@SocialLoginButton
                    }
                    scope.launch {
                        try {
                            val idToken = googleAuthHelper.getGoogleIdToken(activity)
                            viewModel.loginWithGoogle(idToken)
                        } catch (e: Exception) {
                            Log.e("GoogleLogin", "구글 로그인 실패: ${e.message}")
                        }
                    }
                }
            )

            // 이메일 기존 그대로 유지.
            SocialLoginButton(
                backgroundColor = Color.Transparent,
                borderColor = colorTheme.white,
                iconRes = null,
                text = "이메일로 시작하기",
                textColor = colorTheme.white,
                onClick = {
                    //navigator.navigate("email_login")
                    if (buttonsEnabled) {
                        navigator.navigate("email_login")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()

    LinkuPreview {
        LoginScreen(
            navigator = navController,
            viewModel = viewModel() // 프리뷰용
        )
    }
}
