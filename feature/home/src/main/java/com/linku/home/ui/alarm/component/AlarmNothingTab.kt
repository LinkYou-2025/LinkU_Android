package com.linku.home.ui.alarm.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.home.R

@Composable
fun AlarmNothingTab(
    isVisible: Boolean = true
) {
    if (!isVisible) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 17.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 2.dp)
            )

            Text(
                text = "조용하네요👀\n새로운 알림이 오면 여기에 알려드릴게요",
                style = LocalTextStyle.current.copy(
                    color = LocalColorTheme.current.gray[600],
                    fontSize = 14.sp,
                ),
                modifier = Modifier
                    .padding(start = 12.dp),
            )
        }

    }
}

@Preview
@Composable
fun PreviewAlarmNothingTab() {
    AlarmNothingTab()
}