package com.example.login.auth

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.navigation.compose.rememberNavController
import com.example.login.Paperlogy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry

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

    // 이메일 인증 화면은 뒤로가면 로그인 화면(약관 선택 페이지)으로 돌아가는게 맞는지
    //TODO : 다인언니에게 물어보기!
    BackHandler {
        parentEntry.savedStateHandle["from_email_verification"] = true

        navigator.navigate("login") {
            popUpTo("auth_graph") { inclusive = false }
            launchSingleTop = true
        }
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
 * UI만 그리는 프레젠테이션 컴포저블입니다. Preview에서 ViewModel 없이 안전하게 사용 가능.
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
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 52.dp, end = 20.dp, bottom = 48.dp + 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            // 이메일 입력 필드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(1.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = {
                        Text(
                            "이메일 주소를 입력해주세요",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Paperlogy,
                            color = Color(0xFF757575)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Normal
                    ),
                    singleLine = true,
                    enabled = !isSending && !isVerifying,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
            // 에러 문구
            val emailErrorText: String? = when {
                email.isNotBlank() && !emailValid -> "이메일 양식이 올바르지 않습니다!"
                sendResult == "이미 가입된 이메일입니다." || sendResult == "이미 가입된 이메일입니다" -> "이미 가입된 이메일입니다."
                else -> null
            }
            emailErrorText?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    color = Color(0xFFFF5E5E),
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(x = 4.dp)
                )
            }
            // 인증 코드 입력 영역
            if (isCodeSent) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    placeholder = {
                        Text(
                            "코드를 입력해주세요",
                            fontSize = 13.sp,
                            fontFamily = Paperlogy,
                            color = Color(0xFF757575)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    enabled = !isVerifying,
                    trailingIcon = {
                        val textModifier = Modifier.padding(end = 12.dp)
                        if (sendResult == "서버 오류") {
                            Text(
                                text = "서버 오류",
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                fontFamily = Paperlogy,
                                modifier = textModifier
                            )
                        } else {
                            Text(
                                text = timerText,
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                fontFamily = Paperlogy,
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
            } else if (sendResult == "서버 오류") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "서버 오류: 잠시 후 다시 시도해주세요",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        // 하단 고정 버튼
        val isButtonEnabled = sendResult != "서버 오류" &&
                !isSending && !isVerifying &&
                (if (isCodeSent) isCodeValid else emailValid)
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0
        val bottomGapWhenIme = 4.dp
        val bottomGapDefault = 16.dp
        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = bottomPadding)
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = when {
                                isCodeSent && isCodeValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                                !isCodeSent && emailValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                                else -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                            }
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(enabled = isButtonEnabled) {
                        if (isCodeSent) {
                            onVerifyCode()
                        } else {
                            onSendCode()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCodeSent) "인증하기" else "인증메일 발송",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Paperlogy
                )
            }
        }
    }
}

@Composable
fun StepIndicator() {
    val isPreview = LocalInspectionMode.current

    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = 14.dp) // ✅ ProfileStepIndicator의 기준 오프셋 반영
        ) {
            // 1️⃣ 1번 활성 원
            Box(
                modifier = Modifier
                    .size(30.dp) // ✅ ProfileStepIndicator 크기 통일
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(6.dp)) //  8dp → 6dp, 균형 조정

            // 🔹 연결 점선 (3개)
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.2.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 2️⃣ 2번 비활성 원
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2",
                    color = Color(0xFFD6D6D6),
                    fontSize = 16.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 🔹 두 번째 점선
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.2.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3번 비활성 원
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3",
                    color = Color(0xFFD6D6D6),
                    fontSize = 16.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 하단 텍스트 (ProfileStepIndicator 정렬 반영)
        Text(
            text = "계정 정보",
            modifier = Modifier.padding(start = 2.dp, top = 6.dp), // ✅ ProfileStepIndicator 간격 반영
            fontSize = 13.sp,
            fontFamily = Paperlogy,
            color = Color(0xFFCB59EB),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
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
