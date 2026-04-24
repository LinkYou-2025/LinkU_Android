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
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.auth.AuthErrorMessages
import com.linku.core.model.auth.EmailAuthState
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.StepIndicator
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
    val authState by viewModel.authState.collectAsState()
    val isCodeSent by viewModel.isCodeSent.collectAsState()


    // 파생 상태로- 중복 제거.
    val emailValid = remember(email) {
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    val isCodeValid = code.length == 6
    val isSending = authState is EmailAuthState.Sending // 파생 상태
    val isVerifying = authState is EmailAuthState.Verifying // 파생 상태


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
        viewModel.resetAll()
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


    // UI만 그리는 프레젠테이션 컴포저블에 상태/이벤트를 위임 -> 렌더 이슈로 부득이하게 프리뷰 구현을 위해 코드 추가.
    EmailVerificationScreenContent(
        navigator = navigator,
        email = email,
        onEmailChange = { email = it },
        code = code,
        onCodeChange = { code = it },
        isSending = isSending,
        isVerifying = isVerifying,
        sendResult = sendResult,
        verifyResult = verifyResult,
        isCodeSent = isCodeSent,
        isCodeValid = isCodeValid,
        emailValid = emailValid,
        onSendCode = { viewModel.sendEmailCode(email.trim()) },
        onVerifyCode = { viewModel.verifyEmailCode(email.trim(), code.trim()) }
    )
}

/**
 * UI만 그리는 프레젠테이션 컴포저블입니다.
 * Preview에서 ViewModel 없이 안전하게 사용 가능.
 * 여기 이메일 인증에서는  """ui"""만 담당합니다
 */
@Composable
fun EmailVerificationScreenContent(
    navigator: NavHostController,
    email: String,
    onEmailChange: (String) -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    isSending: Boolean,
    isVerifying: Boolean,
    sendResult: String?,
    verifyResult: String?,
    isCodeSent: Boolean,
    isCodeValid: Boolean,
    emailValid: Boolean,
    onSendCode: () -> Unit,
    onVerifyCode: () -> Unit
) {

    val colorTheme = MaterialTheme.linkuColors

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
            //1단계
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )
            Spacer(modifier = Modifier.height((36.scaler)))
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )
            Spacer(modifier = Modifier.height((32.scaler)))
            // 이메일 입력 필드

            //이메일 입력 필드
            LoginTextField(
                value = email,
                onValueChange = onEmailChange,
                hint = "이메일 주소를 입력해주세요",
                enabled = !isCodeSent, // 인증번호 발송 후엔 수정 불가. //enabled = true
                modifier = Modifier.fillMaxWidth(),
            )

            // 에러 문구
            val emailErrorText: String? = when {
                email.isNotBlank() && !emailValid -> "이메일 양식이 올바르지 않습니다!"
                sendResult == AuthErrorMessages.EMAIL_ALREADY_EXISTS -> AuthErrorMessages.EMAIL_ALREADY_EXISTS
                else -> null
            }
            emailErrorText?.let {
                Spacer(modifier = Modifier.height((6.scaler)))
                Text(
                    text = it,
                    color = colorTheme.negative,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(
                        x = (4.scaler)
                    )

                )
            }
            // 인증 코드 입력 영역 이건 타이머가 있어서 따로 요소 불러오지 않고 여기서만.
            if (isCodeSent) {
                Spacer(modifier = Modifier.height((10.scaler)))
                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    placeholder = {
                        Text(
                            "코드를 입력해주세요",
                            fontSize = 14.sp,
                            fontFamily = Paperlogy.font,
                            color = colorTheme.gray[400]!!
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
                            TimerText()  // 여기만 리컴포지션
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                when (verifyResult) {
                    AuthErrorMessages.VERIFY_FAILED,
                    AuthErrorMessages.NETWORK_ERROR -> {
                        Spacer(modifier = Modifier.height((12.scaler)))
                        Text(
                            text = verifyResult,
                            color = colorTheme.negative,
                            fontSize = 13.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(400),
                            modifier = Modifier.padding(start = (12.scaler))
                        )
                    }

                    else -> Unit
                }

            } else if (sendResult == AuthErrorMessages.SERVER_ERROR) {
                Spacer(modifier = Modifier.height((8.scaler)))
                Text(
                    text = "서버 오류: 잠시 후 다시 시도해주세요",
                    color = colorTheme.negative,
                    fontSize = 13.sp,
                    modifier = Modifier.padding((8.scaler))
                )
            }
        }
        // 하단 고정 영역
        val isButtonEnabled = sendResult != AuthErrorMessages.SERVER_ERROR &&
                !isSending && !isVerifying &&
                (if (isCodeSent) isCodeValid else emailValid)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 인증번호 안내 텍스트 (버튼 위 21dp)
            Text(
                text = "인증번호가 오지 않는다면?",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = colorTheme.gray[400]!!,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(
                        bottom = (21.scaler)
                    )
                    .noRippleClickable {
                        // TODO: 재전송 안내 api 개발시 연동하기!
                    }
            )

            BottomGradientButton(
                text = if (isCodeSent) "인증하기" else "인증메일 발송",
                enabled = isButtonEnabled,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = {
                    if (isCodeSent) {
                        onVerifyCode()
                    } else {
                        onSendCode()
                    }
                }
            )
        }
    }
}

/**
 * Preview에서는 ViewModel 없이 더미 상태만 넘겨서 프리뷰는 잘 표시될 수 있도록 함..
 */
@Preview(showBackground = true)
@Composable
fun EmailVerificationScreenPreview() {
    val fakeNavigator = rememberNavController()
    LinkuPreview {
        EmailVerificationScreenContent(
            navigator = fakeNavigator,
            email = "",
            onEmailChange = {},
            code = "",
            onCodeChange = {},
            isSending = false,
            isVerifying = false,
            sendResult = null,
            verifyResult = null,
            isCodeSent = false,
            isCodeValid = false,
            emailValid = true,
            onSendCode = {},
            onVerifyCode = {}
        )
    }
}


//타이머  보려고
@Preview(showBackground = true)
@Composable
fun EmailVerificationScreen_TimerPreview() {
    val fakeNavigator = rememberNavController()

    LinkuPreview {
        EmailVerificationScreenContent(
            navigator = fakeNavigator,
            email = "test@email.com",
            onEmailChange = {},
            code = "123456",
            onCodeChange = {},
            isSending = false,
            isVerifying = false,
            sendResult = "인증 코드 전송 성공",
            verifyResult = AuthErrorMessages.VERIFY_FAILED,
            isCodeSent = true,
            isCodeValid = true,
            emailValid = true,
            onSendCode = {},
            onVerifyCode = {}
        )
    }
}

// 타이머 별도 컴포저블 분리 - 타이머로 인한 과도한 리컴포지션 방지
@Composable
private fun TimerText() {
    val viewModel: EmailAuthViewModel = hiltViewModel()
    val timer by viewModel.timer.collectAsState()
    val timerText = String.format(Locale.getDefault(), "%02d:%02d", timer / 60, timer % 60)
    Text(
        text = timerText,
        color = MaterialTheme.linkuColors.negative,
        fontSize = 13.sp,
        modifier = Modifier.padding(end = 12.dp)
    )
}
