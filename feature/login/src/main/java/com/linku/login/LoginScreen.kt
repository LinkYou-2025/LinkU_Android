package com.linku.login

//피그마에서 스플래쉬 다음으로 나오는 로그인 화면 입니다.


import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.linku.core.model.auth.LoginType
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.modal.ModalWindow
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.auth.GoogleAuthHelper
import com.linku.login.auth.findActivity
import com.linku.login.ui.item.SocialLoginButton
import com.linku.login.viewmodel.SocialAuthViewModel
import com.linku.login.viewmodel.state.SocialAuthUiEffect
import kotlinx.coroutines.CancellationException
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
            viewModel.notifySocialLoginFailed()
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
                // ClientErrorCause.Cancelled 외에도 AuthErrorCause.ACCESS_DENIED로 오는 경우가 있어 함께 취소로 처리함.
                val isUserCancelled = (error is ClientError && error.reason == ClientErrorCause.Cancelled) ||
                    (error is AuthError && error.reason == AuthErrorCause.AccessDenied)
                if (isUserCancelled) {
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
    onNavigateToEmailLogin: () -> Unit,
    onNavigateToSocialOnboarding: (accessToken: String, loginType: LoginType) -> Unit,
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

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // 소셜 로그인 실패 시 노출되는 커스텀 토스트 (딤 처리 없이 버튼 위에 떠 있는 검정 pill 형태).
    var toastMessage by remember { mutableStateOf("") }
    var isToastVisible by remember { mutableStateOf(false) }

//    //카카오 로그인 state 수집
//    val kakaoLoginState by viewModel.kakaoLoginState.collectAsStateWithLifecycle()
//
//    // 구글 로그인 state 수집
//    val googleLoginState by viewModel.googleLoginState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SocialAuthUiEffect.NavigateToHome -> {
                    onLoginSuccess()
                }

                is SocialAuthUiEffect.NavigateToAdditionalInfo -> {
                    Log.d(TAG, "MVI Effect 수령: TEMP 유저 -> 온보딩 위임 콜백 트리거")
                    onNavigateToSocialOnboarding(effect.loginResult.accessToken, effect.loginType)
                }

                is SocialAuthUiEffect.ShowToast -> {
                    toastMessage = effect.message
                    isToastVisible = true
                }

                else -> {}
            }
        }
    }
//
//    // 구글 로그인 상태 처리 추가함.
//    LaunchedEffect(googleLoginState) {
//        when (val state = googleLoginState) {
//            is SocialAuthViewModel.SocialLoginState.Success -> {
//                val result = state.result
//                when (result.status) {
//                    "ACTIVE" -> onLoginSuccess()
//                    "TEMP" -> {
//                        navigator.navigate("social_login_gate")
//                        navigator.getBackStackEntry("social_auth_graph")
//                            .savedStateHandle["socialToken"] = result.accessToken
//                    }
//                }
//                viewModel.resetGoogleLoginState()
//            }
//
//            is SocialAuthViewModel.SocialLoginState.Error -> {
//                Log.e(
//                    "GoogleLogin",
//                    "구글 로그인 에러: ${(googleLoginState as SocialAuthViewModel.SocialLoginState.Error).message}"
//                )
//                viewModel.resetGoogleLoginState() // 리셋 추가.
//            }
//
//            else -> {}
//        }
//    }

    // 시스템 바 숨김/복원은 MainScreen의 EdgeToEdgeSystemBars(hideSystemBars) 한 곳에서만 처리함
    // (LoginApp.kt의 "login"/"social_login_gate" 진입 시 edgeToEdgeSystemBars=true로 세팅함).

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
            SocialLoginButtonWithRecentBadge(
                type = LoginType.KAKAO,
                recentLoginType = uiState.recentLoginType,
                onClick = {
                    if (buttonsEnabled) handleKakaoLogin(context, viewModel)
                    //약관 화면에서 중복 로그인 방지.
                }
            )


            // 구글
            SocialLoginButtonWithRecentBadge(
                type = LoginType.GOOGLE,
                recentLoginType = uiState.recentLoginType,
                onClick = {
                    if (!buttonsEnabled) return@SocialLoginButtonWithRecentBadge
                    val activity = context.findActivity() ?: run {
                        viewModel.notifySocialLoginFailed()
                        return@SocialLoginButtonWithRecentBadge
                    }
                    scope.launch {
                        try {
                            val idToken = googleAuthHelper.getGoogleIdToken(activity)
                            viewModel.loginWithGoogle(idToken)
                        } catch (e: CancellationException) {
                            throw e  // 화면 이탈 등으로 인한 정상 취소, 에러 아님
                        } catch (e: Exception) {
                            Log.e("GoogleLogin", "구글 로그인 실패: ${e.message}")
                            viewModel.notifySocialLoginFailed()
                        }
                    }
                }
            )

            // 이메일 기존 그대로 유지.
            SocialLoginButtonWithRecentBadge(
                type = LoginType.EMAIL,
                recentLoginType = uiState.recentLoginType,
                onClick = {
                    //navigator.navigate("email_login")
                    if (buttonsEnabled) {
                        onNavigateToEmailLogin()
                    }
                }
            )
        }

        // 소셜 로그인 실패 시 노출되는 딤 처리 + 토스트. 토스트가 떠 있는 동안 뒤쪽 버튼 탭을 막기 위해 스크림에 클릭도 소비함.
        if (isToastVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorTheme.black.copy(alpha = 0.5f))
                    .clickable(enabled = true, onClick = {})
            )
        }

        TimedCustomToastMessage(
            visible = isToastVisible,
            toastMessage = toastMessage,
            onDismiss = { isToastVisible = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomPadding + 50.scaler + 12.scaler)
        )

        // 탈퇴 유예기간(INACTIVE) 계정으로 소셜 로그인 시도 시 노출되는 복구 확인 모달
        ModalWindow(
            visible = uiState.showRecoverModal,
            onOkay = { viewModel.recoverAccount() },
            onNegativeClick = { viewModel.keepWithdrawn() },
            onDismiss = { viewModel.dismissRecoverModal() },
            positiveText = "계정 복구",
            negativeText = "탈퇴 유지",
            title = "탈퇴 처리 중인 계정입니다.",
            isLogoDimmed = true
        ) {
            Text(
                text = "지금 로그인하면 계정이 즉시 복구됩니다.\n14일이 지나면 모든 정보가 삭제돼요.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(400),
                color = colorTheme.gray[600],
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 소셜 로그인 버튼 위에 마지막으로 로그인했던 수단을 알려주는 "최근 로그인" 말풍선을 겹쳐 보여줍니다.
 *
 * @param type 이 버튼이 나타내는 로그인 수단.
 * @param recentLoginType 사용자가 마지막으로 로그인했던 수단. [type]과 일치할 때만 말풍선이 노출됩니다.
 */
@Composable
private fun SocialLoginButtonWithRecentBadge(
    type: LoginType,
    recentLoginType: LoginType,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SocialLoginButton(type = type, onClick = onClick)

        if (recentLoginType == type) {
            Image(
                painter = painterResource(id = R.drawable.img_recent_login),
                contentDescription = "최근 로그인",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-7).scaler, y = (-20).scaler)
                    .width(100.scaler)
                    .height(42.scaler)
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6)
@Composable
fun LoginScreenPreview() {
    LinkuPreview {
        LoginScreen(
            onNavigateToEmailLogin = {},
            onNavigateToSocialOnboarding = { _, _ -> },
            viewModel = viewModel()
        )
    }
}
