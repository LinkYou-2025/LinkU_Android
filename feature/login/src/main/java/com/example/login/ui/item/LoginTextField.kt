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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke

//회원가입 중 입력 텍스트 필드
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    enabled: Boolean = true,
    textStyle: TextStyle? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val strokeWidth = 1.dp

    Box(
        modifier = modifier
            .height(56.dp)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF2C6FFF),
                            Color(0xFFC800FF)
                        )
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(width = strokeWidth.toPx())
                )
            }
            .padding(strokeWidth) // stroke 공간 확보
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

            textStyle = textStyle ?: TextStyle(
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

//@Composable
//fun LoginTextField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    hint: String,
//    enabled: Boolean = true,
//    modifier: Modifier = Modifier
//) {
//    val shape = RoundedCornerShape(16.dp)
//
//    Box(
//        modifier = modifier
//            //.fillMaxWidth()
//            .height(56.dp)
//            .background(
//                brush = Brush.horizontalGradient(
//                    listOf(
//                        Color(0xFF2C6FFF),
//                        Color(0xFFC800FF)
//                    )
//                ),
//                shape = shape
//            )
//            .padding(1.dp) // 테두리 두께
//    ) {
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//
//            placeholder = {
//                Text(
//                    text = hint,
//                    fontSize = 14.sp,
//                    lineHeight = 20.sp,
//                    fontWeight = FontWeight.Medium,
//                    fontFamily = Paperlogy,
//                    color = Color(0xFFB7B9BF)
//                )
//            },
//
//            textStyle = TextStyle(
//                fontSize = 14.sp,
//                fontFamily = Paperlogy,
//                fontWeight = FontWeight.Normal
//            ),
//
//            singleLine = true,
//            enabled = enabled,
//
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White, shape),
//
//            shape = shape,
//
//            colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedContainerColor = Color.Transparent,
//                unfocusedContainerColor = Color.Transparent
//            )
//        )
//    }
//}

@Preview(
    name = "LoginTextField Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F6F9
)
@Composable
fun LoginTextFieldPreview() {
    val text = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            //.fillMaxWidth()
            .padding(16.dp)
    ) {
        // 그라데이션 테두리 ON
        LoginTextField(
            value = text.value,
            onValueChange = { text.value = it },
            hint = "이메일을 입력해주세요"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 그라데이션 테두리 OFF
        LoginTextField(
            value = text.value,
            onValueChange = { text.value = it },
            hint = "비밀번호를 입력해주세요"
        )
    }
}