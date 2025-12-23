package com.example.login.ui.item


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.Paperlogy

//회원가입 중 입력 텍스트 필드
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    showGradientBorder: Boolean = true // ⭐ 핵심
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = if (showGradientBorder)
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
                    )
                else
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                shape = shape
            )
            .padding(1.dp) // ⭐ 테두리 두께
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,

            placeholder = {
                Text(
                    text = hint,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Paperlogy,
                    color = Color(0xFFB7B9BF)
                )
            },

            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Normal
            ),

            singleLine = true,
            enabled = enabled,

            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape),

            shape = shape,

            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}