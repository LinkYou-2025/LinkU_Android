package com.linku.home.ui.alarm.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.linku.design.R as Res

@Composable
fun AlarmSettingTab(
    onClick: () -> Unit,
    isVisible: Boolean = true
) {
    if (!isVisible) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
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
                text = "알림 받기를 설정하고 유용한 알림들을\n받아보세요.",
                color = LocalColorTheme.current.gray[600],
                modifier = Modifier
                    .padding(start = 12.dp),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier.size(14.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_right),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlarmSettingTab() {
    AlarmSettingTab(onClick = {})
}