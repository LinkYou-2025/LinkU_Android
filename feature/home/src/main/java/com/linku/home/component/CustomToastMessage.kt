package com.linku.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider

@Composable
fun CustomToastMessage(
    backgroundColor: Color,
    textColor: Color,
    toastMessage: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = backgroundColor)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = toastMessage,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = textColor
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewCustomToastMessage() {
    ThemeProvider {
        CustomToastMessage(
            backgroundColor = Color(0xFFE0FBEB),
            textColor = LocalColorTheme.current.positive,
            toastMessage = "유효한 링크입니다!"
        )
    }
}
