package com.linku

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "✅ MainApplication 실행됨")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

    }
}