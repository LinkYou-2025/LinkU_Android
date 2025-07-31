package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.mypage.R
import com.example.mypage.component.ServiceQuitModal

@Composable
fun ServiceQuitScreen() {
    var username by remember { mutableStateOf("세나") }

    var reasonText by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.width(10.dp)
                )

                Spacer(modifier = Modifier.width(135.dp))

                Text(
                    text = "회원 탈퇴",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black
                )
            }

            Spacer(modifier = Modifier.height(41.75.dp))

            Text(
                text = "${username}님, 서비스를 탈퇴하시겠어요?",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = LocalColorTheme.current.black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "그동안 링큐 서비스를 이용해주셔서 진심으로 감사해요.",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                color = LocalColorTheme.current.black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = buildAnnotatedString {
                    append("회원 탈퇴를 진행하시면,\n10일 뒤에 계정 정보와 함께 ")
                    withStyle(style = SpanStyle(color = LocalColorTheme.current.negative)) {
                        append("저장하신 링크, 폴더, 감정/상황 기록 등 모든 데이터가 완전히 삭제")
                    }
                    append("되며 삭제된 데이터는 ")
                    withStyle(style = SpanStyle(color = LocalColorTheme.current.negative)) {
                        append("복구가 불가능")
                    }

                    append("해요.")
                },
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                color = LocalColorTheme.current.gray[800],
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "조금 더 고민해보고 싶으시다면, 언제든지 돌아오셔도 괜찮아요.",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                color = LocalColorTheme.current.gray[800],
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "정말로 서비스를 탈퇴하시겠어요?",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                color = LocalColorTheme.current.black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "떠나시는 이유를 자유롭게 작성해주세요.",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                color = LocalColorTheme.current.black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LocalColorTheme.current.gray[100])
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                if (reasonText.isBlank()) {
                    Text(
                        text = "회원님의 소중한 피드백을 통해\n링큐는 더 나은 서비스를 제공해드릴 수 있어요.",
                        color = LocalColorTheme.current.gray[400],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                BasicTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    textStyle = TextStyle(
                        color = LocalColorTheme.current.black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = Basic.maincolor)
                    .clickable { showDialog = true }, // ✅ 클릭 시 모달 표시
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "탈퇴하기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)) // 40% 투명한 검정색 배경
                .zIndex(1f)
                .clickable(enabled = false) {}, // 외부 클릭 막기
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                ServiceQuitModal(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        showDialog = false
                        // 실제 탈퇴 로직 호출
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewServiceQuitScreen() {
    ServiceQuitScreen()
}