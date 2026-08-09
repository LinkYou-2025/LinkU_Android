package com.linku.curation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onRetry: () -> Unit = {},
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    val colorTheme = MaterialTheme.linkuColors

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = errorMessage,
            style = LocalTextStyle.current.copy(
                color = colorTheme.black,
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
            ),
        )

        Spacer(Modifier.height(45.scaler))

        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .height(36.scaler)
                .background(
                    brush = colorTheme.maincolor,
                    shape = RoundedCornerShape(50),
                ),
            contentPadding = PaddingValues(horizontal = 12.scaler),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = null,
                modifier = Modifier.size(14.scaler),
                tint = Color.Unspecified,
            )

            Spacer(modifier = Modifier.width(8.scaler))

            Text(
                text = "다시 시도",
                style = LocalTextStyle.current.copy(
                    color = colorTheme.white,
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                )
            )
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
