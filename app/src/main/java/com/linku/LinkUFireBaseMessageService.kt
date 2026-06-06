package com.linku

import android.app.NotificationManager
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linku.core.repository.AlarmRepository
import com.linku.data.preference.NotificationPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LinkUFireBaseMessageService : FirebaseMessagingService() {

    // FirebaseMessagingService는 생성자 주입이 불가. 따라서 필드 주입 사용
    @Inject
    lateinit var alarmRepository: AlarmRepository
    @Inject
    lateinit var notificationPreference: NotificationPreference

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", token)

        notificationPreference.setFcmToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.registerFCMToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 푸시알람 활성화 안되어있으면 종료
        if (!notificationPreference.isMasterNotificationEnabled()) return

        val title = message.notification?.title ?: message.data["title"] ?: return
        val body = message.notification?.body ?: message.data["message"] ?: return

        Log.d("FCM", "메세지 수신 완료")
        Log.d("FCM", "title: $title / body: $body")

        // 일단은 액티비티로의 이동처리만 구현. 추후 수정 예정
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        getSystemService(NotificationManager::class.java)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "default_channel"
    }
}
