package com.linku

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "✅ MainApplication 실행됨")
    }
}