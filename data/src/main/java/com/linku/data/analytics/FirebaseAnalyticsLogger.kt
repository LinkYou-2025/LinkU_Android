package com.linku.data.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.linku.core.analytics.AnalyticsEvent
import com.linku.core.analytics.AnalyticsEventMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name) {
            AnalyticsEventMapper.toParams(event).forEach {
                (key, value) -> param(key, value)
            }
        }
    }
}
