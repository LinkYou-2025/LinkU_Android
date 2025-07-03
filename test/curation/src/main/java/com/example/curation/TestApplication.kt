package com.example.curation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}