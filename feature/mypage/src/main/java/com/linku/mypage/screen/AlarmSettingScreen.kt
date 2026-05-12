package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.mypage.R
import com.linku.mypage.component.alarm.NotificationSwitch
import com.linku.mypage.component.alarm.SubNotificationSwitch

@Composable
fun AlarmSettingScreen(
    navController: NavController
) {
    var isAlarmEnabled by remember { mutableStateOf(false) }
    var isLinkActivityEnabled by remember { mutableStateOf(false) }
    var isFolderShareEnabled by remember { mutableStateOf(false) }
    var isAICurationEnabled by remember { mutableStateOf(false) }
    var isNoticeServiceEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(11.dp)
                    .noRippleClickable { navController.popBackStack() }
            )

            Text(
                text = "알림 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.75.dp))

        // 알림 수신 설정 토글
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                ambientShadowColor = Color.Black.copy(alpha = 0.02f)
                spotShadowColor = Color.Black.copy(alpha = 0.02f)
            }
            .clip(RoundedCornerShape(22.dp))
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 20.dp, vertical = 18.dp)

        ) {
            NotificationSwitch(
                title = "알림 수신 설정",
                checked = isAlarmEnabled,
                onCheckedChange = { isChecked ->
                    isAlarmEnabled = isChecked
                    if (isChecked) {
                        isLinkActivityEnabled = true
                        isFolderShareEnabled = true
                        isAICurationEnabled = true
                        isNoticeServiceEnabled = true
                    } else {
                        isLinkActivityEnabled = false
                        isFolderShareEnabled = false
                        isAICurationEnabled = false
                        isNoticeServiceEnabled = false
                    }
                }
            )

            if (isAlarmEnabled) {
                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "링크 활동 알림",
                    checked = isLinkActivityEnabled,
                    onCheckedChange = { checked ->
                        isLinkActivityEnabled = checked
                        if (
                            !checked &&
                            !isFolderShareEnabled &&
                            !isAICurationEnabled &&
                            !isNoticeServiceEnabled
                        ) {
                            isAlarmEnabled = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "폴더 공유 및 권한 알림",
                    checked = isFolderShareEnabled,
                    onCheckedChange = { checked ->
                        isFolderShareEnabled = checked
                        if (
                            !checked &&
                            !isLinkActivityEnabled &&
                            !isAICurationEnabled &&
                            !isNoticeServiceEnabled
                        ) {
                            isAlarmEnabled = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "AI 큐레이션 알림",
                    checked = isAICurationEnabled,
                    onCheckedChange = { checked ->
                        isAICurationEnabled = checked
                        if (
                            !checked &&
                            !isLinkActivityEnabled &&
                            !isFolderShareEnabled &&
                            !isNoticeServiceEnabled
                        ) {
                            isAlarmEnabled = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "공지 및 서비스 알림",
                    checked = isNoticeServiceEnabled,
                    onCheckedChange = { checked ->
                        isNoticeServiceEnabled = checked
                        if (
                            !checked &&
                            !isLinkActivityEnabled &&
                            !isFolderShareEnabled &&
                            !isAICurationEnabled
                        ) {
                            isAlarmEnabled = false
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAlarmSettingScreen() {
    val navController = rememberNavController()
    AlarmSettingScreen(navController = navController)
}
