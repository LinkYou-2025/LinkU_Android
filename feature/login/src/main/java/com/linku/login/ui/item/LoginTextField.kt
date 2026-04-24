package com.linku.login.ui.item


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

//회원가입 중 입력 텍스트 필드
@Composable
internal fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    enabled: Boolean = true,
    textStyle: TextStyle? = null,
    modifier: Modifier = Modifier
) {

    val colorTheme = MaterialTheme.linkuColors
    val shape = RoundedCornerShape(16.dp)
    val strokeWidth = 1.dp
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

    Box(
        modifier = modifier
            .height((56.scaler))
            .drawBehind {
                // 선의 절반 두께만큼 안쪽으로 좌표를 오프셋 시킴(좌우 테두리 잘림 방지)
                val inset = strokeWidthPx / 2
                drawRoundRect(
                    brush = colorTheme.maincolor,
                    // 시작점 보정
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    // 크기 보정 (양쪽 inset만큼 줄여야 함)
                    size = androidx.compose.ui.geometry.Size(
                        width = size.width - strokeWidthPx,
                        height = size.height - strokeWidthPx
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
                    color = colorTheme.gray[400]
                )
            },

            textStyle = textStyle ?: TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colorTheme.black
            ),

            singleLine = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxSize()
                .background(colorTheme.white, shape),

            shape = shape,

            colors = TextFieldDefaults.colors(
                //테두리 제어
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,

                // 배경색 제어
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // 비활성 상태의 텍스트/힌트 컬러 보정 (너무 흐려지지 않게)
                disabledTextColor = colorTheme.black,
                disabledPlaceholderColor = colorTheme.gray[400],

                )
        )
    }
}


@Preview(
    name = "LoginTextField Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F6F9
)
@Composable
fun LoginTextFieldPreview() {
    val colorTheme = LocalColorTheme.current
    val text = remember { mutableStateOf("") }

    LinkuPreview {
        Column(
            modifier = Modifier
                .background(colorTheme.gray[100])
                .padding((16.scaler))
        ) {
            // 그라데이션 테두리 ON
            LoginTextField(
                value = text.value,
                onValueChange = { text.value = it },
                hint = "이메일을 입력해주세요"
            )

            Spacer(modifier = Modifier.height((16.scaler)))

            // 그라데이션 테두리 OFF
            LoginTextField(
                value = text.value,
                onValueChange = { text.value = it },
                hint = "비밀번호를 입력해주세요"
            )
        }
    }
}