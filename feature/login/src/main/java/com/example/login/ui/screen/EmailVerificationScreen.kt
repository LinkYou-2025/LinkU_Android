package com.example.login.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.font.Paperlogy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.example.login.ui.item.LoginTextField
import com.example.login.ui.item.StepIndicator
import com.example.login.ui.item.BottomGradientButton
import com.example.design.util.rememberFigmaDimens
import com.example.login.viewmodel.EmailAuthViewModel
import com.example.login.viewmodel.SignUpViewModel
import com.example.design.theme.LocalColorTheme

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
    // 상태 변수
    val email = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(180) }
    var errorMessage by remember { mutableStateOf("") }

    // 리셋 신호
    val resetSignalFlow = remember(navigator) {
        navigator.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("reset_email_screen", false)
    }
    val resetSignal = resetSignalFlow?.collectAsState(initial = false)?.value ?: false

    LaunchedEffect(resetSignal) {
        if (resetSignal) {
            email.value = ""
            code.value = ""
            isSending = false
            isVerifying = false
            timer = 0
            viewModel.reset()
            navigator.currentBackStackEntry
                ?.savedStateHandle
                ?.set("reset_email_screen", false)
        }
    }

    val emailValid = remember(email.value) {
        Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }
    val context = LocalContext.current
    val sendResult by viewModel.sendCodeResult.collectAsState()
    val verifyResult by viewModel.verifyCodeResult.collectAsState()
    val isCodeSent = sendResult == "인증 코드 전송 성공"
    val isCodeValid = code.value.length == 6
    val timerText = String.format("%02d:%02d", timer / 60, timer % 60)

    // 인증 타이머
    LaunchedEffect(isCodeSent) {
        if (isCodeSent) {
            timer = 180
            while (timer > 0) {
                delay(1000)
                timer -= 1
            }
        }
    }

    // 결과 오면 토스트 + 잠금 해제
    LaunchedEffect(sendResult) {
        sendResult?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            isSending = false
        }
    }
    LaunchedEffect(verifyResult) {
        verifyResult?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            isVerifying = false
        }
    }
    // 인증 성공 시 다음 화면으로
    val isVerifySuccess by viewModel.isVerifySuccess.collectAsState()
    LaunchedEffect(isVerifySuccess) {
        if (isVerifySuccess) {
            signUpViewModel.email = email.value.trim()
            navigator.navigate("sign_up_password")
        }
    }

    // UI만 그리는 프레젠테이션 컴포저블에 상태/이벤트를 위임 -> 렌더 이슈로 부득이하게 프리뷰 구현을 위해 코드 추가.
    EmailVerificationScreenContent(
        navigator = navigator,
        email = email.value,
        onEmailChange = { email.value = it; errorMessage = "" },
        code = code.value,
        onCodeChange = { code.value = it; errorMessage = "" },
        isSending = isSending,
        isVerifying = isVerifying,
        timer = timer,
        sendResult = sendResult,
        verifyResult = verifyResult,
        isCodeSent = isCodeSent,
        isCodeValid = isCodeValid,
        timerText = timerText,
        emailValid = emailValid,
        errorMessage = errorMessage,
        onSendCode = {
            val cleanEmail = email.value.trim()
            if (isSending) return@EmailVerificationScreenContent
            isSending = true
            viewModel.sendEmailCode(cleanEmail)
        },
        onVerifyCode = {
            val cleanEmail = email.value.trim()
            if (isVerifying) return@EmailVerificationScreenContent
            isVerifying = true
            viewModel.verifyEmailCode(cleanEmail, code.value.trim())
        }
    )
}

/**
 * UI만 그리는 프레젠테이션 컴포저블입니다.
 * Preview에서 ViewModel 없이 안전하게 사용 가능.
 * 여기 이메일 인증에서는  """ui"""만 당당합니다
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
    timer: Int,
    sendResult: String?,
    verifyResult: String?,
    isCodeSent: Boolean,
    isCodeValid: Boolean,
    timerText: String,
    emailValid: Boolean,
    errorMessage: String,
    onSendCode: () -> Unit,
    onVerifyCode: () -> Unit
) {

    //디자인 모듈 불러오기
    val colorTheme = LocalColorTheme.current
    val (w, h) = rememberFigmaDimens()     //  Figma 412×917 기준 반응형
    val paperlogyFamily = Paperlogy.font


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = w(20f),
                    end = w(20f),
                    top = h(60f),
                    bottom = h(48f + 24f)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            //1단계
            StepIndicator(
                currentStep = 1,
                totalSteps = 3,
                label = "계정 정보"
            )
            Spacer(modifier = Modifier.height(h(36f)))
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = paperlogyFamily,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(h(32f)))
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
                sendResult == "이미 가입된 이메일입니다." || sendResult == "이미 가입된 이메일입니다" -> "이미 가입된 이메일입니다."
                else -> null
            }
            emailErrorText?.let {
                Spacer(modifier = Modifier.height(h(6f)))
                Text(
                    text = it,
                    color = Color(0xFFFF5E5E),
                    fontSize = 13.sp,
                    fontFamily = paperlogyFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(
                        x = w(4f)
                    )

                )
            }
            // 인증 코드 입력 영역 이건 타이머가 있어서 따로 요소 불러오지 않고 여기서만.
            if (isCodeSent) {
                Spacer(modifier = Modifier.height(h(10f)))
                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    placeholder = {
                        Text(
                            "코드를 입력해주세요",
                            fontSize = 14.sp,
                            fontFamily = paperlogyFamily,
                            color = colorTheme.gray[400]!!
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(h(56f))
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
                        val textModifier = Modifier.padding(end = w(12f))
                        if (sendResult == "서버 오류") {
                            Text(
                                text = "서버 오류",
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontFamily = paperlogyFamily,
                                modifier = Modifier.padding(end = w(22f)),
                                textAlign = TextAlign.Right
                            )
                        } else {
                            Text(
                                text = timerText,
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                fontFamily = paperlogyFamily,
                                modifier = textModifier
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                val codeErrorText: String? = when {
                    verifyResult == "인증번호가 올바르지 않습니다" ||
                            verifyResult == "인증 코드 불일치" ||
                            verifyResult == "인증 실패" ->
                        "이메일 인증 코드가 잘못 입력 되었습니다."
                    else -> null
                }

                codeErrorText?.let {
                    Spacer(modifier = Modifier.height(h(12f)))
                    Text(
                        text = it,
                        color = Color(0xFFFF5E5E),
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontFamily = paperlogyFamily,
                        fontWeight = FontWeight(400),
                        modifier = Modifier.padding(
                            start = w(12f)
                        )
                    )
                }//TODO : 하진 언니한테 오류 멘트 받아올 수 있는 api 수정 부탁하기!

            } else if (sendResult == "서버 오류") {
                Spacer(modifier = Modifier.height(h(8f)))
                Text(
                    text = "서버 오류: 잠시 후 다시 시도해주세요",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(h(8f))
                )
            }
        }
        // 하단 고정 영역
        val isButtonEnabled = sendResult != "서버 오류" &&
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
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight(500),
                color = colorTheme.gray[400]!!,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(
                        bottom = h(21f)
                    )
                    .clickable {
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
    EmailVerificationScreenContent(
        navigator = fakeNavigator,
        email = "",
        onEmailChange = {},
        code = "",
        onCodeChange = {},
        isSending = false,
        isVerifying = false,
        timer = 180,
        sendResult = null,
        verifyResult = null,
        isCodeSent = false,
        isCodeValid = false,
        timerText = "03:00",
        emailValid = true,
        errorMessage = "",
        onSendCode = {},
        onVerifyCode = {}
    )
}


//타이머  보려고
@Preview(showBackground = true)
@Composable
fun EmailVerificationScreen_TimerPreview() {
    val fakeNavigator = rememberNavController()

    EmailVerificationScreenContent(
        navigator = fakeNavigator,
        email = "test@email.com",
        onEmailChange = {},
        code = "123456",
        onCodeChange = {},
        isSending = false,
        isVerifying = false,
        timer = 153,
        sendResult = "인증 코드 전송 성공",
        verifyResult = "인증번호가 올바르지 않습니다",
        isCodeSent = true,
        isCodeValid = true,
        timerText = "02:33",
        emailValid = true,
        errorMessage = "",
        onSendCode = {},
        onVerifyCode = {}
    )
}
