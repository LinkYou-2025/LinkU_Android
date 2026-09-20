package com.linku.data.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.linku.core.analytics.AnalyticsEvent
import com.linku.core.analytics.AnalyticsLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    @ApplicationContext private val context: Context,
) : AnalyticsLogger {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    override fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name) {
            event.params.forEach { (key, value) -> param(key, value) }
        }
    }
}
