package com.example.linku_android

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "✅ MainApplication 실행됨")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)


        // 키 해시 확인용 - 등록 후 삭제할 것
        // TODO : 지우기
        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "키 해시: $keyHash")
    }
}