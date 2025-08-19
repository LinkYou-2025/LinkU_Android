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
    // 선택된 직업 인덱스 (1부터 시작)
    var selectedJobIndex by remember { mutableStateOf<Int?>(null) }

    val jobs = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
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

        // 직업 목록 버튼
        jobs.forEachIndexed { index, job ->
            JobOptionButton(
                text = job,
                isSelected = selectedJobIndex == index,
                onClick = { selectedJobIndex = index }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (selectedJobIndex != null)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(enabled = selectedJobIndex != null) {
                    // ViewModel에 선택된 직업 index 저장 (1부터 시작)
                    signUpViewModel.jobId = (selectedJobIndex ?: 0) + 1

                    // 다음 화면으로 이동
                    navigator.navigate("sign_up_purpose") // 다음 라우트에 맞게 수정
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                fontFamily = Paperlogy,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
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
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 직업명 텍스트
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    color = Color.Black
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

@Preview(showBackground = true)
@Composable
fun SignUpJobScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakeViewModel = remember { SignUpViewModel() }

    SignUpJobScreen(
        navigator = fakeNavController,
        signUpViewModel = fakeViewModel
    )
}