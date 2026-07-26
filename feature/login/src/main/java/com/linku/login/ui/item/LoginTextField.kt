package com.linku.login.ui.item


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    modifier: Modifier = Modifier,
    hint: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChanged: (Boolean) -> Unit = {},
) {

    val colorTheme = MaterialTheme.linkuColors
    val shape = RoundedCornerShape(18.dp)
    val strokeWidth = 1.dp
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = (372.scaler)) // 너비 반응형 적용
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
            .background(colorTheme.white, shape)
            // 텍스트 자체에 패딩을 줘서 높이가 콘텐츠에 맞춰 보장되도록 함 (고정 height 제거)
            .padding(start = 22.scaler, end = 21.scaler, top = 18.scaler, bottom = 18.scaler),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colorTheme.gray[400]
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(500),
                color = colorTheme.black
            ),
            singleLine = true,
            enabled = enabled,
            cursorBrush = SolidColor(colorTheme.black),
            // 키보드 타입을 명시하지 않으면 IME 자동완성/맞춤법 검사 밑줄이 표시됨(PasswordLoginTextField와 동일 이슈).
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                autoCorrectEnabled = false
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChanged(it.isFocused) }
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
                .fillMaxWidth()
                .background(colorTheme.gray[100])
                .padding(horizontal = (20.scaler), vertical = (16.scaler)) // 좌우 여백 20
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