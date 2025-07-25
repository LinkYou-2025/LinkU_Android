package com.example.linku_android.auth

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
import com.example.linku_android.component.Paperlogy
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
    // 사용자 입력 상태 정의
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 비밀번호 길이 및 조합 유효성 검사
    val isPasswordLengthValid = password.length in 8..20
    val isPasswordComplex = password.any { it.isDigit() } &&
            password.any { it.isLetter() } &&
            password.any { !it.isLetterOrDigit() }

    // 비밀번호가 유효한지 여부
    val isPasswordValid = isPasswordLengthValid && isPasswordComplex

    // 비밀번호 일치 여부
    val doPasswordsMatch = password == confirmPassword

    // 비밀번호가 유효할 경우에만 확인 입력 필드 표시
    val showConfirmField = isPasswordValid

    // 모든 조건을 만족할 경우 다음 단계 진행 가능
    val canProceed = isPasswordValid && doPasswordsMatch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        StepIndicator()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "사용하실 비밀번호를\n 입력해주세요",
            fontSize = 22.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 비밀번호 입력 필드
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
                    Text(
                        "비밀번호를 입력해주세요.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF757575)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 조건 상태 표시
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isPasswordComplex) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "영문, 숫자, 특수기호 조합",
                    fontFamily = Paperlogy,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isPasswordLengthValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "8~20자",
                    fontFamily = Paperlogy,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        // 비밀번호 확인 입력 필드
        if (showConfirmField) {
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text(
                        "비밀번호를 확인해주세요.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF757575)
                    )
                },
                singleLine = true,
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
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // 비밀번호 불일치 메시지
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

        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (canProceed)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(enabled = canProceed) {
                    // 비밀번호 저장
                    signUpViewModel.password = password

                    // 다음 화면으로 이동 (예: 닉네임 입력 페이지)
                    navigator?.navigate("sign_up_nickname")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontFamily = Paperlogy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

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
