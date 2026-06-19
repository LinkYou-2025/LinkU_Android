package com.linku.login.ui.screen.email

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.StepIndicator
import com.linku.login.viewmodel.EmailAuthViewModel
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.EmailUiEffect
import com.linku.login.viewmodel.state.EmailUiEvent
import com.linku.login.viewmodel.state.EmailUiState
import java.util.Locale


@Composable
internal fun EmailVerificationScreen(
    onBackClick: () -> Unit,
    onNavigateToPassword: () -> Unit,
    viewModel: EmailAuthViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel
) {
    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current
    val emailUiState by viewModel.state.collectAsStateWithLifecycle()

    // 화면 진입 시 리셋
    LaunchedEffect(Unit) {
        viewModel.onEvent(EmailUiEvent.ClearStatus)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is EmailUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is EmailUiEffect.NavigateToPassword -> {
                    signUpViewModel.updateForm { it.copy(email = effect.verifiedEmail) }
                    onNavigateToPassword()
                }
            }
        }
    }

    EmailVerificationScreenContent(
        emailUiState = emailUiState,
        timerProvider = { emailUiState.timer },
        onEmailEvent = { viewModel.onEvent(it) }
    )
}


@Composable
internal fun EmailVerificationScreenContent(
    emailUiState: EmailUiState,
    timerProvider: () -> Int,
    onEmailEvent: (EmailUiEvent) -> Unit
) {
    val emailValid = remember(emailUiState.email) {
        emailUiState.email.length >= 5 && emailUiState.email.contains("@") &&
                Patterns.EMAIL_ADDRESS.matcher(emailUiState.email).matches()
    }

    val isButtonEnabled = if (emailUiState.isCodeSent) {
        emailUiState.code.length == 6 && !emailUiState.isLoading
    } else {
        emailValid && !emailUiState.isLoading
    }

    val colorTheme = MaterialTheme.linkuColors

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.scaler, end = 20.scaler, top = 60.scaler, bottom = 72.scaler),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(currentStep = 1)
            Spacer(modifier = Modifier.height(36.scaler))
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )
            Spacer(modifier = Modifier.height(32.scaler))

            if (!emailUiState.isCodeSent) {
                // 1단계: 이메일 입력 화면
                LoginTextField(
                    value = emailUiState.email,
                    onValueChange = { onEmailEvent(EmailUiEvent.EmailChanged(it)) },
                    hint = "이메일 주소를 입력해주세요",
                    enabled = !emailUiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )

                emailUiState.emailError?.let {
                    Spacer(modifier = Modifier.height(6.scaler))
                    Text(
                        text = it,
                        color = colorTheme.negative,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.offset(x = 4.scaler)
                    )
                }
            } else {
                // 2단계: 코드 입력 화면
                LoginTextField(
                    value = emailUiState.email,
                    onValueChange = {},
                    hint = "이메일 주소를 입력해주세요",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(10.scaler))

                OutlinedTextField(
                    value = emailUiState.code,
                    onValueChange = { onEmailEvent(EmailUiEvent.CodeChanged(it)) },
                    placeholder = {
                        Text("코드를 입력해주세요", fontSize = 14.sp, color = colorTheme.gray[400])
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.scaler)
                        .background(colorTheme.white, shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            brush = colorTheme.maincolor,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    enabled = !emailUiState.isLoading,
                    trailingIcon = { TimerText(timerProvider = timerProvider) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                emailUiState.codeError?.let {
                    Spacer(modifier = Modifier.height(12.scaler))
                    Text(
                        text = it,
                        color = colorTheme.negative,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 12.scaler)
                    )
                }
            }
        }

        // 하단 영역
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "인증번호가 오지 않는다면?",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colorTheme.gray[400],
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(bottom = 21.scaler)
                    .noRippleClickable {
                        if (!emailUiState.isLoading) {
                            onEmailEvent(EmailUiEvent.SendCodeClicked)
                        }
                    }
            )

            BottomGradientButton(
                text = if (emailUiState.isCodeSent) "인증하기" else "인증메일 발송",
                enabled = isButtonEnabled,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (emailUiState.isCodeSent) {
                        onEmailEvent(EmailUiEvent.VerifyCodeClicked)
                    } else {
                        onEmailEvent(EmailUiEvent.SendCodeClicked)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationScreen_TimerPreview() {
    LinkuPreview {
        EmailVerificationScreenContent(
            emailUiState = EmailUiState(
                email = "test@email.com",
                code = "123456",
                isCodeSent = true,
                timer = 180
            ),
            timerProvider = { 180 },
            onEmailEvent = {}
        )
    }
}

@Composable
private fun TimerText(timerProvider: () -> Int) {
    val seconds = timerProvider()
    val timerText = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60)
    Text(
        text = timerText,
        color = MaterialTheme.linkuColors.negative,
        fontSize = 13.sp,
        modifier = Modifier.padding(end = 12.dp)
    )
}
