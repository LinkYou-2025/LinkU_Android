package com.example.login.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.style.TextAlign
import com.example.login.Paperlogy
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.login.R
import com.example.login.Paperlogy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


//비밀번호 재설정임!

@Composable
fun ResetPasswordScreen(
    navigator: NavHostController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()

    val ui = viewModel.ui.collectAsState().value
    var showSuccessDialog by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // ✅ 추가: 버튼 전용 바닥 패딩 계산
    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottomPx > 0
    val bottomGapWhenIme = 4.dp        // 키보드와 버튼 간격
    val bottomGapDefault = 16.dp       // 원래 화면 하단 여백 유지
    val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
    val extraNavPadding = if (isImeVisible) 0.dp else navBottomDp
    val bottomPadding = (if (isImeVisible) bottomGapWhenIme else bottomGapDefault) + extraNavPadding

    BackHandler {
        navigator.navigate("email_login") {
            popUpTo("resetPassword") { inclusive = true }
        }
    }

    // 성공시 다이얼로그 노출
    LaunchedEffect(ui.success) {
        if (ui.success) {
            showSuccessDialog = true
            viewModel.consumeSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 52.dp,   // ⬆️ 위쪽만 52
                bottom = 16.dp
                //bottom = 48.dp + 24.dp   // ✅ 하단 버튼(48) + 여유(24) 확보
            ),


            //.padding(horizontal = 20.dp, vertical = 52.dp)
            //.imePadding(),
        horizontalAlignment = Alignment.Start
       // horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_whiteback),
            contentDescription = "Logo",
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Start),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "비밀번호 재설정",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Paperlogy,
            color = Color(0xFF87898F),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (ui.error != null) viewModel.consumeError()
            },
            placeholder = {
                Text(
                    "이메일 주소를 입력해주세요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Paperlogy,
                    color = Color(0xFFB7B9BF)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isEmailValid && !ui.loading) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.request(email.text)
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
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

        // 입력 하단 에러 문구 (피그마  #FF3B30 느낌)
        if (ui.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = ui.error ?: "",
                color = Color(0xFFFF3B30),
                fontSize = 12.sp,
                fontFamily = Paperlogy,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 제출 버튼
        // ✅ 하단 버튼: 키보드 보이면 4dp, 아니면 40dp(+내비바) 간격
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(start = 20.dp, end = 20.dp, bottom = bottomPadding)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isEmailValid)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = isEmailValid && !ui.loading) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.request(email.text)
                },
            contentAlignment = Alignment.Center
        ) {
            if (ui.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "임시 비밀번호 받기",
                    color = Color.White,
                    fontFamily = Paperlogy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (isEmailValid)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(50)
//                )
//                .clickable(enabled = isEmailValid && !ui.loading) {
//                    keyboardController?.hide()
//                    focusManager.clearFocus()
//                    viewModel.request(email.text)
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            if (ui.loading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(20.dp),
//                    strokeWidth = 2.dp,
//                    color = Color.White
//                )
//            } else {
//                Text(
//                    text = "임시 비밀번호 받기",
//                    color = Color.White,
//                    fontFamily = Paperlogy,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
    }
    Spacer(modifier = Modifier.height(32.dp))

    // 성공 팝업
    if (showSuccessDialog) {
        PasswordResetAlert(
            onDismissRequest = { showSuccessDialog = false },
            onConfirmClick = {
                showSuccessDialog = false
                navigator.navigate("email_login") {
                    popUpTo("resetPassword") { inclusive = true }
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "ResetPasswordScreen Preview")
@Composable
fun ResetPasswordScreenPreview() {
    val fakeNavController = rememberNavController()
    ResetPasswordScreen(navigator = fakeNavController)
}

//
//@Composable
//fun ResetPasswordScreen(navigator: NavHostController) {
//    var email by remember { mutableStateOf(TextFieldValue("")) }
//    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val coroutineScope = rememberCoroutineScope()
//
//    // Alert(팝업) UI 상태 관리
//    var showSuccessDialog by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 32.dp, vertical = 40.dp)
//            .imePadding(), // 키보드 올라오면 자동 여백 처리
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.height(40.dp))
//
//        Image(
//            painter = painterResource(id = R.drawable.logo_whiteback),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(64.dp)
//                .align(Alignment.Start),
//            contentScale = ContentScale.Fit
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "비밀번호 재설정",
//            fontSize = 22.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = Paperlogy,
//            color = Color.Black,
//            textAlign = TextAlign.Start,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Normal,
//            fontFamily = Paperlogy,
//            color = Color(0xFF87898F),
//            textAlign = TextAlign.Start,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            placeholder = {
//                Text(
//                    "이메일 주소를 입력해주세요.",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Normal,
//                    fontFamily = Paperlogy,
//                    color = Color(0xFFB7B9BF)
//                )
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Email,
//                imeAction = ImeAction.Done
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White, shape = RoundedCornerShape(16.dp))
//                .border(
//                    width = 1.dp,
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                    ),
//                    shape = RoundedCornerShape(16.dp)
//                ),
//            colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedContainerColor = Color.Transparent,
//                unfocusedContainerColor = Color.Transparent
//            ),
//            shape = RoundedCornerShape(16.dp)
//        )
//
//        Spacer(modifier = Modifier.weight(1f)) // 항상 아래로 밀려 있게 유지
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = if (isEmailValid)
//                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                        else
//                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(50)
//                )
//                .clickable(enabled = isEmailValid) {
//                    keyboardController?.hide()
//                    coroutineScope.launch {
//                        // // 임시: API 없이 바로 팝업 띄우기!
//                        delay(1000) //임시 지연
//                        showSuccessDialog = true  // 팝업 띄우기
//                        //navigator.popBackStack()
//
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "임시 비밀번호 받기",
//                color = Color.White,
//                fontFamily = Paperlogy,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//
//    //  팝업 UI 표시
//    if (showSuccessDialog) {
//        PasswordResetAlert(
//            onDismissRequest = { showSuccessDialog = false },
//            onConfirmClick = {
//                showSuccessDialog = false
//                // 로그인 화면으로 이동
//                navigator.navigate("email_login") {
//                    // 팝업 띄운 resetPassword 화면 스택에서 제거
//                    popUpTo("resetPassword") { inclusive = true }
//                }
//            }
//        )
//    }
//}
//
//
//
//@Preview(showBackground = true, name = "ResetPasswordScreen Preview")
//@Composable
//fun ResetPasswordScreenPreview() {
//    val fakeNavController = rememberNavController()
//    ResetPasswordScreen(navigator = fakeNavController)
//}
//

@Preview(showBackground = true, name = "ResetPasswordScreen Content Preview")
@Composable
fun ResetPasswordScreenContentPreview() {
    ResetPasswordScreenContent(
        email = TextFieldValue("test@email.com"),
        onEmailChange = {},
        isEmailValid = true,
        loading = false,
        error = null,
        bottomPadding = 16.dp,
        onSubmit = {}
    )
}

@Composable
fun ResetPasswordScreenContent(
    email: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    isEmailValid: Boolean,
    loading: Boolean,
    error: String?,
    onSubmit: () -> Unit,
    bottomPadding: Dp
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 52.dp,
                bottom = 16.dp
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_whiteback),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "비밀번호 재설정",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
            fontSize = 16.sp,
            fontFamily = Paperlogy,
            color = Color(0xFF87898F)
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    "이메일 주소를 입력해주세요.",
                    fontSize = 14.sp,
                    fontFamily = Paperlogy,
                    color = Color(0xFFB7B9BF)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                    ),
                    RoundedCornerShape(16.dp)
                ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error,
                color = Color(0xFFFF3B30),
                fontSize = 12.sp,
                fontFamily = Paperlogy,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = bottomPadding)
                .background(
                    brush = Brush.horizontalGradient(
                        if (isEmailValid)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = isEmailValid && !loading) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onSubmit()
                },
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    "임시 비밀번호 받기",
                    color = Color.White,
                    fontFamily = Paperlogy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
