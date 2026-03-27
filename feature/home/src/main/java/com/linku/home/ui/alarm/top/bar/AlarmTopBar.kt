package com.linku.home.ui.alarm.top.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.home.R

@Composable
fun AlarmTopBar(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "알림함",
            color = LocalColorTheme.current.black,
            fontSize = 16.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .width(11.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(23.dp))

            Image(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onHomeClick() }
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlarmTopBar() {
    AlarmTopBar(
        onBack = {},
        onHomeClick = {}
    )
}