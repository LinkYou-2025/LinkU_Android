package com.example.mypage.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.color.Basic

@Composable
fun ServiceQuitModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LocalColorTheme.current.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "탈퇴하시겠습니까?",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = LocalFontTheme.current.font),
            color = LocalColorTheme.current.black,
            modifier = Modifier.padding(top = 45.dp)
        )

        Text(
            text = "회원 탈퇴시 모든 데이터가 삭제되며",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
            color = LocalColorTheme.current.gray[600],
            modifier = Modifier.padding(top = 35.dp)
        )

        Text(
            text = "이후 복구가 불가능하니 신중히 결정해주세요.",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = LocalFontTheme.current.font),
            color = LocalColorTheme.current.gray[600]
        )

        Row(
            modifier = Modifier
                .padding(top = 36.dp, start = 27.dp, end = 27.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, brush = Basic.maincolor), RoundedCornerShape(14.dp))
                    .background(LocalColorTheme.current.white)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "취소하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        brush = Basic.maincolor,  // 그라데이션 Brush 사용
                        fontFamily = LocalFontTheme.current.font
                    ),
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f) // brush 적용 시 필수
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = Basic.maincolor)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "탈퇴하기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = LocalFontTheme.current.font),
                    color = LocalColorTheme.current.white
                )
            }
        }

        Spacer(modifier = Modifier.height(27.92.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewServiceQuitModal() {
    ServiceQuitModal(
        onDismiss = {},
        onConfirm = {}
    )
}