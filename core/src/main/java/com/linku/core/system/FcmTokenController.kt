package com.linku.core.system

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FcmTokenController {

    suspend fun getToken(): String {
        val token = FirebaseMessaging.getInstance().token.await()

        Log.d(
            "FCM",
            "FCM Token issued: ${token.take(20)}..."
        )

        return token
    }
}
