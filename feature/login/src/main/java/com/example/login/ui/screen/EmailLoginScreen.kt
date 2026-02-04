package com.example.login.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.login.R
import com.example.design.theme.font.Paperlogy
import com.example.login.ui.item.LoginTextField
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import com.example.login.ui.item.GradientButtonCore
import com.example.login.ui.item.PasswordLoginTextField
import com.example.design.modifier.noRippleClickable
import android.util.Patterns
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.LocalColorTheme
import com.example.design.util.DesignSystemBars
import com.example.login.viewmodel.LoginViewModel
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.SystemBarMode
import com.example.core.system.SystemBarController
import com.example.login.viewmodel.LoginState
import com.example.login.viewmodel.LoginErrorType

@Composable
fun EmailLoginScreen(
    navigator: NavHostController,
    loginViewModel: LoginViewModel? = null,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit = {}
) {

    // 1. 키보드 제어를 위한 FocusManager 가져오기
    val focusManager = LocalFocusManager.current

    // 2. 디자인 모듈의 폰트 패밀리 가져오기
    val colorTheme = LocalColorTheme.current

    // LoginState 관찰 추가
    val loginState by loginViewModel?.loginState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(LoginState.Idle) }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                focusManager.clearFocus()
                onLoginSuccess()
            }
            is LoginState.Error -> {
                focusManager.clearFocus()
            }
            else -> {}
        }
    }

    val systemBarController =
        LocalContext.current as? SystemBarController
    val isPreview = LocalInspectionMode.current

    // 로그인 입력 화면부터는 시스템 바 다시 표시
    if (!isPreview && systemBarController != null) {
        DisposableEffect(systemBarController) {
            systemBarController.setSystemBarMode(SystemBarMode.VISIBLE)
            onDispose { }
        }
    }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailValid =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    // 🔑 화면 높이
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 🔑 키보드 상태 (프리뷰 안전)
    val density = LocalDensity.current
    val isInPreview = LocalInspectionMode.current
    val imeBottom = if (isInPreview) 0 else WindowInsets.ime.getBottom(density)
    val isKeyboardOpen = imeBottom > 0
    val buttonOffsetY = if (isKeyboardOpen) 0.dp else (-4.scaler)



    // 🔑 피그마 비율 적용
    val logoRatio = if (isKeyboardOpen) 102f / 917f else 262f / 917f //키보드 활성화 전, 후
    val logoTopPadding = screenHeight * logoRatio

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //  로고 위치 (비율 기반, 전체가 같이 이동)
            Spacer(modifier = Modifier.height(logoTopPadding))

            Image(
                painter = painterResource(id = R.drawable.ic_logo_color),
                contentDescription = "LinkU Logo",
                modifier = Modifier
                    .width((84.scaler))
                    .height((60.scaler)),
                contentScale = ContentScale.Fit
            )


            Spacer(Modifier.height((8.scaler)))

            Text(
                text = "Link U, Think You",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(400),
                    color = colorTheme.gray[600]!!,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height((40.scaler)))

            // 입력 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20.scaler)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        // 입력 시 에러 초기화.
                        if (loginState is LoginState.Error) {
                            loginViewModel?.clearError()
                        } },
                    hint = "이메일",
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontFamily = Paperlogy.font,
                        fontWeight = FontWeight(500),
                        color = colorTheme.black
                    )
                )

                Spacer(Modifier.height((10.scaler)))

                PasswordLoginTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        // 입력 시 에러 초기화.
                        if (loginState is LoginState.Error) {
                            loginViewModel?.clearError()
                        }
                    }
                )

                // 에러 메시지 추가
                if (loginState is LoginState.Error) {
                    Spacer(Modifier.height(12.scaler))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = (loginState as LoginState.Error).errorType.message,
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontFamily = Paperlogy.font,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFF5E5E)
                            ),
                            modifier = Modifier.padding(start = 22.scaler)  // 오른쪽으로 22만큼
                        )
                    }
                }
            }

            Spacer(Modifier.height((45.scaler)))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20.scaler))
                    .offset(y = buttonOffsetY)
            ) {
                GradientButtonCore(
                    text = "로그인하기",
                    enabled = isFormValid && loginState !is LoginState.Loading, //로딩 중 비활성화.
                    activeGradient = colorTheme.maincolor,
                    inactiveGradient = colorTheme.inactiveColor,
                    onClick = {
                        //focusManager.clearFocus() //키보드 내리기 필요하다면 사용하기.
                        loginViewModel?.login(
                            email.trim(),
                            password.trim()
                        )
                    }
                )
            }

            Spacer(Modifier.height((20.scaler)))


            // 🔑 비율 기반 가로 위치 계산
            // 디자인 기준 너비 412 대비 현재 화면의 비율 지점
            val resetStartPos = (101.scaler)   // 비밀번호 재설정 시작점
            val dividerStartPos = (220.scaler) // | 시작점
            val signUpStartPos = (247.scaler)  // 회원가입 시작점

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height((30.scaler)), // 클릭 영역 확보를 위한 높이
                horizontalArrangement = Arrangement.spacedBy(25.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "비밀번호 재설정",
                    fontSize = 15.sp,
                    fontFamily = Paperlogy.font,
                    color = Color(0xFF87898F),
                    modifier = Modifier
                        .noRippleClickable {
                            //if (loginState !is LoginState.Loading) { -> 혹시 나중에 로딩중이 길어지면 사용해주세요.
                            navigator.navigate("resetPassword")
                            //}
                        }
                )
                Text(
                    text = "|",
                    fontSize = 14.sp,
                    fontFamily = Paperlogy.font,
                    color = Color(0xFF87898F),
                    style = TextStyle(
                        baselineShift = BaselineShift(0.3f)  // 약간 위로 올림
                    )
                )
                Text(
                    text = "회원가입",
                    fontSize = 15.sp,
                    fontFamily = Paperlogy.font,
                    color = Color(0xFF87898F),
                    modifier = Modifier
                        .noRippleClickable {
                            focusManager.clearFocus()
                            onSignUpClick()
                        }
                )
            }
        }
    }
}



@Preview(
    name = "Email Login – Keyboard OFF",
    showBackground = true
)
@Composable
fun EmailLoginPreview() {
    EmailLoginScreen(
        navigator = rememberNavController(),
        loginViewModel = null,
        onSignUpClick = {},
        onLoginSuccess = {}

    )
}




