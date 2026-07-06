package com.linku.design.component

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

/**
 * 토스트 메시지의 UI만 담당하는 컴포넌트입니다.
 *
 * 배경색은 black,
 * 텍스트 색상은 white로 고정되어 있습니다.
 *
 * 노출 여부나 자동 dismiss 처리는 담당하지 않기 때문에,
 * 항상 화면에 보여줄 토스트 UI가 필요할 때 사용합니다.
 *
 * 자동으로 사라지는 토스트가 필요하다면 [TimedCustomToastMessage]를 사용해주세요.
 */
@Composable
fun CustomToastMessage(
    toastMessage: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = colors.black)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = toastMessage,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = colors.white
        )
    }
}

/**
 * 일정 시간이 지나면 자동으로 사라지는 토스트 메시지 컴포넌트입니다.
 *
 * [visible] 값이 true일 때 [CustomToastMessage]를 표시하고,
 * 3000ms가 지난 뒤 [onDismiss]를 호출합니다.
 *
 * 이 컴포넌트는 토스트 UI와 자동 dismiss만 담당합니다.
 * 토스트 메시지와 노출 여부 상태는 사용하는 화면에서 관리해야 합니다.
 *
 * 사용 예시:
 *
 * ```
 * var toastMessage by remember { mutableStateOf("") }
 * var isToastVisible by remember { mutableStateOf(false) }
 *
 * TimedCustomToastMessage(
 *     visible = isToastVisible,
 *     toastMessage = toastMessage,
 *     onDismiss = { isToastVisible = false },
 *     modifier = Modifier
 *         .align(Alignment.TopCenter)
 *         .padding(top = 86.dp)
 * )
 *
 * // 토스트를 띄울 때
 * toastMessage = "링크가 저장되었어요."
 * isToastVisible = true
 * ```
 *
 * ViewModel에서 이벤트를 받아 표시하는 경우:
 *
 * ```
 * LaunchedEffect(Unit) {
 *     toastEvent.collect { event ->
 *         toastMessage = event.message
 *         isToastVisible = true
 *     }
 * }
 * ```
 *
 * [toastMessage]는 null을 허용하지 않습니다.
 * 토스트를 숨길 때 빈 문자열이나 null로 메시지를 관리하지 말고,
 * [visible] 값을 false로 변경해 노출 여부만 제어하는 것을 권장합니다.
 */
@Composable
fun TimedCustomToastMessage(
    visible: Boolean,
    toastMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(visible, toastMessage) {
        if (visible) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier
    ) {
        CustomToastMessage(
            toastMessage = toastMessage
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun PreviewCustomToastMessage() {
    ThemeProvider {
        CustomToastMessage(
            toastMessage = "링크가 저장되었습니다!"
        )
    }
}