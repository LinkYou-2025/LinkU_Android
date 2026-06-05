package com.linku

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

    @Inject
    lateinit var alarmRepository: AlarmRepository
    @Inject
    lateinit var notificationPreference: NotificationPreference


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        notificationPreference.setFcmToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.registerFCMToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: 푸시 알림 처리
    }

}