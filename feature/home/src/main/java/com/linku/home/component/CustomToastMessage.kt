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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import kotlinx.coroutines.delay

@Composable
fun CustomToastMessage(
    backgroundColor: Color,
    textColor: Color,
    toastMessage: String,
    modifier: Modifier = Modifier
) {
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

@Composable
fun TimedCustomToastMessage(
    visible: Boolean,
    backgroundColor: Color,
    textColor: Color,
    toastMessage: String,
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
            backgroundColor = backgroundColor,
            textColor = textColor,
            toastMessage = toastMessage
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewCustomToastMessage() {
    val colors = MaterialTheme.linkuColors

    ThemeProvider {
        CustomToastMessage(
            backgroundColor = Color(0xFFE0FBEB),
            textColor = colors.positive,
            toastMessage = "유효한 링크입니다!"
        )
    }
}