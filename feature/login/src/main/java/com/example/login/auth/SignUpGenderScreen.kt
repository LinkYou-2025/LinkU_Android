package com.example.login.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.login.R
import com.example.login.Paperlogy


@Composable
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    // 성별 선택 상태: 1 = 남성, 2 = 여성
    var selectedGender by remember { mutableStateOf<Int?>(null) }

    //var selectedGender by remember { mutableStateOf<Int?>(null) }
    val isButtonEnabled = selectedGender != null

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ 닉네임 화면과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0
        val bottomGapWhenIme = 4.dp     // 키보드 보일 때
        val bottomGapDefault = 16.dp    // 키보드 없을 때(시작 지점)
        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 52.dp,   // ⬆️ 위쪽만 52
                bottom = 48.dp + 24.dp // ⬇️ 아래는 40 유지
            ),
            //.padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 상단 프로필 단계 표시
        ProfileStepIndicator()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "성별을\n선택해주세요",
            fontSize = 22.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 선택 옵션: 남성
        GenderOptionButton(
            text = "남성",
            isSelected = selectedGender == 1,
            onClick = { selectedGender = 1 }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 선택 옵션: 여성
        GenderOptionButton(
            text = "여성",
            isSelected = selectedGender == 2,
            onClick = { selectedGender = 2 }
        )

        Spacer(modifier = Modifier.weight(1f))}

        // ✅ 하단 고정 버튼 (닉네임 화면과 동일)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                //.imePadding()
                //.navigationBarsPadding()
                //.padding(start = 20.dp, end = 20.dp,  bottom = bottomPadding)
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
                )
                .clickable(enabled = isButtonEnabled) {
                    signUpViewModel.gender = selectedGender ?: 1
                    navigator.navigate("sign_up_job") {
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
//shape = RoundedCornerShape(18.dp)

@Composable
fun GenderOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderGradient = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))

    // 요구: 왼쪽도 12% → 오른쪽도 12% (동일 투명도)
    val fillGradientSelected = listOf(
        Color(0xFF2C6FFF).copy(alpha = 0.18f),
        Color(0xFFC800FF).copy(alpha = 0.16f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            // 1) 기본 흰 바탕
            .background(Color.White)
            // 2) 선택 시 전체 영역 그라데이션(패딩 이전에 칠함)
            .then(
                if (isSelected)
                    Modifier.background(brush = Brush.horizontalGradient(fillGradientSelected))
                else
                    Modifier
            )
            // 3) 테두리는 맨 위에
            .border(width = 1.dp, brush = Brush.horizontalGradient(borderGradient), shape = shape)
            .clickable(onClick = onClick)
            // 4) 내용 패딩은 마지막에
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontFamily = Paperlogy,
                color = Color.Black
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFCB59EB), shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "선택됨",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F6F9,
    name = "성별 선택 - 선택된 버튼만"
)
@Composable
fun GenderOptionButtonPreview_Selected() {
    Box(Modifier.padding(16.dp)) {
        GenderOptionButton(
            text = "여성",
            isSelected = true,
            onClick = {}
        )
    }
}


//@Composable
//fun GenderOptionButton(
//    text: String,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp)
//            .background(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                ),
//                shape = RoundedCornerShape(16.dp)
//            )
//            .padding(1.dp)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    if (isSelected) Color(0xFFF0E8FF) else Color.White,
//                    shape = RoundedCornerShape(16.dp)
//                ),
//            contentAlignment = Alignment.CenterStart
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
////                    //.fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // 텍스트
//                Text(
//                    text = text,
//                    fontSize = 13.sp,
//                    fontFamily = Paperlogy,
//                    color = Color.Black
//                )
//
//                // 선택된 경우에만 체크박스 표시
//                if (isSelected) {
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(Color(0xFFCB59EB), shape = RoundedCornerShape(4.dp)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = "선택됨",
//                            tint = Color.White,
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun SignUpGenderScreenPreview() {
//    val fakeNavController = rememberNavController()
//    val fakeViewModel = remember { SignUpViewModel() } // 직접 생성 (미리보기용)
//
//    SignUpGenderScreen(
//        navigator = fakeNavController,
//        signUpViewModel = fakeViewModel
//    )
//}
