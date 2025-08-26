package com.example.login.auth


import android.R.attr.textStyle
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalDensity

import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun EmailVerificationScreen(
    navigator: NavHostController,
    viewModel: EmailAuthViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    //val signUpViewModel: SignUpViewModel = hiltViewModel()
    val email = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }

    var isSending    by remember { mutableStateOf(false) }   // 코드 전송 중
    var isVerifying  by remember { mutableStateOf(false) }   // 코드 검증 중
    var timer        by remember { mutableStateOf(180) }     // 타이머

    // 2)  savedStateHandle로부터 '리셋 신호' 구독
    val resetSignalFlow = remember(navigator) {
        navigator.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("reset_email_screen", false)
    }
    val resetSignal = resetSignalFlow?.collectAsState(initial = false)?.value ?: false

    // 3) 리셋 신호 들어오면 화면/VM 상태 초기화
    LaunchedEffect(resetSignal) {
        if (resetSignal) {
            email.value = ""
            code.value = ""

            isSending = false
            isVerifying = false
            timer = 0                     // 타이머 정지 상태로 (코드 전송 시에만 180부터 재시작)

            viewModel.reset()             // VM 내부 결과 초기화

            // 플래그 소모 (다음 진입 때 중복 초기화 방지)
            navigator.currentBackStackEntry
                ?.savedStateHandle
                ?.set("reset_email_screen", false)
        }
    }

//    val emailValid = remember(emailState.value) {
//        android.util.Patterns.EMAIL_ADDRESS.matcher(emailState.value).matches()
//    }
    val emailValid = remember(email.value) {
        Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }



    val context = LocalContext.current

    val sendResult by viewModel.sendCodeResult.collectAsState()
    val verifyResult by viewModel.verifyCodeResult.collectAsState()

    var errorMessage by remember { mutableStateOf("") }
//    var timer by remember { mutableStateOf(180) }

    //버튼 여러번 누르는 사용자 버그 있음. 한번만 적용되도록 수정하기!
//    var isSending by remember { mutableStateOf(false) }     // 코드 전송 중
//    var isVerifying by remember { mutableStateOf(false) }   // 코드 검증 중

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

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    top = 52.dp,
                    end = 20.dp,
                    bottom = 48.dp + 24.dp // 버튼 높이(48) + 간격(24) 정도 확보
                ),
            //.padding(start = 20.dp, top = 52.dp, end = 20.dp, bottom = 48.dp + 32.dp + 24.dp),
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
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        errorMessage = ""
                    },
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
                    enabled = !isSending && !isVerifying, //  입력 잠깐 잠그기 -> 성격 급한 사용자 감안.
                    modifier = Modifier
                        .fillMaxSize()
                        //.fillMaxWidth()
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

            // 여기 추가: 에러 문구 (입력 박스와의 간격 6dp)
            val emailErrorText: String? = when {
                email.value.isNotBlank() && !emailValid ->
                    "이메일 양식이 올바르지 않습니다!"
                // 서버에서 중복 이메일을 알려주는 경우(문자 그대로 비교)
                sendResult == "이미 가입된 이메일입니다." || sendResult == "이미 가입된 이메일입니다" ->
                    "이미 가입된 이메일입니다."

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
                    value = code.value,
                    onValueChange = {
                        code.value = it
                        errorMessage = ""
                    },
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
                    enabled = !isVerifying, // 검증 중 입력 잠금 -> 성격 급한 사용자 감안 수정.
                    trailingIcon = {
                        val textModifier = Modifier.padding(end = 12.dp) // ← 오른쪽 여백 확보

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
                                modifier = textModifier   // ← 여기 추가
                            )
                        }
                    }
                    ,
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
        // ── 하단 고정 버튼: SignUpJobScreen 과 동일 규격 ─────────
        val isButtonEnabled = sendResult != "서버 오류" &&
                !isSending && !isVerifying &&
                (if (isCodeSent) isCodeValid else emailValid)

//
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0

        val bottomGapWhenIme = 4.dp      // 키보드 위 간격
        val bottomGapDefault = 16.dp     // 평소 바닥 간격

        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

// 내비게이션 바 높이(dp) – IME가 없을 때만 적용
        //val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        //val extraNavPadding = if (isImeVisible) 0.dp else navBottomDp

        //val bottomPadding =
            (if (isImeVisible) bottomGapWhenIme else bottomGapDefault) //+ extraNavPadding

        // ── 하단 고정 버튼: 원래 위치 + 키보드 대응 ─────────
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ... 위의 Column 그대로

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)    // ✅ 화면 하단에 고정
                    //.imePadding()                     // ✅ 키보드가 올라오면 자동으로 위로
                    //.navigationBarsPadding()          // ✅ 제스처/내비 바 안전영역 확보
                    .padding(start = 20.dp, end = 20.dp, bottom = bottomPadding) // 하단 간격
                    //.offset(y = (-48).dp)         // ⬅️ 추가: 키보드 위 간격 줄이기
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = when {
                                isCodeSent && isCodeValid -> listOf(
                                    Color(0xFF2C6FFF),
                                    Color(0xFFC800FF)
                                )

                                !isCodeSent && emailValid -> listOf(
                                    Color(0xFF2C6FFF),
                                    Color(0xFFC800FF)
                                )

                                else -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                            }
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(enabled = isButtonEnabled) {
                        val cleanEmail = email.value.trim()
                        if (isCodeSent) {
                            if (isVerifying) return@clickable
                            isVerifying = true
                            viewModel.verifyEmailCode(cleanEmail, code.value.trim())
                        } else {
                            if (isSending) return@clickable
                            isSending = true
                            viewModel.sendEmailCode(cleanEmail)
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
//        // 🔒 버튼 활성 조건에 잠금 플래그 포함
//        val isButtonEnabled = sendResult != "서버 오류" &&
//                !isSending && !isVerifying &&
//                (if (isCodeSent) isCodeValid else emailValid)
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize() // ⛔️ 여기에서 horizontal 32dp 제거
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 32.dp) // ✅ 내용 여백은 여기로 이동
//                    .padding(top = 40.dp),
//                horizontalAlignment = Alignment.Start
//            ) {
//                // ... 기존 내용들 (StepIndicator, Text, 필드들)
//            }
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .imePadding()
//                    .padding(start = 32.dp, end = 32.dp, bottom = 50.dp) // ⬅️ 여기 16 → 32 로 수정
//                    .height(50.dp)
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = when {
//                                isCodeSent && isCodeValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                                !isCodeSent && emailValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                                else -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                            }
//                        ),
//                        shape = RoundedCornerShape(12.dp) // ⬅️ 동일
//                    )
//                    .clickable(enabled = isButtonEnabled) {
//                        val cleanEmail = email.value.trim()
//                        if (isCodeSent) {
//                            if (isVerifying) return@clickable
//                            isVerifying = true
//                            viewModel.verifyEmailCode(cleanEmail, code.value.trim())
//                        } else {
//                            if (isSending) return@clickable
//                            isSending = true
//                            viewModel.sendEmailCode(cleanEmail)
//                        }
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = if (isCodeSent) "인증하기" else "인증메일 발송",
//                    color = Color.White,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = Paperlogy
//                )
//            }
//        }
//    }


//        // 하단 버튼 (메일 발송 또는 인증)
//        // 하단 버튼 (메일 발송 또는 인증)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .imePadding() // ✅ 키보드 올라올 때 버튼도 같이 이동
//                .padding(start = 16.dp, end = 16.dp, bottom = 50.dp) // ✅ 마케팅 화면과 동일
//                .height(50.dp) // ✅ 마케팅 화면과 동일
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = when {
//                            isCodeSent && isCodeValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            !isCodeSent && emailValid  -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            else                        -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                        }
//                    ),
//                    shape = RoundedCornerShape(12.dp) // ✅ 마케팅 화면과 동일
//                )
//                .clickable(enabled = isButtonEnabled) {
//                    val cleanEmail = email.value.trim()
//                    if (isCodeSent) {
//                        if (isVerifying) return@clickable
//                        isVerifying = true
//                        viewModel.verifyEmailCode(cleanEmail, code.value.trim())
//                    } else {
//                        if (isSending) return@clickable
//                        isSending = true
//                        viewModel.sendEmailCode(cleanEmail)
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = if (isCodeSent) "인증하기" else "인증메일 발송",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                fontFamily = Paperlogy
//            )
//        }
//    }


//    // Send 결과 토스트
//    LaunchedEffect(sendResult) {
//        sendResult?.let { msg ->
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Verify 결과 토스트
//    LaunchedEffect(verifyResult) {
//        verifyResult?.let { msg ->
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
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
}
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 32.dp)
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = when {
//                            isCodeSent && isCodeValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            !isCodeSent && emailValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            else -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                        }
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .clickable(
//                    enabled = if (isCodeSent) isCodeValid else emailValid
//                ) {
//                    if (isCodeSent) {
//                        viewModel.verifyEmailCode(context, email.value, code.value)
//                    } else {
//                        viewModel.sendEmailCode(email.value)
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = if (isCodeSent) "인증하기" else "인증메일 발송",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//
//    //비밀번호 수정으로 넘어가기
//    val isVerifySuccess by viewModel.isVerifySuccess.collectAsState()
//
//    LaunchedEffect(isVerifySuccess) {
//        if (isVerifySuccess) {
//            signUpViewModel.email = email.value
//            navigator?.navigate("sign_up_password")
//        }
//    }
//}


@Composable
fun StepIndicator() {
    val isPreview = LocalInspectionMode.current //폰트 표시

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ... 점선
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 2번 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("2", color = Color(0xFFD6D6D6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(8.dp))

            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("3", color = Color(0xFFD6D6D6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        //  1번 원 바로 아래에 계정 정보
        Text(
            text = "계정 정보",
            modifier = Modifier.padding(start = 0.dp, top = 4.dp),
            fontSize = 12.sp,
            color = Color(0xFFCB59EB),
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationScreenPreview() {
    val fakeNavigator = rememberNavController()
    val fakeEmailViewModel = viewModel<EmailAuthViewModel>()
    val fakeSignUpViewModel = viewModel<SignUpViewModel>()

    EmailVerificationScreen(
        navigator = fakeNavigator,
        viewModel = fakeEmailViewModel,
        signUpViewModel = fakeSignUpViewModel
    )
}

