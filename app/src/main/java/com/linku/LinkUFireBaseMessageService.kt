package com.linku

import android.app.NotificationManager
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linku.core.di.ApplicationScope
import com.linku.core.repository.AlarmRepository
import com.linku.data.preference.NotificationPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LinkUFireBaseMessageService : FirebaseMessagingService() {

    // FirebaseMessagingService는 생성자 주입이 불가. 따라서 필드 주입 사용
    @Inject
    lateinit var alarmRepository: AlarmRepository
    @Inject
    lateinit var notificationPreference: NotificationPreference

    @Inject
    @ApplicationScope
    lateinit var externalScope: CoroutineScope

    //FireBase에서 새 토큰이 발급되었을 때 호출되는 콜백
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", token)

        notificationPreference.setFcmToken(token)

        externalScope.launch {
            alarmRepository.registerFCMToken(token)
        }
    }

    // FireBase로부터 메세지를 받았을 때 호출되는 콜백
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 푸시알람 활성화 안되어있으면 종료
        if (!notificationPreference.isMasterNotificationEnabled()) return

        // FCM 메시지 타입에 따라 title/body 추출
        // Notification Message: message.notification에서 추출
        // Data Message: message.data에서 추출
        // 둘 다 없으면 처리 불필요로 판단하여 종료
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

        // 알람 제작
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
