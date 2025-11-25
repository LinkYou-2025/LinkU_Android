package com.example.login.auth


import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SignUpJobScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    var selectedJobIndex by remember { mutableStateOf(
        if (signUpViewModel.jobId > 0) signUpViewModel.jobId - 1 else null
    ) }
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    val isButtonEnabled = selectedJobIndex != null

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        // ✅ 닉네임/성별과 동일한 바텀 패딩 계산
        val density = LocalDensity.current
        val imeBottomPx = WindowInsets.ime.getBottom(density)
        val isImeVisible = imeBottomPx > 0
        val bottomGapWhenIme = 4.dp
        val bottomGapDefault = 16.dp
        val bottomPadding = if (isImeVisible) bottomGapWhenIme else bottomGapDefault

        // 본문 (버튼과 겹치지 않게 하단 여유 48+24)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 52.dp,
                    bottom = 48.dp + 24.dp
                ),
            horizontalAlignment = Alignment.Start
        ) {
            ProfileStepIndicator()
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            jobs.forEachIndexed { index, job ->
                JobOptionButton(
                    text = job,
                    isSelected = selectedJobIndex == index,
                    onClick = {
                        selectedJobIndex = index
                        signUpViewModel.jobId = index + 1
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 하단 고정 버튼 (닉네임 화면과 동일)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)   // BoxScope.align
                //.imePadding()
                //.navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp,bottom = bottomPadding) //bottom = 16.dp)
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
                    signUpViewModel.jobId = (selectedJobIndex ?: 0) + 1
                    navigator.navigate("sign_up_purpose") { launchSingleTop = true }
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
fun JobOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSelected) Color(0xFFF0E8FF) else Color.White,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                    //.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 직업명 텍스트
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )

                // 선택되었을 경우 체크 박스 표시
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFCB59EB), shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "선택됨",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F6F9,
    name = "직업 선택 전체 화면"
)
@Composable
fun SignUpJobScreenPreview() {
    val fakeNavController = rememberNavController()
    SignUpJobScreenPreviewOnly(navigator = fakeNavController)
}


//ui 확인용. 철저히 프리뷰용.
@Composable
private fun SignUpJobScreenPreviewOnly(navigator: NavHostController) {
    var selectedJobIndex by remember { mutableStateOf(2) } // "직장인" 선택 예시
    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    val isButtonEnabled = selectedJobIndex != null

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 72.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ProfileStepIndicator()
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "현재 하고 계신 일이나\n활동을 알려주세요",
                fontSize = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            jobs.forEachIndexed { index, job ->
                JobOptionButton(
                    text = job,
                    isSelected = selectedJobIndex == index,
                    onClick = { selectedJobIndex = index }
                )
                Spacer(modifier = Modifier.height(12.dp))
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

