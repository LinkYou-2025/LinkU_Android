package com.linku.login.ui.screen.email

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.linku.core.model.SystemBarMode
import com.linku.core.model.auth.SignUpState
import com.linku.core.system.SystemBarController
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.BuildConfig
import com.linku.login.R
import com.linku.login.viewmodel.SignUpViewModel

@Composable
internal fun WelcomeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel //null 불가.
) {

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


    //화면 진입 시 자동 회원가입 요청 하나라도 비면 회원가입 불가.
    LaunchedEffect(Unit) {


        //릴리즈 빌드에는 로그 찍히지 않음. 디버그로는 확인가능함. api 연동 확인용으로 일단 놓음.
        // TODO : 런칭 전 해당 로그 삭제.
        if (BuildConfig.DEBUG) {
            Log.d("WelcomeScreen", "=== 회원가입 폼 상태 ===")
            Log.d("WelcomeScreen", "email: ${signUpViewModel.uiState.value.signUpForm?.email}")
            Log.d(
                "WelcomeScreen",
                "nickname: ${signUpViewModel.uiState.value.signUpForm?.nickname}"
            )
            Log.d("WelcomeScreen", "gender: ${signUpViewModel.uiState.value.signUpForm?.gender}")
            Log.d("WelcomeScreen", "jobId: ${signUpViewModel.uiState.value.signUpForm?.jobId}")
            Log.d(
                "WelcomeScreen",
                "purposeList: ${signUpViewModel.uiState.value.signUpForm?.purposeList}"
            )
            Log.d(
                "WelcomeScreen",
                "interestList: ${signUpViewModel.uiState.value.signUpForm?.interestList}"
            )
            Log.d("WelcomeScreen", "=====================")
        }
        signUpViewModel.signUp()
    }

    // 서버 응답 감지
    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                Log.d("WelcomeScreen", "회원가입 성공")
                navigator.navigate("email_login") {
                    popUpTo("auth_graph") { inclusive = true }
                }

            }

            is SignUpState.Error -> {
                val message = (signUpState as SignUpState.Error).message
                Log.e("WelcomeScreen", "회원가입 실패: $message")
                // 에러 시 이전 단계로 돌아감.
                navigator.popBackStack()
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
                    .background(colorTheme.white, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인 하러가기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(brush = colorTheme.maincolor),
                )
            }
        }


    }
}

