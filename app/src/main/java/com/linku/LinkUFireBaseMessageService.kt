package com.linku

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class LinkUFireBaseMessageService : FirebaseMessagingService() {

    

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버에 FCM 토큰 전송
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: 푸시 알림 처리
    }

}