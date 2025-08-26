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

@Composable
fun SignUpNicknameScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    var nickname by remember { mutableStateOf("") }

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

//@Composable
//fun SignUpNicknameScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    var nickname by remember { mutableStateOf("") }
//
//    val isNicknameAvailable by signUpViewModel.isNicknameAvailable.collectAsState()
//    val nicknameMessage by signUpViewModel.nicknameMessage.collectAsState()
//    val isLoading by signUpViewModel.isLoading.collectAsState()
//
//    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 10
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 본문 (기존 UI 유지)
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    start = 20.dp,
//                    end = 20.dp,
//                    top = 52.dp,
//                    // 버튼 영역만큼 여유
//                    bottom = 48.dp + 32.dp + 24.dp
//                ),
//            horizontalAlignment = Alignment.Start
//        ) {
//            ProfileStepIndicator()
//            Spacer(Modifier.height(32.dp))
//
//            Text(
//                text = "사용하실 닉네임을\n입력해주세요",
//                fontSize = 22.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Spacer(Modifier.height(32.dp))
//
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
//                    value = nickname,
//                    onValueChange = {
//                        nickname = it
//                        signUpViewModel.nickname = it
//                        if (isNicknameValid) signUpViewModel.checkNickname()
//                    },
//                    placeholder = {
//                        Text("닉네임을 입력해주세요.", fontSize = 13.sp, fontFamily = Paperlogy, color = Color(0xFF757575))
//                    },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.White, RoundedCornerShape(16.dp)),
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
//            if (isNicknameAvailable == false) {
//                Text("중복된 닉네임 입니다.", fontSize = 13.sp, fontFamily = Paperlogy, color = Color(0xFFFF5E5E))
//            }
//            if (nicknameMessage == "서버 요청 실패") {
//                Text("서버 요청 실패", fontSize = 13.sp, fontFamily = Paperlogy, color = Color(0xFFFF5E5E))
//            }
//
//            Spacer(Modifier.height(12.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(if (isNicknameValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF), RoundedCornerShape(4.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(Icons.Default.Check, contentDescription = "조건 만족", tint = Color.White, modifier = Modifier.size(12.dp))
//                }
//                Spacer(Modifier.width(4.dp))
//                Text("국문/영문 10자 이하", fontSize = 12.sp, fontFamily = Paperlogy, color = Color(0xFF757575))
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//        }
//
//        // 하단 고정 버튼 (이메일 화면과 동일 위치)
//        val canProceed = isNicknameValid && (isNicknameAvailable == true)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .imePadding()                                   // 키보드 회피
//                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp) // ← 동일 위치
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
//                    signUpViewModel.nickname = nickname
//                    navigator.navigate("sign_up_gender")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text("다음", fontFamily = Paperlogy, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
//        }
//    }
//}
//@Composable
//fun SignUpNicknameScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    var nickname by remember { mutableStateOf("") }
//
//    // ViewModel 상태
//    val isNicknameAvailable by signUpViewModel.isNicknameAvailable.collectAsState()
//    val nicknameMessage by signUpViewModel.nicknameMessage.collectAsState()
//    val isLoading by signUpViewModel.isLoading.collectAsState()
//
//    // 로컬 유효성
//    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 10
//
//    // ✅ 루트를 Box로 변경: 본문과 하단 버튼을 형제로 분리
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // ───── 본문(기존 UI 그대로) ─────
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    start = 20.dp,
//                    end = 20.dp,
//                    top = 52.dp,
//                    // ✅ 하단 버튼 영역만큼 여백 확보 (키보드 미노출시에도 콘텐츠 가림 방지)
//                    bottom = 48.dp + 32.dp + 24.dp
//                ),
//            horizontalAlignment = Alignment.Start
//        ) {
//            ProfileStepIndicator()
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Text(
//                text = "사용하실 닉네임을\n입력해주세요",
//                fontSize = 22.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 닉네임 입력 필드 (그대로)
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
//                    value = nickname,
//                    onValueChange = {
//                        nickname = it
//                        signUpViewModel.nickname = it
//                        if (isNicknameValid) {
//                            signUpViewModel.checkNickname()
//                        }
//                    },
//                    placeholder = {
//                        Text(
//                            "닉네임을 입력해주세요.",
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
//            // 닉네임 중복/실패 메시지 (그대로)
//            if (isNicknameAvailable == false) {
//                Text(
//                    text = "중복된 닉네임 입니다.",
//                    fontSize = 13.sp,
//                    fontFamily = Paperlogy,
//                    color = Color(0xFFFF5E5E),
//                    fontWeight = FontWeight.Normal
//                )
//            }
//            if (nicknameMessage == "서버 요청 실패") {
//                Text(
//                    text = "서버 요청 실패",
//                    fontSize = 13.sp,
//                    fontFamily = Paperlogy,
//                    color = Color(0xFFFF5E5E),
//                    fontWeight = FontWeight.Normal
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(
//                            if (isNicknameValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                            shape = RoundedCornerShape(4.dp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = "조건 만족",
//                        tint = Color.White,
//                        modifier = Modifier.size(12.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "국문/영문 10자 이하",
//                    fontSize = 12.sp,
//                    fontFamily = Paperlogy,
//                    color = Color(0xFF757575)
//                )
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//        }
//
//        // ───── 하단 고정 버튼: 이메일 인증 페이지와 동일한 배치/동작 ─────
//        val canProceed = isNicknameValid && (isNicknameAvailable == true)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .imePadding()                                 // ✅ 키보드 올라오면 버튼도 같이 상승
//                .padding(start = 20.dp, end = 20.dp, bottom = 64.dp) // ✅ 동일 위치
//                .offset(y = -16.dp)                           // ✅ 위로 16 올림(미세 위치 동일화)
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
//                    signUpViewModel.nickname = nickname
//                    navigator.navigate("sign_up_gender")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "다음",
//                fontFamily = Paperlogy,
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}
//@Composable
//fun SignUpNicknameScreen(
//    navigator: NavHostController,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    var nickname by remember { mutableStateOf("") }
//
//    // ViewModel 상태 관찰
//    val isNicknameAvailable by signUpViewModel.isNicknameAvailable.collectAsState()
//    val nicknameMessage by signUpViewModel.nicknameMessage.collectAsState()
//    val isLoading by signUpViewModel.isLoading.collectAsState()
//
//    // 닉네임 입력 유효성 (로컬 조건)
//    val isNicknameValid = nickname.isNotBlank() && nickname.length <= 10
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
//        ProfileStepIndicator()
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Text(
//            text = "사용하실 닉네임을\n입력해주세요",
//            fontSize = 22.sp,
//            fontFamily = Paperlogy,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        //닉네임 입력 필드
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
//                value = nickname,
//                onValueChange = {
//                    nickname = it
//                    signUpViewModel.nickname = it
//                    // 닉네임이 유효할 때만 API 호출 -> 서버 수정 중으로 부득이하게 주석처리, 추후 해지하기.
//                    if (isNicknameValid) {
//                        signUpViewModel.checkNickname()
//                    }
//                },
//                placeholder = {
//                    Text(
//                        "닉네임을 입력해주세요.",
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
//        // 닉네임 중복 메시지 표시 (빨간 글씨)
//        if (isNicknameAvailable == false) {
//            Text(
//                text = "중복된 닉네임 입니다.",
//                fontSize = 13.sp,
//                fontFamily = Paperlogy,
//                color = Color(0xFFFF5E5E),
//                fontWeight = FontWeight.Normal
//            )
//        }
//
//        // 서버 요청 실패 메시지 표시
//        if (nicknameMessage == "서버 요청 실패") {
//            Text(
//                text = "서버 요청 실패",
//                fontSize = 13.sp,
//                fontFamily = Paperlogy,
//                color = Color(0xFFFF5E5E),
//                fontWeight = FontWeight.Normal
//            )
//        }
//
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(
//                modifier = Modifier
//                    .size(20.dp)
//                    .background(
//                        if (isNicknameValid) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
//                        shape = RoundedCornerShape(4.dp)
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "조건 만족",
//                    tint = Color.White,
//                    modifier = Modifier.size(12.dp)
//                )
//            }
//            Spacer(modifier = Modifier.width(4.dp))
//            Text(
//                text = "국문/영문 10자 이하",
//                fontSize = 12.sp,
//                fontFamily = Paperlogy,
//                color = Color(0xFF757575)
//            )
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // 다음 버튼
//        // 닉네임 유효성 + 서버 중복 확인 성공 여부
//        val canProceed = isNicknameValid && (isNicknameAvailable == true)
//
//        //서버 수정 중으로 부득이하게,
//        // 임시방편
//        //val canProceed = isNicknameValid //-> 추후 삭제하고 위의 코드로 교체.
//
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .offset(y = -16.dp) // ✅ 위로 16 올림
//                .padding(bottom = 64.dp) //  하단 여백 이동
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
//                    signUpViewModel.nickname = nickname
//                    navigator.navigate("sign_up_gender")
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "다음",
//                fontFamily = Paperlogy,
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//
//    }
//}
@Composable
fun ProfileStepIndicator() {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 1번 체크 원
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
                    .background(Color(0xFFE5ACF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
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

            // 2번 활성 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2",
                    fontFamily = Paperlogy,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
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

            // 3번 비활성 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3",
                    color = Color(0xFFD6D6D6),
                    fontFamily = Paperlogy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "프로필 설정",
            modifier = Modifier.padding(start = 64.dp, top = 4.dp),
            fontSize = 12.sp,
            fontFamily = Paperlogy,
            color = Color(0xFFCB59EB),
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpNicknameScreenPreview() {
    val fakeNavController = rememberNavController()
    SignUpNicknameScreen(navigator = fakeNavController)
}