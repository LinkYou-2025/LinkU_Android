package com.example.login.auth

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
import com.example.linku_android.component.Paperlogy
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.linku_android.R



@Composable
fun ResetPasswordScreen(navigator: NavHostController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp)
            .imePadding(), // 키보드 올라오면 자동 여백 처리
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_whiteback),
            contentDescription = "Logo",
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Start),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "비밀번호 재설정",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Paperlogy,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "걱정 마세요! 이메일 주소를 입력해 주시면,\n임시 비밀번호를 보내드릴게요!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Paperlogy,
            color = Color(0xFF87898F),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
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

        Spacer(modifier = Modifier.weight(1f)) // 항상 아래로 밀려 있게 유지

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isEmailValid)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable(enabled = isEmailValid) {
                    keyboardController?.hide()
                    coroutineScope.launch {
                        // TODO: 임시 비밀번호 전송 로직 처리
                        delay(1000)
                        navigator.popBackStack()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "임시 비밀번호 받기",
                color = Color.White,
                fontFamily = Paperlogy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "ResetPasswordScreen Preview")
@Composable
fun ResetPasswordScreenPreview() {
    val fakeNavController = rememberNavController()
    ResetPasswordScreen(navigator = fakeNavController)
}

