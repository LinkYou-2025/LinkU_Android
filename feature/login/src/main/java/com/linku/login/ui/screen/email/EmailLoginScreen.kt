package com.linku.login.ui.screen.email

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.SystemBarMode
import com.linku.core.model.auth.LoginState
import com.linku.core.system.SystemBarController
import com.linku.design.component.GradientButtonCore
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.PasswordLoginTextField
import com.linku.login.viewmodel.LoginViewModel
import com.linku.login.viewmodel.state.LoginUiState

@Composable
fun EmailLoginScreen(
    loginViewModel: LoginViewModel? = null,
    onSignUpClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit = {}
) {

    val focusManager = LocalFocusManager.current
    val colorTheme = MaterialTheme.linkuColors
    val density = LocalDensity.current

    val uiState by loginViewModel?.state?.collectAsStateWithLifecycle()
        ?: androidx.compose.runtime.remember { mutableStateOf(LoginUiState()) }
    val loginState = uiState.loginState

    val windowInfo = LocalWindowInfo.current
    val containerSize = windowInfo.containerSize
    val containerWidthDp = with(density) { containerSize.width.toDp() }
    val containerHeightDp = with(density) { containerSize.height.toDp() }

    val systemBarController = LocalContext.current as? SystemBarController
    val isPreview = LocalInspectionMode.current

    // 로그인 입력 화면 진입 시 시스템 바 복구
    // (색상/아이콘 밝기는 MainScreen이 edgeToEdgeSystemBars 상태를 보고 공통 처리함)
    DisposableEffect(Unit) {
        if (!isPreview && systemBarController != null) {
            systemBarController.setSystemBarMode(SystemBarMode.VISIBLE)
        }
        onDispose {}
    }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()
    val isFormValid = uiState.email.isNotBlank() && uiState.password.isNotBlank() && isEmailValid

    val isInPreview = LocalInspectionMode.current
    val imeBottom = if (isInPreview) 0 else WindowInsets.ime.getBottom(density)
    val isKeyboardOpen = imeBottom > 0
    val buttonOffsetY = if (isKeyboardOpen) 0.dp else (-4.scaler)


    val logoRatio = if (isKeyboardOpen) 102f / 917f else 262f / 917f //키보드 활성화 전, 후
    val logoTopPadding = containerHeightDp * logoRatio

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white)
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
                    fontWeight = FontWeight(400),
                    color = colorTheme.gray[600],
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
                    value = uiState.email,
                    onValueChange = { loginViewModel?.onEmailChanged(it) },
                    hint = "이메일"
                )

                Spacer(Modifier.height((10.scaler)))

                PasswordLoginTextField(
                    value = uiState.password,
                    onValueChange = { loginViewModel?.onPasswordChanged(it) }
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
                                fontWeight = FontWeight(400),
                                color = colorTheme.negative
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
                    onClick = { loginViewModel?.login() }
                )
            }

            Spacer(Modifier.height((20.scaler)))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height((30.scaler)), // 클릭 영역 확보를 위한 높이
                horizontalArrangement = Arrangement.spacedBy(
                    25.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "비밀번호 재설정",
                    fontSize = 15.sp,
                    color = colorTheme.gray[600],
                    modifier = Modifier
                        .noRippleClickable {
                            onResetPasswordClick()
                            //loginViewModel?.onResetPasswordClicked()
                        }
                )
                Text(
                    text = "|",
                    fontSize = 14.sp,
                    color = colorTheme.gray[600],
                    style = TextStyle(
                        baselineShift = BaselineShift(0.3f)  // 약간 위로 올림
                    )
                )
                Text(
                    text = "회원가입",
                    fontSize = 15.sp,
                    color = colorTheme.gray[600],
                    modifier = Modifier
                        .noRippleClickable {
                            focusManager.clearFocus()
                            onSignUpClick()
                            //loginViewModel?.onSignUpClicked()
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
    LinkuPreview {
        EmailLoginScreen(
            loginViewModel = null,
            onSignUpClick = {},
            onResetPasswordClick = {},
            onLoginSuccess = {}
        )
    }
}




