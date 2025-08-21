package com.example.login.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.clickable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignUpPasswordScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    // 항상 이메일 인증으로 되돌리는 로직
    fun goBackToEmailVerification() {
        val popped = navigator.popBackStack("email_verification", inclusive = false)
        if (!popped) {
            navigator.navigate("email_verification") {
                launchSingleTop = true
                // 이미 존재하면 그 지점까지 스택 정리 (없으면 no-op)
                popUpTo("email_verification") {
                    inclusive = false
                    saveState = true
                }
            }
        }
    }

    BackHandler(enabled = true) {
        // 이메일 화면으로 돌아가기 전에 '리셋 신호' 전달
        navigator.previousBackStackEntry
            ?.savedStateHandle
            ?.set("reset_email_screen", true)

        val popped = navigator.popBackStack("email_verification", inclusive = false)
        if (!popped) {
            navigator.navigate("email_verification") {
                launchSingleTop = true
                popUpTo("email_verification") {
                    inclusive = false
                    saveState = true
                }
            }
        }
    }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isPasswordLengthValid = password.length in 8..20
    val isPasswordComplex =
        password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() }

    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
    val doPasswordsMatch = password == confirmPassword
    val showConfirmField = isPasswordValid
    val canProceed = isPasswordValid && doPasswordsMatch

    Box(modifier = Modifier.fillMaxSize()) {

        // 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    // 버튼 영역만큼 여유 (48dp 높이 + 32dp 바텀 패딩 + 약간의 버퍼)
                    bottom = 48.dp + 32.dp + 24.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator()
            Spacer(Modifier.height(32.dp))

            Text(
                text = "사용하실 비밀번호를\n 입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(32.dp))

            // 비밀번호 입력
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
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text("비밀번호를 입력해주세요.", fontSize = 13.sp, fontFamily = Paperlogy, color = Color(0xFF757575))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxSize()
                        //.fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // 조건 체크 라인
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("영문, 숫자, 특수기호 조합", fontFamily = Paperlogy, fontSize = 12.sp, color = Color(0xFF757575))
                }

                Spacer(Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("8~20자", fontFamily = Paperlogy, fontSize = 12.sp, color = Color(0xFF757575))
                }
            }

            if (showConfirmField) {
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = {
                        Text("비밀번호를 확인해주세요.", fontSize = 13.sp, fontFamily = Paperlogy, color = Color(0xFF757575))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                    Text(
                        text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFFF5E5E),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // 하단 고정 버튼 (이메일 화면과 동일 위치)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .imePadding()                                   // 키보드 올라오면 함께 상승
                .padding(start = 20.dp, end = 20.dp, bottom = 42.dp) // ← 동일 위치(살짝 더 낮게)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (canProceed)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = canProceed) {
                    signUpViewModel.password = password
                    navigator.navigate("sign_up_nickname")
                },
            contentAlignment = Alignment.Center
        ) {
            Text("다음", color = Color.White, fontFamily = Paperlogy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

//@Composable
//fun SignUpPasswordScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    val isPasswordLengthValid = password.length in 8..20
//    val isPasswordComplex =
//        password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() }
//
//    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
//    val doPasswordsMatch = password == confirmPassword
//    val showConfirmField = isPasswordValid
//    val canProceed = isPasswordValid && doPasswordsMatch
//
//    // ✅ 루트를 Box로: 본문과 하단 버튼을 형제로 분리 (이메일 인증 화면과 동일 패턴)
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // ───────── 본문 ─────────
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    start = 20.dp,
//                    end = 20.dp,
//                    top = 52.dp,
//                    // ✅ 하단 버튼 영역만큼 안전 여백 확보 (컨텐츠 가림 방지)
//                    bottom = 48.dp + 32.dp + 24.dp
//                ),
//            horizontalAlignment = Alignment.Start
//        ) {
//            StepIndicator()
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Text(
//                text = "사용하실 비밀번호를\n 입력해주세요",
//                fontSize = 22.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                textAlign = TextAlign.Start
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 비밀번호 입력
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        ),
//                        shape = RoundedCornerShape(16.dp)
//                    )
//                    .padding(1.dp)
//            ) {
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    placeholder = {
//                        Text(
//                            "비밀번호를 입력해주세요.",
//                            fontSize = 13.sp,
//                            fontFamily = Paperlogy,
//                            color = Color(0xFF757575)
//                        )
//                    },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // 조건 체크 라인
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(
//                                if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                                shape = RoundedCornerShape(4.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "영문, 숫자, 특수기호 조합",
//                        fontFamily = Paperlogy,
//                        fontSize = 12.sp,
//                        color = Color(0xFF757575)
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(
//                                if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                                shape = RoundedCornerShape(4.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "8~20자",
//                        fontFamily = Paperlogy,
//                        fontSize = 12.sp,
//                        color = Color(0xFF757575)
//                    )
//                }
//            }
//
//            if (showConfirmField) {
//                Spacer(modifier = Modifier.height(24.dp))
//
//                OutlinedTextField(
//                    value = confirmPassword,
//                    onValueChange = { confirmPassword = it },
//                    placeholder = {
//                        Text(
//                            "비밀번호를 확인해주세요.",
//                            fontSize = 13.sp,
//                            fontFamily = Paperlogy,
//                            color = Color(0xFF757575)
//                        )
//                    },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .background(Color.White, shape = RoundedCornerShape(16.dp))
//                        .border(
//                            width = 1.dp,
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            ),
//                            shape = RoundedCornerShape(16.dp)
//                        ),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//
//                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
//                    Text(
//                        text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
//                        fontSize = 13.sp,
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.Normal,
//                        color = Color(0xFFFF5E5E),
//                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
//                    )
//                }
//            }
//        }
//
//        // ───────── 하단 고정 버튼 ─────────
//        // 이메일 인증 화면과 동일 배치: align + imePadding + padding + offset
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .imePadding() // ✅ 키보드 올라오면 자동으로 위로 이동
//                .padding(bottom = 32.dp) // ✅ 버튼과 키보드 사이에 여유 공간
////                .fillMaxWidth()
////                .align(Alignment.BottomCenter)
////                .imePadding()                              // ✅ 키보드 올라오면 버튼도 함께 상승
////                .padding(start = 20.dp, end = 20.dp, bottom = 64.dp) // ✅ 동일 위치
////                .offset(y = -16.dp)                        // ✅ 위로 16 올림 (미세 위치 동일화)
////                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (canProceed)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .clickable(enabled = canProceed) {
//                    signUpViewModel.password = password
//                    navigator.navigate("sign_up_nickname")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "다음",
//                color = Color.White,
//                fontFamily = Paperlogy,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}

//@Composable
//fun SignUpPasswordScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    val isPasswordLengthValid = password.length in 8..20
//    val isPasswordComplex = password.any { it.isDigit() } &&
//            password.any { it.isLetter() } &&
//            password.any { !it.isLetterOrDigit() }
//
//    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
//    val doPasswordsMatch = password == confirmPassword
//    val showConfirmField = isPasswordValid
//    val canProceed = isPasswordValid && doPasswordsMatch
//
//    // ✅ 루트를 Box로 바꿔서, 본문과 하단 버튼을 형제 레벨로 분리 (이메일 화면과 동일 패턴)
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // ───────── 본문 ─────────
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    start = 20.dp,
//                    end = 20.dp,
//                    top = 52.dp,
//                    // ✅ 버튼 영역만큼 바닥 여백 확보 (이메일 화면 패턴과 동일하게 넉넉히)
//                    bottom = 48.dp + 32.dp + 24.dp
//                ),
//            horizontalAlignment = Alignment.Start
//        ) {
//            StepIndicator()
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Text(
//                text = "사용하실 비밀번호를\n 입력해주세요",
//                fontSize = 22.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                textAlign = TextAlign.Start
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 비밀번호 입력
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        ),
//                        shape = RoundedCornerShape(16.dp)
//                    )
//                    .padding(1.dp)
//            ) {
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    placeholder = {
//                        Text(
//                            "비밀번호를 입력해주세요.",
//                            fontSize = 13.sp,
//                            fontFamily = Paperlogy,
//                            color = Color(0xFF757575)
//                        )
//                    },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // 조건 체크 라인
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(
//                                if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                                shape = RoundedCornerShape(4.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "영문, 숫자, 특수기호 조합",
//                        fontFamily = Paperlogy,
//                        fontSize = 12.sp,
//                        color = Color(0xFF757575)
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(
//                                if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                                shape = RoundedCornerShape(4.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "8~20자",
//                        fontFamily = Paperlogy,
//                        fontSize = 12.sp,
//                        color = Color(0xFF757575)
//                    )
//                }
//            }
//
//            if (showConfirmField) {
//                Spacer(modifier = Modifier.height(24.dp))
//
//                OutlinedTextField(
//                    value = confirmPassword,
//                    onValueChange = { confirmPassword = it },
//                    placeholder = {
//                        Text(
//                            "비밀번호를 확인해주세요.",
//                            fontSize = 13.sp,
//                            fontFamily = Paperlogy,
//                            color = Color(0xFF757575)
//                        )
//                    },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .background(Color.White, shape = RoundedCornerShape(16.dp))
//                        .border(
//                            width = 1.dp,
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                            ),
//                            shape = RoundedCornerShape(16.dp)
//                        ),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//
//                if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
//                    Text(
//                        text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
//                        fontSize = 13.sp,
//                        fontFamily = Paperlogy,
//                        fontWeight = FontWeight.Normal,
//                        color = Color(0xFFFF5E5E),
//                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
//                    )
//                }
//            }
//        }
//
//        // ───────── 하단 고정 버튼 (이메일 화면과 동일 배치/동작) ─────────
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .imePadding()                         // ✅ 키보드 올라오면 버튼도 같이 올라감
//                .padding(start = 20.dp, end = 20.dp, bottom = 64.dp) // ✅ 동일 위치
//                .offset(y = -16.dp)                   // ✅ 위로 16 올림 (약관/이메일 버튼과 동일)
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (canProceed)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .clickable(enabled = canProceed) {
//                    signUpViewModel.password = password
//                    navigator.navigate("sign_up_nickname")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "다음",
//                color = Color.White,
//                fontFamily = Paperlogy,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}
//@Composable
//fun SignUpPasswordScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    // 사용자 입력 상태 정의
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    // 비밀번호 길이 및 조합 유효성 검사
//    val isPasswordLengthValid = password.length in 8..20
//    val isPasswordComplex = password.any { it.isDigit() } &&
//            password.any { it.isLetter() } &&
//            password.any { !it.isLetterOrDigit() }
//
//    // 비밀번호가 유효한지 여부
//    val isPasswordValid = isPasswordLengthValid && isPasswordComplex
//
//    // 비밀번호 일치 여부
//    val doPasswordsMatch = password == confirmPassword
//
//    // 비밀번호가 유효할 경우에만 확인 입력 필드 표시
//    val showConfirmField = isPasswordValid
//
//    // 모든 조건을 만족할 경우 다음 단계 진행 가능
//    val canProceed = isPasswordValid && doPasswordsMatch
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(
//                start = 20.dp,
//                end = 20.dp,
//                top = 52.dp,   // ⬆️ 위쪽만 52
//                bottom = 40.dp // ⬇️ 아래는 40 유지
//            ),
//            //.padding(horizontal = 20.dp, vertical = 40.dp),
//        horizontalAlignment = Alignment.Start
//    ) {
//        StepIndicator()
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Text(
//            text = "사용하실 비밀번호를\n 입력해주세요",
//            fontSize = 22.sp,
//            fontFamily = Paperlogy,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black,
//            textAlign = TextAlign.Start
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        // 비밀번호 입력 필드
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                )
//                .padding(1.dp)
//        ) {
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                placeholder = {
//                    Text(
//                        "비밀번호를 입력해주세요.",
//                        fontSize = 13.sp,
//                        fontFamily = Paperlogy,
//                        color = Color(0xFF757575)
//                    )
//                },
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(16.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // 비밀번호 조건 상태 표시
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(
//                            if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                            shape = RoundedCornerShape(4.dp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(12.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "영문, 숫자, 특수기호 조합",
//                    fontFamily = Paperlogy,
//                    fontSize = 12.sp,
//                    color = Color(0xFF757575)
//                )
//            }
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(
//                            if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                            shape = RoundedCornerShape(4.dp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(12.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "8~20자",
//                    fontFamily = Paperlogy,
//                    fontSize = 12.sp,
//                    color = Color(0xFF757575)
//                )
//            }
//        }
//
//        // 비밀번호 확인 입력 필드
//        if (showConfirmField) {
//            Spacer(modifier = Modifier.height(24.dp))
//
//            OutlinedTextField(
//                value = confirmPassword,
//                onValueChange = { confirmPassword = it },
//                placeholder = {
//                    Text(
//                        "비밀번호를 확인해주세요.",
//                        fontSize = 13.sp,
//                        fontFamily = Paperlogy,
//                        color = Color(0xFF757575)
//                    )
//                },
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .background(Color.White, shape = RoundedCornerShape(16.dp))
//                    .border(
//                        width = 1.dp,
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        ),
//                        shape = RoundedCornerShape(16.dp)
//                    ),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(16.dp)
//            )
//
//            // 비밀번호 불일치 메시지
//            if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
//                Text(
//                    text = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
//                    fontSize = 13.sp,
//                    fontFamily = Paperlogy,
//                    fontWeight = FontWeight.Normal,
//                    color = Color(0xFFFF5E5E),
//                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // 다음 버튼
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 64.dp) // ✅ 버튼 하단 여백
//                .offset(y = -16.dp) // ✅ 위로 16 올림
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (canProceed)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .clickable(enabled = canProceed) {
//                    // 비밀번호 저장
//                    signUpViewModel.password = password
//
//                    // 다음 화면으로 이동 (예: 닉네임 입력 페이지)
//                    navigator?.navigate("sign_up_nickname")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "다음",
//                color = Color.White,
//                fontFamily = Paperlogy,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//    }
//}

@Preview(showBackground = true)
@Composable
fun SignUpPasswordScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakeSignUpViewModel = viewModel<SignUpViewModel>()

    SignUpPasswordScreen(
        navigator = fakeNavController,
        signUpViewModel = fakeSignUpViewModel
    )
}
