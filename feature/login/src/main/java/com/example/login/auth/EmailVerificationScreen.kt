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
    var timer by remember { mutableStateOf(180) }

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
            .padding(horizontal = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
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

            // 인증 코드 입력 영역
            if (isCodeSent) {
                Spacer(modifier = Modifier.height(24.dp))

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
                    trailingIcon = {
                        if (sendResult == "서버 오류") {
                            Text(
                                text = "서버 오류",
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                fontFamily = Paperlogy
                            )
                        } else {
                            Text(
                                text = timerText,
                                color = Color(0xFFFF5E5E),
                                fontSize = 13.sp,
                                fontFamily = Paperlogy
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
            }
            else if (sendResult == "서버 오류") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "서버 오류: 잠시 후 다시 시도해주세요",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        val isButtonEnabled = sendResult != "서버 오류" &&
                (if (isCodeSent) isCodeValid else emailValid)

        // 하단 버튼 (메일 발송 또는 인증)
        // 하단 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = when {
                            isCodeSent && isCodeValid -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            !isCodeSent && emailValid  -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else                        -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(enabled = isButtonEnabled) {   //  여기서 변경됨
                    val cleanEmail = email.value.trim()
                    if (isCodeSent) {
                        viewModel.verifyEmailCode(cleanEmail, code.value.trim())
                    } else {
                        viewModel.sendEmailCode(cleanEmail)
                    }
                },
//                .clickable(enabled = if (isCodeSent) isCodeValid else emailValid) {
//                    val cleanEmail = emailState.value.trim()
//                    if (isCodeSent) {
//                        viewModel.verifyEmailCode(cleanEmail, codeState.value.trim())
//                    } else {
//                        viewModel.sendEmailCode(cleanEmail)
//                    }
//                },
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

    // Send 결과 토스트
    LaunchedEffect(sendResult) {
        sendResult?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Verify 결과 토스트
    LaunchedEffect(verifyResult) {
        verifyResult?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
