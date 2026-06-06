package com.linku

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "✅ MainApplication 실행됨")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        createNotificationChannels()

    }

    // 알림 채널 생성. minSdk가 26이므로 분기처리 생략
    private fun createNotificationChannels() {
        val channel = NotificationChannel(
            "default_channel",
                "기본 알림",
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

    }
}