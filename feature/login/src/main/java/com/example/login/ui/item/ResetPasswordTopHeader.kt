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
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler

@Composable
fun ResetPasswordTopHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    // 1. 테마 및 반응형 유틸리티 가져오기
    val colorTheme = LocalColorTheme.current


    // 피그마 기준 해상도(412x917) 대비 반응형 좌표 계산
    val topOffset = (59.scaler)
    val startPadding = (20.scaler)

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
                .width((11.scaler))  //  너비 반응형 적용
                .height((20.scaler))
                .noRippleClickable { onBack() }
        )
    }
}

@Preview(
    name = "ResetPasswordTopHeader Preview",
    showBackground = true
)
@Composable
private fun ResetPasswordTopHeaderPreview() {

    val colorTheme = LocalColorTheme.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) //프리뷰라 굳이 수정하지 않음.
    ) {
        ResetPasswordTopHeader(
            onBack = {}
        )
    }
}
