package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.LocalColorTheme
import com.example.mypage.R

@Composable
fun AlarmSettingScreen(
    navController: NavController
) {
    var isAlarmEnabled by remember { mutableStateOf(false) }
    var isAICurationEnabled by remember { mutableStateOf(false) }
    var isNoticeEventEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .width(10.dp)
                    .clickable { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.width(135.dp))

            Text(
                text = "알림 설정",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = LocalColorTheme.current.black
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 알림 수신 설정 토글
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
        ) {
            NotificationSwitch(
                title = "알림 수신 설정",
                checked = isAlarmEnabled,
                onCheckedChange = { isChecked ->
                    isAlarmEnabled = isChecked
                    if (isChecked) {
                        isAICurationEnabled = true
                        isNoticeEventEnabled = true
                    }
                }
            )

            if (isAlarmEnabled) {

                Spacer(modifier = Modifier.height(20.dp))

                SubNotificationSwitch(
                    title = "AI 큐레이션 알림",
                    checked = isAICurationEnabled,
                    onCheckedChange = { isAICurationEnabled = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                SubNotificationSwitch(
                    title = "공지사항/ 이벤트 알림",
                    checked = isNoticeEventEnabled,
                    onCheckedChange = { isNoticeEventEnabled = it }
                )
            }
        }
    }
}

@Composable
fun NotificationSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onSwitchOn: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = LocalColorTheme.current.black,
            modifier = Modifier.weight(1f)
        )

        CustomSwitch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
                if (it) onSwitchOn?.invoke()
            }
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(46.dp)
            .height(26.dp)
            .graphicsLayer {
                shadowElevation = 2.dp.toPx()
                shape = RoundedCornerShape(12.dp)
                clip = false
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) LocalColorTheme.current.blue[200] else LocalColorTheme.current.gray[200])
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(21.9.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .graphicsLayer {
                    shadowElevation = 2.dp.toPx()
                    shape = CircleShape
                    clip = false
                }
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
        )
    }
}

@Composable
fun SubNotificationSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onSwitchOn: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = LocalColorTheme.current.gray[700],
            modifier = Modifier
                .padding(start = 13.dp)
                .weight(1f)
        )

        CustomSwitch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
                if (it) onSwitchOn?.invoke()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAlarmSettingScreen() {
    val navController = rememberNavController()
    AlarmSettingScreen(navController = navController)
}