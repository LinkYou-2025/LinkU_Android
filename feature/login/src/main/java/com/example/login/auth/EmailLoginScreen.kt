package com.example.login.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy
import com.example.login.ui.item.BottomGradientButton
import com.example.login.ui.item.LoginTextField
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import com.example.login.Paperlogy
import com.example.login.ui.item.GradientButtonCore
import com.example.login.ui.item.PasswordLoginTextField
import com.example.design.modifier.noRippleClickable
import com.example.login.ui.bottom_sheet.TermsAgreementSheet

@Composable
fun EmailLoginScreen(
    navigator: NavHostController,
    loginViewModel: LoginViewModel? = null,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailValid =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    // 🔑 화면 높이
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 🔑 키보드 상태 (프리뷰 안전)
    val density = LocalDensity.current
    val isInPreview = LocalInspectionMode.current
    val imeBottom = if (isInPreview) 0 else WindowInsets.ime.getBottom(density)
    val isKeyboardOpen = imeBottom > 0
    val buttonOffsetY = if (isKeyboardOpen) 0.dp else (-4).dp

    // 🔑 BottomGradientButton 내부 padding과 동일한 값 계산
    val navBottom = WindowInsets.navigationBars.getBottom(density)

    val buttonInnerPadding = when {
        imeBottom > 0 -> 20.dp   // 키보드 열림
        navBottom > 0 -> 16.dp   // 네비게이션 바 있음
        else -> 24.dp           // 풀스크린
    }

    // 🔑 피그마 비율 적용
    val logoRatio = if (isKeyboardOpen) 126f / 917f else 262f / 917f //키보드 활성화 전, 후
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
                    .width(84.62123.dp)
                    .height(60.00009.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Link U, Think You",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF87898F),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🔹 입력 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "이메일"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PasswordLoginTextField(
                    value = password,
                    onValueChange = { password = it }
                )
            }

            Spacer(modifier = Modifier.height(45.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = buttonOffsetY)
            ) {
                GradientButtonCore(
                    text = "로그인하기",
                    enabled = isFormValid,
                    activeGradient = listOf(
                        Color(0xFF2C6FFF),
                        Color(0xFFC800FF)
                    ),
                    inactiveGradient = listOf(
                        Color(0xFF9BCBFF),
                        Color(0xFFF4AFFF)
                    ),
                    onClick = {
                        loginViewModel?.login(
                            email.trim(),
                            password.trim()
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            // 🔑 화면 너비
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val resetStartPadding = screenWidth * (101f / 412f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(start = resetStartPadding)
                ) {
                    Text(
                        text = "비밀번호 재설정",
                        fontSize = 15.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF87898F),
                        modifier = Modifier
                            .alignByBaseline()
                            .noRippleClickable {
                                navigator.navigate("resetPassword")
                            }
                    )

                    Spacer(modifier = Modifier.width(25.dp))

                    Text(
                        text = "|",
                        fontSize = 14.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF87898F),
                        style = TextStyle(
                            baselineShift = BaselineShift(0.15f)
                        ),
                        modifier = Modifier.alignByBaseline()
                    )
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_divider_vertical),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .height(12.dp)
//                            .alignBy { it.measuredHeight / 2 } //깨짐.
//                    )

                    Spacer(modifier = Modifier.width(25.dp))

                    Text(
                        text = "회원가입",
                        fontSize = 15.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF87898F),
                        modifier = Modifier
                            .alignByBaseline()
                            .noRippleClickable {
                                onSignUpClick()
                            }
                    )
                }
            }
        }
    }
}




//@Preview(showBackground = true, name = "Login - Keyboard OFF")
//@Composable
//fun EmailLoginPreview_NoKeyboard() {
//    EmailLoginScreen(
//        navigator = rememberNavController()
//    )
//}





