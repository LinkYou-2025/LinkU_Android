package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R

@Composable
internal fun PasswordLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "비밀번호",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors
    val shape = RoundedCornerShape(16.dp)
    val strokeWidth = 1.dp   // LoginTextField와 동일
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    var isPasswordVisible by remember { mutableStateOf(false) }
    // value 파라미터와 동기화된 TextFieldValue 관리
    var fieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }

    // value 파라미터가 바뀌면 fieldValue 동기화
    LaunchedEffect(value) {
        if (fieldValue.text != value) {
            fieldValue = TextFieldValue(text = value)
        }
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
                onValueChange = { newValue: TextFieldValue ->
                    fieldValue = newValue
                    onValueChange(newValue.text)
                },

                placeholder = {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorTheme.gray[400]!!
                    )
                },

                textStyle =
                    if (isPasswordVisible) {
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight(500),
                            color = colorTheme.black,
                            letterSpacing = 0.sp
                        )
                    } else {
                        TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = colorTheme.black
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
                    .background(colorTheme.white, shape),
                //.padding(end = (40.scaler)),// 👁 아이콘 공간

                shape = shape,

                colors = TextFieldDefaults.colors(
                    cursorColor = colorTheme.black,
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
                contentDescription = if (isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기", //직관적으로 수정.
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = (18.scaler))
                    .size((22.scaler))
                    .noRippleClickable {  // 수정함.
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

    var password by remember { mutableStateOf("") }
    LinkuPreview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.linkuColors.gray[100]!!)
                .padding((16.scaler))
        ) {
            PasswordLoginTextField(
                value = password,
                onValueChange = { password = it }
            )
        }
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

    LinkuPreview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.linkuColors.gray[100]!!)
                .padding((16.scaler))
        ) {
            // 강제로 눈 열린 상태 확인용
            ThemeProvider {
                PasswordLoginTextField(
                    value = password,
                    onValueChange = { password = it }
                )
            }
        }
    }
}
