package com.linku.mypage.screen

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.linku.design.modifier.noRippleClickable
import com.linku.design.modifier.skeleton
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.linku.core.model.alarm.AlarmSetting
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.AlarmSettingUiState
import com.linku.mypage.NotificationEffect
import com.linku.mypage.NotificationIntent
import com.linku.mypage.NotificationViewModel
import com.linku.mypage.R
import com.linku.mypage.component.notification.NotificationSwitch
import com.linku.mypage.component.notification.SubNotificationSwitch
import com.linku.mypage.component.notification.SystemAlarmTab

@Composable
fun AlarmSettingScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.notificationState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 에러 발생시 Toast를 띄우는 Side Effect
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NotificationEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 액티비티가 ON_RESUME될 때마다
    // 시스템 알림 설정 상태를 최신 값으로 갱신
    // 사용자가 시스템 알람 화면에서 알람 설정을 바꾸고 앱으로 돌아오는 상황에 대응
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.sendIntent(NotificationIntent.RefreshSystemAlarm)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AlarmSettingScreenContent(
        state = state,
        onBackClick = { navController.popBackStack() },
        onIntent = viewModel::sendIntent // Intent가 발생하면
    )
}

@Composable
private fun AlarmSettingScreenContent(
    state: AlarmSettingUiState,
    onBackClick: () -> Unit,
    onIntent: (NotificationIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
            .verticalScroll(rememberScrollState())
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
                    .noRippleClickable { onBackClick() }
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

        val context = LocalContext.current
        SystemAlarmTab(
            onClick = {
                // 기기 알람 설정 화면으로 이동
                val intent = Intent(
                    Settings.ACTION_APP_NOTIFICATION_SETTINGS
                ).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            },
            isSystemAlarmAllowed = state.isSystemAlarmAllowed,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))


        // 알림 수신 설정 토글
        Column(
            modifier = Modifier
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
                .skeleton(isLoading = state.isLoading)
        ) {
            NotificationSwitch(
                title = "모든 푸시 알림",
                checked = state.alarmToggleUiState.isAllEnabled,
                onCheckedChange = { onIntent(NotificationIntent.ToggleAll(it)) }
            )

            if (state.alarmToggleUiState.isAllEnabled) {
                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "링크 활동 알림",
                    checked = state.alarmToggleUiState.isLinkEnabled,
                    onCheckedChange = { onIntent(NotificationIntent.ToggleLink(it)) }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "폴더 공유 및 권한 알림",
                    checked = state.alarmToggleUiState.isFolderEnabled,
                    onCheckedChange = { onIntent(NotificationIntent.ToggleFolder(it)) }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "AI 큐레이션 알림",
                    checked = state.alarmToggleUiState.isCurationEnabled,
                    onCheckedChange = { onIntent(NotificationIntent.ToggleCuration(it)) }
                )

                Spacer(modifier = Modifier.height(15.dp))

                SubNotificationSwitch(
                    title = "공지 및 서비스 알림",
                    checked = state.alarmToggleUiState.isNoticeEnabled,
                    onCheckedChange = { onIntent(NotificationIntent.ToggleNotice(it)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmSettingScreenPreview() {
    ThemeProvider {
        AlarmSettingScreenContent(
            state = AlarmSettingUiState(
                isSystemAlarmAllowed = true,
                alarmToggleUiState = AlarmSetting(
                    isAllEnabled = true,
                    isLinkEnabled = true,
                    isFolderEnabled = true,
                    isCurationEnabled = true,
                    isNoticeEnabled = true
                )
            ),
            onBackClick = {},
            onIntent = {}
        )
    }
}
