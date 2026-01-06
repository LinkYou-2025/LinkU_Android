package com.example.login.ui.item


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import com.example.login.R
import com.example.design.modifier.noRippleClickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun ResetPasswordTopHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // 🔑 피그마: Y 위치
    val topOffset = screenHeight * (59f / 917f)
    val startPadding = screenWidth * (20f / 412f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = topOffset,
                start = startPadding
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back_black),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .width(10.dp)
                .height(16.25.dp)
                .noRippleClickable { onBack() }
        )
    }
}

@Preview(
    name = "ResetPasswordTopHeader Preview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ResetPasswordTopHeaderPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        ResetPasswordTopHeader(
            onBack = {}
        )
    }
}
