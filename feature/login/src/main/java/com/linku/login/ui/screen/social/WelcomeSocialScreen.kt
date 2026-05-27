package com.linku.login.ui.screen.social

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.SystemBarMode
import com.linku.core.model.auth.SignUpState
import com.linku.core.system.SystemBarController
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R
import com.linku.login.viewmodel.SignUpViewModel

@Composable
internal fun WelcomeSocialScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel? = null,
    onLoginSuccess: () -> Unit = {}
) {
    //디자인 모듈 가져오기.
    val colorTheme = MaterialTheme.linkuColors
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    //  시스템 바 숨기기 설정
    val systemBarController = LocalContext.current as? SystemBarController
    val isPreview = LocalInspectionMode.current

    DisposableEffect(Unit) {
        if (!isPreview && systemBarController != null) {
            systemBarController.setSystemBarMode(SystemBarMode.HIDDEN)
        }
        onDispose {
            // WelcomeScreen을 떠날 때 다시 바텀바를 보여줌
            if (!isPreview && systemBarController != null) {
                systemBarController.setSystemBarMode(SystemBarMode.VISIBLE)
            }
        }
    }

    // 뒤로가기 막기
    BackHandler {
        // 아무것도 하지 않음 → 뒤로가기 무시됨 -> 아예 이전 화원가입 했던 화면들 돌아갈 수 없음!
    }
    //  signUpState 사용
    val signUpState by signUpViewModel?.signUpState?.collectAsStateWithLifecycle() ?: remember {
        mutableStateOf(SignUpState.Idle)
    }
    //val signUpSuccess by signUpViewModel.signUpSuccess.collectAsStateWithLifecycle()
    var isSignUpRequested by remember { mutableStateOf(false) } //중복 호출 방자용 상태 추가

    //화면 진입 시 자동 회원가입 요청
    LaunchedEffect(Unit) {// Unit은 절대 안 바뀜. 그렇지만 이게 맞는 설계이니
        if (!isSignUpRequested) {
            isSignUpRequested = true
            Log.d("WelcomeScreen", "Welcome 진입 → 회원가입 자동 요청")
            signUpViewModel?.signUp()
        }
    }

    // 서버 응답 감지
    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                Log.d("WelcomeScreen", "회원가입 성공")
                onLoginSuccess()  //  MainApp의 홈 이동 로직 호출
                navigator.navigate("email_login") {
                    popUpTo("auth_graph") { inclusive = true }
                }
                isSignUpRequested = false
            }
            // 재시도 문제가 있음. 사용자에게 회원가입 시패시 안내를 하고 재시도를 하도록 해야함.
            is SignUpState.Error -> {
                val message = (signUpState as SignUpState.Error).message
                Log.e("WelcomeScreen", "회원가입 실패: $message")
                isSignUpRequested = false
            }

            is SignUpState.Loading -> {
                Log.d("WelcomeScreen", "회원가입 진행 중...")
            }

            is SignUpState.Idle -> {
                // 초기 상태
            }
        }
    }

    // 하단 동적 패딩 계산 로직 -> 기존 회원가입 바텀 그라데이션 버튼과 동일하게 작동함.
    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBottom = WindowInsets.navigationBars.getBottom(density)
    val screenHeight = configuration.screenHeightDp.dp

    val bottomPadding = when {
        imeBottom > 0 -> 20.dp
        navBottom > 0 -> screenHeight * (16f / 917f)
        else -> screenHeight * (24f / 917f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                colorTheme.verticalMainColor
            )
    ) {
        // 중앙 콘텐츠 (Column)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 중앙 콘텐츠 레이어 (로고 및 텍스트)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter) // 상단 기준 정렬 후 offset으로 세밀하게 이동
                    .offset(y = (configuration.screenHeightDp.dp * (394f / 917f)) - (65.scaler / 2)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_logo_white),
                    contentDescription = "Logo",
                    modifier = Modifier
                        // X축 오프셋은 기존 계산식 유지
                        .offset(x = (160.scaler) - (configuration.screenWidthDp.dp / 2) + (46.scaler))
                        .width(92.scaler)
                        .height(65.scaler),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(20.scaler))

                Text(
                    text = "링큐에 오신 걸 환영해요!",
                    color = colorTheme.white,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center // 피그마와 동일하게 중앙 정렬
                )

                Spacer(modifier = Modifier.height(16.scaler))

                Text(
                    text = "당신을 위한 링크, 링큐가 기억하고 연결해줄게요!",
                    color = colorTheme.white,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // 2. 하단 버튼 레이어
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.scaler, end = 20.scaler, bottom = bottomPadding)
                    .height(50.scaler)
                    .background(colorTheme.white, shape = RoundedCornerShape(18.dp))
                    .clickable {
                        when (signUpState) {
                            is SignUpState.Error -> {
                                // 실패 시 재시도
                                isSignUpRequested = false
                                signUpViewModel?.signUp()
                            }

                            is SignUpState.Success -> onLoginSuccess()
                            else -> {} // Loading 중엔 무시
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                //  상태에 따라 버튼 내용 변경
                when (signUpState) {
                    is SignUpState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorTheme.accentColor,
                        strokeWidth = 2.dp
                    )

                    is SignUpState.Error -> Text(
                        text = "다시 시도하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(brush = colorTheme.maincolor),
                        fontFamily = Paperlogy.font
                    )

                    else -> Text(
                        text = "홈으로 이동하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(brush = colorTheme.maincolor),
                        fontFamily = Paperlogy.font
                    )
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val fakeNavController = rememberNavController()
    LinkuPreview {
        WelcomeSocialScreen(navigator = fakeNavController)
    }
}

