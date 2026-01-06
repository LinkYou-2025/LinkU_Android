package com.example.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.Paperlogy
import com.example.login.R
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswordLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "비밀번호",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val strokeWidth = 1.dp   // LoginTextField와 동일
    var isPasswordVisible by remember { mutableStateOf(false) }

    var fieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
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
            .padding(strokeWidth) //  stroke 공간 확보
    ) {
        Box {
            OutlinedTextField(
                value = fieldValue,
                onValueChange = { newValue ->
                    val fixedValue = newValue.copy(composition = null)
                    fieldValue = fixedValue
                    onValueChange(fixedValue.text)
                },

                placeholder = {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFFB7B9BF)
                    )
                },

                textStyle =
                    if (isPasswordVisible) {
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = Paperlogy,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF000208),
                            letterSpacing = 0.sp
                        )
                    } else {
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Paperlogy,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    },

                singleLine = true,
                enabled = enabled,

                visualTransformation =
                    if (isPasswordVisible)
                        VisualTransformation.None
                    else
                        DotPasswordVisualTransformation(),

                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, shape)
                    .padding(end = 40.dp), // 👁 아이콘 공간

                shape = shape,

                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            // 👁 눈 아이콘
            Image(
                painter = painterResource(
                    if (isPasswordVisible)
                        R.drawable.ic_password_visibility_on
                    else
                        R.drawable.ic_password_visibility_off
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 18.dp)
                    .size(22.dp)
                    .clickable {
                        isPasswordVisible = !isPasswordVisible
                    }
            )
        }
    }
}


//@Composable
//fun PasswordLoginTextField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    hint: String = "비밀번호",
//    enabled: Boolean = true,
//    modifier: Modifier = Modifier
//) {
//    val shape = RoundedCornerShape(16.dp)
//    var isPasswordVisible by remember { mutableStateOf(false) }
//
//    var fieldValue by remember {
//        mutableStateOf(TextFieldValue(text = value))
//    }
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(56.dp)
//            .background(
//                Brush.horizontalGradient(
//                    listOf(Color(0xFF2C6FFF), Color(0xFFC800FF))
//                ),
//                shape
//            )
//            .padding(1.dp)
//    ) {
//        Box {
//            OutlinedTextField(
//                value = fieldValue,
//                onValueChange = { newValue ->
//                    val fixedValue = newValue.copy(composition = null) //밑줄 방지
//                    fieldValue = fixedValue
//                    onValueChange(fixedValue.text)
//                },
//
//                placeholder = {
//                    Text(
//                        text = hint,
//                        fontSize = 14.sp,
//                        fontFamily = Paperlogy,
//                        color = Color(0xFFB7B9BF)
//                    )
//                },
//
//                textStyle =
//                    if (isPasswordVisible) {
//                        // 👁 비밀번호 보임 상태 (일반 텍스트)
//                        TextStyle(
//                            fontSize = 14.sp,
//                            lineHeight = 16.sp,
//                            fontFamily = Paperlogy,
//                            fontWeight = FontWeight.Medium,
//                            color = Color(0xFF000208),
//                            letterSpacing = 0.sp
//                        )
//                    } else {
//                        //  비밀번호 숨김 상태 (닷)
//                        TextStyle(
//                            fontSize = 14.sp,
//                            fontFamily = Paperlogy,
//                            fontWeight = FontWeight.Bold,
//                            letterSpacing = 2.sp
//                        )
//                    },
//
//                singleLine = true,
//                enabled = enabled,
//
//                visualTransformation =
//                    if (isPasswordVisible)
//                        VisualTransformation.None
//                    else
//                        DotPasswordVisualTransformation(),
//
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White, shape)
//                    .padding(end = 40.dp),
//
//                shape = shape,
//
//                colors = TextFieldDefaults.colors(
//                    cursorColor = Color.Black,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent
//                )
//            )
//
//            // 👁 눈 아이콘
//            Image(
//                painter = painterResource(
//                    if (isPasswordVisible)
//                        R.drawable.ic_password_visibility_on
//                    else
//                        R.drawable.ic_password_visibility_off
//                ),
//                contentDescription = null,
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .padding(end = 18.dp)
//                    .size(22.dp)
//                    .clickable {
//                        isPasswordVisible = !isPasswordVisible
//                    }
//            )
//        }
//    }
//}

//커스텀 닷
class DotPasswordVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val dot = "●"
        return TransformedText(
            AnnotatedString(dot.repeat(text.length)),
            OffsetMapping.Identity
        )
    }
}



//프리뷰 추가
@Preview(
    name = "PasswordLoginTextField - Hidden",
    showBackground = true,
    backgroundColor = 0xFFF5F6F9
)
@Composable
private fun PasswordLoginTextFieldHiddenPreview() {
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PasswordLoginTextField(
            value = password,
            onValueChange = { password = it }
        )
    }
}

@Preview(
    name = "PasswordLoginTextField - Visible",
    showBackground = true,
    backgroundColor = 0xFFF5F6F9
)
@Composable
private fun PasswordLoginTextFieldVisiblePreview() {
    var password by remember { mutableStateOf("password123") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 강제로 눈 열린 상태 확인용
        CompositionLocalProvider {
            PasswordLoginTextField(
                value = password,
                onValueChange = { password = it }
            )
        }
    }
}
