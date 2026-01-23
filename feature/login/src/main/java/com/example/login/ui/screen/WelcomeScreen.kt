package com.example.login.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.design.theme.font.Paperlogy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler
import com.example.login.viewmodel.SignUpViewModel
import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun WelcomeScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel? = null
) {
    //디자인 모듈 가져오기.
    val colorTheme = LocalColorTheme.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    //여기만 안드로이드 자체 바텀바 컬러 변경
    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? Activity
            ?: return@DisposableEffect onDispose { }
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, view)

        //  기존 값 백업
        val originalNavColor = window.navigationBarColor
        val originalAppearance = insetsController.isAppearanceLightNavigationBars

        // WelcomeScreen 진입 시 적용
        window.navigationBarColor = Color(0xFFC800FF).toArgb()
        insetsController.isAppearanceLightNavigationBars = false // 아이콘 흰색

        onDispose {
            //  WelcomeScreen 벗어날 때 원복
            window.navigationBarColor = originalNavColor
            insetsController.isAppearanceLightNavigationBars = originalAppearance
        }
    }

    // 뒤로가기 막기
    BackHandler {
        // 아무것도 하지 않음 → 뒤로가기 무시됨 -> 아예 이전 화원가입 했던 화면들 돌아갈 수 없음!
    }
    //  signUpSuccess(Boolean?) 사용
    val signUpSuccess by signUpViewModel?.signUpSuccess?.collectAsState() ?: remember {
        mutableStateOf<Boolean?>(null)
    }
    //val signUpSuccess by signUpViewModel.signUpSuccess.collectAsState()
    var isSignUpRequested by remember { mutableStateOf(false) } //중복 호출 방자용 상태 추가


    // 서버 응답 감지
    LaunchedEffect(signUpSuccess) {
        when (signUpSuccess) {
            true -> {
                Log.d("WelcomeScreen", " 회원가입 성공")
                //navigator.navigate("home")  // 회원가입 후 홈으로 이동
                // 수정 이전 화원가입으로 갈 수 없음. 백버튼을 아무리 눌러도 작동 안함.
                navigator.navigate("email_login") {
                    popUpTo("auth_graph") { inclusive = true }
                }
                isSignUpRequested = false
            }

            false -> {
                Log.e("WelcomeScreen", "회원가입 실패")
                isSignUpRequested = false //  실패 시 버튼 다시 활성화
            }
            null -> {} // 아직 응답 없음

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
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC800FF), // 위
                        Color(0xFF2C6FFF)  // 아래
                    )
                )
            )
    ) {
        // 중앙 콘텐츠 (Column)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 로고 위치 (394/917)
            Spacer(modifier = Modifier.height((394.scaler)))
            Image(
                painter = painterResource(id = R.drawable.img_logo_white),
                contentDescription = "Logo",
                Modifier
                    .offset(x = (160.scaler) - (configuration.screenWidthDp.dp / 2) + (46.scaler)) // 시작 너비 보정
                    .width((92.scaler))
                    .height((65.scaler)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height((20.scaler)))

            Text(
                text = "링큐에 오신 걸 환영해요!",
                color = colorTheme.white,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy.font,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height((16.scaler)))

            Text(
                text = "당신을 위한 링크, 링큐가 기억하고 연결해줄게요!",
                color = colorTheme.white,
                fontSize = 16.sp,
                fontFamily = Paperlogy.font,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center

            )
        }
        // 버튼을 Box의 직접 자식으로 두고, 하단 정렬
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = (20.scaler), end = (20.scaler), bottom = bottomPadding)
                .height((50.scaler))
                .background(
                    Color.White,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = !isSignUpRequested) {
                    if (!isSignUpRequested) {
                        isSignUpRequested = true
                        Log.d("WelcomeScreen", "회원가입 API 호출 시도")
                        signUpViewModel?.signUp()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "로그인 하러가기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(brush = colorTheme.maincolor),
                fontFamily = Paperlogy.font
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