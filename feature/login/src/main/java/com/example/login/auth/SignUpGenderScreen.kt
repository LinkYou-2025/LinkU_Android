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
fun SignUpGenderScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    // 성별 선택 상태: 1 = 남성, 2 = 여성
    var selectedGender by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        // 하단 "다음" 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (selectedGender != null)
                            listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                        else
                            listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(enabled = selectedGender != null) {
                    //  ViewModel에 선택된 성별 저장
                    signUpViewModel.gender = selectedGender ?: 1

                    // 다음 화면으로 이동
                    navigator.navigate("sign_up_job")
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

@Composable
fun GenderOptionButton(
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
                // 텍스트
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    color = Color.Black
                )

                // 선택된 경우에만 체크박스 표시
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
fun SignUpGenderScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakeViewModel = remember { SignUpViewModel() } // 직접 생성 (미리보기용)

    SignUpGenderScreen(
        navigator = fakeNavController,
        signUpViewModel = fakeViewModel
    )
}
