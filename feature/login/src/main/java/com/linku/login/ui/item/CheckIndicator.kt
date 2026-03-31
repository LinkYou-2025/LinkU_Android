package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linku.login.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CheckIndicator(
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(18.dp)
            .background(
                color = if (checked) Color(0xFFCB59EB) else Color(0xFFD7D9DF),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_login_check),
            contentDescription = if (checked) "checked" else "unchecked",
            modifier = Modifier
                .width(11.dp) //실제 기기상 작아보여 피그마 대비 사이즈를 키움.
                .height(8.dp)
        )

    }
}



@Preview(
    name = "CheckIndicator - States",
    showBackground = true
)
@Composable
private fun CheckIndicatorPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CheckIndicator(checked = true)   // 보라 배경 + 체크
        CheckIndicator(checked = false)  // 회색 배경 + 체크
    }
}