package com.linku.home.ui.alarm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.home.R

@Composable
fun AlarmErrorFooter(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(LocalColorTheme.current.white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.size(100.dp))

        // 안내 문구
        Text(
            text = "추가 알림을 불러오지 못했어요",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = LocalColorTheme.current.black,
                fontFamily = LocalFontTheme.current.font
            )
        )

        Spacer(Modifier.size(16.dp))

        // 다시 시도 버튼
        TextButton(
            onClick = onClick,
            modifier = Modifier.size(110.dp, 36.dp)
                .background(
                brush = LocalColorTheme.current.maincolor,
                shape = RoundedCornerShape(50)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                tint = Color.Unspecified,
                contentDescription = null,
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "다시 시도",
                style = TextStyle(
                    color = LocalColorTheme.current.white,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalFontTheme.current.font
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmErrorFooterPreview() {
    LinkuPreview {
        AlarmErrorFooter(onClick = {})
    }
}
