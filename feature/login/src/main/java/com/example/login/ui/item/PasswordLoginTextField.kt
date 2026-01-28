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
import com.example.design.theme.font.Paperlogy
import com.example.login.R
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler

@Composable
fun PasswordLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "비밀번호",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    //디자인 모듈.
    val colorTheme = LocalColorTheme.current

    val shape = RoundedCornerShape(16.dp)
    val strokeWidth = 1.dp   // LoginTextField와 동일
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    var isPasswordVisible by remember { mutableStateOf(false) }
    // value 파라미터와 동기화된 TextFieldValue 관리
    var fieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((56.scaler))
            .drawBehind {
                val inset = strokeWidthPx / 2
                drawRoundRect(
                    brush = colorTheme.maincolor,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(
                        width = size.width - strokeWidthPx,
                        height = size.height - strokeWidthPx
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
                        fontFamily = Paperlogy.font,
                        color = colorTheme.gray[400]!!
                    )
                },

                textStyle =
                    if (isPasswordVisible) {
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = Paperlogy.font,
                            fontWeight = FontWeight(500),
                            color = colorTheme.black,
                            letterSpacing = 0.sp
                        )
                    } else {
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Paperlogy.font,
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
                    .background(colorTheme.white, shape)
                    .padding(end = (40.scaler)),// 👁 아이콘 공간

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
                    .padding(end = (18.scaler))
                    .size((22.scaler))
                    .clickable {
                        isPasswordVisible = !isPasswordVisible
                    }
            )
        }
    }
}

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
    val colorTheme = LocalColorTheme.current
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorTheme.gray[100]!!)
            .padding((16.scaler))
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
    val colorTheme = LocalColorTheme.current
    var password by remember { mutableStateOf("password123") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorTheme.gray[100]!!)
            .padding((16.scaler))
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
