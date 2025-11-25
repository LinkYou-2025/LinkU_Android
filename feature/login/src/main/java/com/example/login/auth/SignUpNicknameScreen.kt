package com.example.login.auth

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource

@Composable
fun SignUpNicknameScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    var nickname by remember { mutableStateOf(signUpViewModel.nickname) }

    val isNicknameAvailable by signUpViewModel.isNicknameAvailable.collectAsState()
    val nicknameMessage by signUpViewModel.nicknameMessage.collectAsState()
    val isLoading by signUpViewModel.isLoading.collectAsState()

    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 10

    // ✅ 버튼 활성 조건 (EmailVerificationScreen의 isButtonEnabled와 동일한 느낌)
    val isButtonEnabled = isNicknameValid &&
            (isNicknameAvailable != false) && // false만 비활성, null(미확인) 허용
            !isLoading

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ 추가: 이메일 인증 화면과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0

        val bottomGapWhenIme = 4.dp      // ✅ 키보드 보일 때 버튼-키보드 간격
        val bottomGapDefault = 16.dp     // ✅ 평소 하단 간격(기존 .padding(bottom = 16.dp)와 동일)
        //val navBottomDp = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        //val extraNavPadding = if (isImeVisible) 0.dp else navBottomDp
        val bottomPadding = (if (isImeVisible) bottomGapWhenIme else bottomGapDefault) //+ extraNavPadding


        // 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    // ✅ 버튼과 겹치지 않도록 여유(버튼 48 + 하단간격 24, 필요시 더 넉넉히)
                    bottom = 48.dp + 24.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            ProfileStepIndicator()
            Spacer(Modifier.height(32.dp))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

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
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        signUpViewModel.nickname = it
                        if (isNicknameValid) signUpViewModel.checkNickname()
                    },
                    placeholder = {
                        Text(
                            "닉네임을 입력해주세요.",
                            fontSize = 13.sp,
                            fontFamily = Paperlogy,
                            color = Color(0xFF757575)
                        )
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

            if (isNicknameAvailable == false) {
                Text(
                    "중복된 닉네임 입니다.",
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    color = Color(0xFFFF5E5E)
                )
            }
            if (nicknameMessage == "서버 요청 실패") {
                Text(
                    "서버 요청 실패",
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    color = Color(0xFFFF5E5E)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isNicknameValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "조건 만족",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    "국문/영문 10자 이하",
                    fontSize = 12.sp,
                    fontFamily = Paperlogy,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // ✅ 하단 고정 버튼 (EmailVerificationScreen과 동일한 방식)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // 항상 하단
               // .imePadding()                  // 키보드 올라오면 자동 위로
                //.navigationBarsPadding()       // 제스처/내비 바 안전영역
                //.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = bottomPadding)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = when {
                            isButtonEnabled -> listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                            else            -> listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                        }
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(enabled = isButtonEnabled) {

                    // 다음 화면으로 이동 (등록하신 라우트 사용)
                    navigator.navigate("sign_up_gender") {
                        // 뒤로가기로 되돌아오게 하고 싶으면 이 옵션들 생략
                        // 중복 스택 방지하고 싶으면 아래처럼 설정 가능
                        launchSingleTop = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileStepIndicator() {
    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = (18).dp)
        ) {
            // 1번 완료 원 (ic_level_check 적용)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_level_check),
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            //  연결 점선 (3개, ContentStepIndicator와 동일 간격)
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.2.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 2번 활성 원
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2",
                    fontFamily = Paperlogy,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 두 번째 연결 점선
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
                    fontFamily = Paperlogy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 하단 텍스트 (간격 동일)
        Text(
            text = "프로필 설정",
            modifier = Modifier.padding(start = 68.dp, top = 6.dp),
            fontSize = 13.sp,
            fontFamily = Paperlogy,
            color = Color(0xFFCB59EB),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpNicknameScreenPreview() {
    val fakeNavController = rememberNavController()
    SignUpNicknameScreenPreviewOnly(navigator = fakeNavController)
    //SignUpNicknameScreen(navigator = fakeNavController)
}


/**
 * Preview 전용: ViewModel 없이 UI만 보여줌
 * 프리뷰 이슈로 아예 ui만 보여주는....여기는 ui가 중요하니까...요...
 */
@Composable
private fun SignUpNicknameScreenPreviewOnly(navigator: NavHostController) {
    var nickname by remember { mutableStateOf("테스트닉네임") }

    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 10
    val isNicknameAvailable = true
    val isLoading = false

    val isButtonEnabled = isNicknameValid &&
            (isNicknameAvailable != false) &&
            !isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 72.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ProfileStepIndicator()
            Spacer(Modifier.height(32.dp))

            Text(
                text = "사용하실 닉네임을\n입력해주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

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
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = {
                        Text(
                            "닉네임을 입력해주세요.",
                            fontSize = 13.sp,
                            fontFamily = Paperlogy,
                            color = Color(0xFF757575)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .padding(start = 12.dp), // 오른쪽으로 12dp 이동
                verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isNicknameValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "조건 만족",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    "국문/영문 10자 이하",
                    fontSize = 12.sp,
                    fontFamily = Paperlogy,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isButtonEnabled)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Paperlogy
            )
        }
    }
}
