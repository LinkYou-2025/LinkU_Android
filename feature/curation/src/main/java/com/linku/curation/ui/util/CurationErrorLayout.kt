package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linku.design.theme.linkuColors
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.curation.R
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

@Composable
fun CurationErrorLayout(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    errorMessage: String,
) {
    val colorTheme = MaterialTheme.linkuColors

    CurationFixedGradientBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // 시스템 바텀 바 크기만큼 아래로 내려서 피그마와 맞춘다.
            Spacer(Modifier.height(60.scaler))

            Text(
                text = errorMessage,
                style = LocalTextStyle.current.copy(
                    color = colorTheme.black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    lineHeight = 27.sp,
                    letterSpacing = (-0.839).sp
                ),
            )

            Spacer(Modifier.height(45.scaler))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(brush = colorTheme.maincolor)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 19.scaler, vertical = 11.scaler),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = null,
                    modifier = Modifier.size(14.scaler),
                    tint = Color.Unspecified,
                )

                Spacer(modifier = Modifier.width(6.scaler))

                Text(
                    text = "다시 시도",
                    style = LocalTextStyle.current.copy(
                        color = colorTheme.white,
                        fontWeight = FontWeight(500),
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = (-0.01).sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurationErrorLayoutPreview() {
    LinkuPreview {
        CurationErrorLayout(
            errorMessage = "월간 큐레이션을 불러오지 못했어요",
        )
    }
}
