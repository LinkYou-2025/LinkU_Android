package com.example.linku_android.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linku_android.component.Paperlogy


@Preview(showBackground = true)
@Composable
fun EmailVerificationScreen() {
    val isPreview = LocalInspectionMode.current //폰트 표시
 

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 상단 단계 표시
        StepIndicator()

        Spacer(modifier = Modifier.height(32.dp))

        // 타이틀
        Text(
            text = "가입을 위한 이메일 주소를\n인증해주세요",
            fontSize = 18.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start,

        )

        Spacer(modifier = Modifier.height(32.dp))

        // 이메일 입력 필드
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
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        "이메일 주소를 입력해주세요.",
                        fontSize = 13.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Normal,
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

        Spacer(modifier = Modifier.weight(1f))

        // 하단 인증메일 발송 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "인증메일 발송",
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Bold,

            )
        }
    }
}

@Composable
fun StepIndicator() {
    val isPreview = LocalInspectionMode.current //폰트 표시

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
                    .background(Color(0xFFCB59EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = Paperlogy,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ... 점선
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFFD6D6D6), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 2번 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("2", color = Color(0xFFD6D6D6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
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

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, Color(0xFFD6D6D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("3", color = Color(0xFFD6D6D6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        //  1번 원 바로 아래에 계정 정보
        Text(
            text = "계정 정보",
            modifier = Modifier.padding(start = 0.dp, top = 4.dp),
            fontSize = 12.sp,
            color = Color(0xFFCB59EB),
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light,
        )
    }




}

//
//@Preview(showBackground = true)
//@Composable
//fun EmailVerificationScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 32.dp, vertical = 48.dp),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column {
//            // 🔵 진행 단계 Indicator
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // Step 1 - 활성
//                    Box(
//                        modifier = Modifier
//                            .size(48.dp)
//                            .background(Color(0xFFCB59EB), shape = CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "1",
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Dot 3개
//                    repeat(3) {
//                        Box(
//                            modifier = Modifier
//                                .size(6.dp)
//                                .background(Color(0xFFCFD3DC), shape = CircleShape)
//                        )
//                        if (it < 2) Spacer(modifier = Modifier.width(4.dp))
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Step 2 - 비활성
//                    Box(
//                        modifier = Modifier
//                            .size(48.dp)
//                            .border(2.dp, Color(0xFFCFD3DC), shape = CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "2",
//                            color = Color(0xFFCFD3DC),
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Dot 3개
//                    repeat(3) {
//                        Box(
//                            modifier = Modifier
//                                .size(6.dp)
//                                .background(Color(0xFFCFD3DC), shape = CircleShape)
//                        )
//                        if (it < 2) Spacer(modifier = Modifier.width(4.dp))
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Step 3 - 비활성
//                    Box(
//                        modifier = Modifier
//                            .size(48.dp)
//                            .border(2.dp, Color(0xFFCFD3DC), shape = CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "3",
//                            color = Color(0xFFCFD3DC),
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "계정 정보",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Color(0xFFCB59EB)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 타이틀
//            Text(
//                text = "가입을 위한 이메일 주소를\n인증해주세요",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                textAlign = TextAlign.Start
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 이메일 입력 필드
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
//                    value = "",
//                    onValueChange = {},
//                    placeholder = {
//                        Text(
//                            "이메일 주소를 입력해주세요.",
//                            fontSize = 13.sp,
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
//        }
//
//        // 하단 인증메일 발송 버튼
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(Color(0xFF9BCBFF), Color(0xFFF4AFFF))
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "인증메일 발송",
//                color = Color.White,
//                fontSize = 13.sp,
//                fontWeight = FontWeight.Medium
//            )
//        }
//    }
//}