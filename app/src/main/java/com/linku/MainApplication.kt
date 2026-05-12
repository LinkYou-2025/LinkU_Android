package com.linku

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.linku.data.network.NetworkReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {

    @Inject
    lateinit var networkReceiver: NetworkReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "✅ MainApplication 실행됨")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        networkReceiver.register() // 네트워크 감지 시작

    }

    override fun onTerminate() {
        super.onTerminate()
        networkReceiver.unregister() // 앱 종료 시 해제
    }
}