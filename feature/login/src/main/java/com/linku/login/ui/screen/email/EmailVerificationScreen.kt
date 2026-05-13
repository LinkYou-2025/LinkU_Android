package com.linku.login.ui.screen.email

import android.util.Log
import android.util.Patterns
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.linku.core.model.auth.AuthErrorMessages
import com.linku.core.model.auth.EmailAuthState
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.StepIndicator
import com.linku.login.ui.model.EmailVerificationEvents
import com.linku.login.ui.model.EmailVerificationUiState
import com.linku.login.viewmodel.EmailAuthViewModel
import com.linku.login.viewmodel.SignUpViewModel
import java.util.Locale

/**
 * 이메일 인증 화면의 UI와 로직을 담당하는 화면임.
 * - 이메일 입력, 인증 코드 입력, 타이머, 버튼, 에러 처리, 네비게이션 등 모든 동작을 포함함.
 */

@Composable
fun EmailVerificationScreen(
    navigator: NavHostController,
    parentEntry: NavBackStackEntry,
    viewModel: EmailAuthViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {


    BackHandler {
        parentEntry.savedStateHandle["from_email_verification"] = true
        navigator.popBackStack()   // ← 이게 정답
    }

    // 일회성으로 진행하는 것이니 이건 굳이 SignUpViewModel 모델에 넣지 않는게 좋다고 판단했는데, 확인 부탁드립니다.
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }


    // 뷰모델 상태
    val authState by viewModel.authState.collectAsStateWithLifecycle()


    // 파생 상태로- 중복 제거.
    val emailValid = remember(email) {
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    val isCodeValid = code.length == 6
    val isSending = authState is EmailAuthState.Sending // 파생 상태
    val isVerifying = authState is EmailAuthState.Verifying // 파생 상태
    val isCodeSent by viewModel.isCodeSent.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()

    // 상태에 따라 파생 값 계산
    val sendResult: String? = when (val state = authState) {
        is EmailAuthState.SendSuccess -> state.message
        is EmailAuthState.SendError -> state.message
        else -> null
    }

    val verifyResult: String? = when (val state = authState) {
        is EmailAuthState.VerifySuccess -> "인증 성공"
        is EmailAuthState.VerifyError -> state.message
        else -> null
    }

    // 화면 진입 시 리셋
    LaunchedEffect(Unit) {
        viewModel.reset()  // 타이머 건드리지 않고 상태만 Idle로
    }


    // 상태 변화 감지
    LaunchedEffect(authState) {
        when (authState) {
            is EmailAuthState.SendSuccess -> {
                Log.d("EmailVerificationScreen", "인증 코드 전송 성공")
            }

            is EmailAuthState.SendError -> {
                Log.e(
                    "EmailVerificationScreen",
                    "전송 실패: ${(authState as EmailAuthState.SendError).message}"
                )
            }

            is EmailAuthState.VerifySuccess -> {
                Log.d("EmailVerificationScreen", "인증 성공")
                signUpViewModel.updateForm {
                    it.copy(email = email.trim())
                }
                navigator.navigate("sign_up_password")
            }

            is EmailAuthState.VerifyError -> {
                Log.e(
                    "EmailVerificationScreen",
                    "인증 실패: ${(authState as EmailAuthState.VerifyError).message}"
                )
            }

            else -> Unit
        }
    }


    EmailVerificationScreenContent(
        uiState = EmailVerificationUiState(
            email = email,
            code = code,
            isSending = isSending,
            isVerifying = isVerifying,
            sendResult = sendResult,
            verifyResult = verifyResult,
            isCodeSent = isCodeSent,
            isCodeValid = isCodeValid,
            emailValid = emailValid,
            timer = timer,
        ),
        events = EmailVerificationEvents(
            onEmailChange = { email = it },
            onCodeChange = { code = it },
            onSendCode = { viewModel.sendEmailCode(email.trim()) },
            onVerifyCode = { viewModel.verifyEmailCode(email.trim(), code.trim()) }
        )
    )
}


@Composable
fun EmailVerificationScreenContent(
    uiState: EmailVerificationUiState,
    events: EmailVerificationEvents,
) {
    val (email, code, isSending, isVerifying, sendResult, verifyResult, isCodeSent, isCodeValid, emailValid, timer) = uiState
    val (onEmailChange, onCodeChange, onSendCode, onVerifyCode) = events

    val colorTheme = MaterialTheme.linkuColors

    val isButtonEnabled = sendResult != AuthErrorMessages.SERVER_ERROR &&
            !isSending && !isVerifying &&
            (if (isCodeSent) isCodeValid else emailValid)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = (20.scaler),
                    end = (20.scaler),
                    top = (60.scaler),
                    bottom = (72.scaler)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(currentStep = 1)
            Spacer(modifier = Modifier.height((36.scaler)))
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )
            Spacer(modifier = Modifier.height((32.scaler)))

            when {
                // 1단계: 이메일 입력
                !isCodeSent -> {
                    LoginTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        hint = "이메일 주소를 입력해주세요",
                        enabled = !isSending,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val emailErrorText: String? = when {
                        email.isNotBlank() && !emailValid -> "이메일 양식이 올바르지 않습니다!"
                        sendResult == AuthErrorMessages.EMAIL_ALREADY_EXISTS -> AuthErrorMessages.EMAIL_ALREADY_EXISTS
                        sendResult == AuthErrorMessages.SERVER_ERROR -> "서버 오류: 잠시 후 다시 시도해주세요"
                        else -> null
                    }
                    emailErrorText?.let {
                        Spacer(modifier = Modifier.height((6.scaler)))
                        Text(
                            text = it,
                            color = colorTheme.negative,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.offset(x = (4.scaler))
                        )
                    }
                }

                // 2단계: 인증 코드 입력
                isCodeSent -> {
                    LoginTextField(
                        value = email,
                        onValueChange = {},
                        hint = "이메일 주소를 입력해주세요",
                        enabled = false, // 이메일 수정 불가
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height((10.scaler)))
                    OutlinedTextField(
                        value = code,
                        onValueChange = onCodeChange,
                        placeholder = {
                            Text(
                                "코드를 입력해주세요",
                                fontSize = 14.sp,
                                color = colorTheme.gray[400]
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((56.scaler))
                            .background(colorTheme.white, shape = RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                brush = colorTheme.maincolor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        enabled = !isVerifying,
                        trailingIcon = {
                            if (sendResult == AuthErrorMessages.SERVER_ERROR) {
                                Text(
                                    text = "잠시 후 다시 시도해주세요.",
                                    color = colorTheme.negative,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            } else {
                                TimerText(timer)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    verifyResult?.let {
                        if (it == AuthErrorMessages.VERIFY_FAILED || it == AuthErrorMessages.NETWORK_ERROR) {
                            Spacer(modifier = Modifier.height((12.scaler)))
                            Text(
                                text = it,
                                color = colorTheme.negative,
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight(400),
                                modifier = Modifier.padding(start = (12.scaler))
                            )
                        }
                    }
                }
            }
        }

        // 하단 고정 영역
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "인증번호가 오지 않는다면?",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = colorTheme.gray[400],
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(bottom = (21.scaler))
                    .noRippleClickable { }
            )

            BottomGradientButton(
                text = if (isCodeSent) "인증하기" else "인증메일 발송",
                enabled = isButtonEnabled,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (isCodeSent) onVerifyCode() else onSendCode()
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
            uiState = EmailVerificationUiState(
                email = "test@email.com",
                code = "123456",
                sendResult = "인증 코드 전송 성공",
                verifyResult = AuthErrorMessages.VERIFY_FAILED,
                isCodeSent = true,
                isCodeValid = true,
                emailValid = true,
                timer = 180,
            ),
            events = EmailVerificationEvents(
                onEmailChange = {},
                onCodeChange = {},
                onSendCode = {},
                onVerifyCode = {}
            )
        )
    }
}


@Composable
private fun TimerText(timer: Int) {
    val timerText = String.format(Locale.getDefault(), "%02d:%02d", timer / 60, timer % 60)
    Text(
        text = timerText,
        color = MaterialTheme.linkuColors.negative,
        fontSize = 13.sp,
        modifier = Modifier.padding(end = 12.dp)
    )
}
