package com.linku.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS,
    ERROR
}

/**
 * 토스트 메시지의 UI만 담당하는 컴포넌트입니다.
 *
 * [toastType] 값에 따라 성공/실패 상태에 맞는 배경색과 텍스트 색상을 적용합니다.
 * 노출 여부나 자동 dismiss 처리는 담당하지 않기 때문에,
 * 항상 화면에 보여줄 토스트 UI가 필요할 때 사용합니다.
 */
@Composable
fun CustomToastMessage(
    toastMessage: String,
    toastType: ToastType,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    val backgroundColor = when (toastType) {
        ToastType.SUCCESS -> colors.positiveBg
        ToastType.ERROR -> colors.negativeBg
    }

    val textColor = when (toastType) {
        ToastType.SUCCESS -> colors.positive
        ToastType.ERROR -> colors.negative
    }

    Row(
        modifier = modifier
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

/**
 * 일정 시간이 지나면 자동으로 사라지는 토스트 메시지 컴포넌트입니다.
 *
 * [visible] 값이 true가 되면 [CustomToastMessage]를 표시하고,
 * [delayMillis] 시간이 지난 뒤 [onDismiss]를 호출해 외부 상태를 false로 변경하도록 합니다.
 *
 * 잠깐 보여주고 사라지는 성공/실패 안내 메시지에 사용합니다.
 * 실제 사용 예시는 링크 유효성 검사를 참고해주세요!
 */
@Composable
fun TimedCustomToastMessage(
    visible: Boolean,
    toastMessage: String,
    toastType: ToastType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    delayMillis: Long = 3_000L,
) {
    LaunchedEffect(visible, toastMessage) {
        if (visible) {
            delay(delayMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier
    ) {
        CustomToastMessage(
            toastMessage = toastMessage,
            toastType = toastType
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewCustomToastMessageSuccess() {
    ThemeProvider {
        CustomToastMessage(
            toastMessage = "유효한 링크입니다!",
            toastType = ToastType.SUCCESS
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewCustomToastMessageError() {
    ThemeProvider {
        CustomToastMessage(
            toastMessage = "유효하지 않은 링크입니다!",
            toastType = ToastType.ERROR
        )
    }
}