package com.example.linku_android.deeplink

import com.example.core.model.auth.SocialLoginData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SocialDeepLinkBus {
    private val _flow = MutableSharedFlow<SocialLoginData>(
        extraBufferCapacity = 1,
        replay = 1  //  늦게 구독해도 마지막 값 받을 수 있음
    )
    val flow: SharedFlow<SocialLoginData> = _flow.asSharedFlow()
    fun emit(data: SocialLoginData) { _flow.tryEmit(data) }
}