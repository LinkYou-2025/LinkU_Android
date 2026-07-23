package com.linku

import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.BitmapFactory
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
        if (BuildConfig.DEBUG) {
            Log.d("FCM Token", token)
        }

        externalScope.launch {
            notificationPreference.setFcmToken(token)
            alarmRepository.registerFCMToken(token)
        }
    }

    // FireBase로부터 메세지를 받았을 때 호출되는 콜백
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        externalScope.launch {

            // 푸시알림 활성화 안되어있으면 종료
            if (!notificationPreference.isMasterNotificationEnabled()) {
                Log.e("FCM", "master notification off")
                return@launch
            }

            val title = message.notification?.title
            val body = message.notification?.body

            val type = message.data["type"]
            val targetId = message.data["targetId"]
            val alarmId = message.data["alarmId"]

            // type, targetId 없으면 얼리 리턴
            if (type == null || targetId == null) return@launch

            if (BuildConfig.DEBUG) {
                Log.d("FCM", """
                FCM 수신
                title: $title
                body: $body
                targetId: $targetId
                """.trimIndent()
                )
            }

            // 포그라운드에서 알림을 탭했을 때 앱의 진입 액티비티를 실행하며 알림 데이터를 전달
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                ?.apply {
                    putExtra("type", type)
                    putExtra("targetId", targetId)
                    alarmId?.let { putExtra("alarmId", it) }  // alarmId는 있을 때만
                }

            val pendingIntent = PendingIntent.getActivity(
                this@LinkUFireBaseMessageService,
                0,
                launchIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ) ?: return@launch

            // 알림 제작
            val notification = NotificationCompat.Builder(this@LinkUFireBaseMessageService, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_noti_ex_2) // 상태바 표시 아이콘
                .setLargeIcon( // 알림 확장 영역 표시용 앱 로고
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_logo
                    )
                )
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            // 알림 출력
            getSystemService(NotificationManager::class.java)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    companion object {
        const val CHANNEL_ID = "default_channel"
    }
}
