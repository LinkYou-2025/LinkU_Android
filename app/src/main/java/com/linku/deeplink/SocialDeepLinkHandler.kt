package com.linku.deeplink

import android.content.Intent
import android.util.Log
import com.linku.core.model.auth.SocialLoginData

fun extractSocialDeepLinkData(intent: Intent): SocialLoginData? {
    val uri = intent.data ?: return null

    Log.d("SOCIAL_LOGIN", "URI 전체: $uri")
    Log.d("SOCIAL_LOGIN", "scheme: ${uri.scheme}")
    Log.d("SOCIAL_LOGIN", "host: ${uri.host}")
    Log.d("SOCIAL_LOGIN", "path: ${uri.path}")


    val provider = uri.getQueryParameter("provider") ?: return null
    val result   = uri.getQueryParameter("result") ?: return null

    return SocialLoginData(
        provider = provider,
        result = result,
        status = uri.getQueryParameter("status"),
        accessToken = uri.getQueryParameter("accessToken"),
        refreshToken = uri.getQueryParameter("refreshToken"),
        socialToken = uri.getQueryParameter("socialToken"),
        errorCode = uri.getQueryParameter("errorCode")
    )
}