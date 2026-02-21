package com.example.linku_android.deeplink

import android.content.Intent
import android.util.Log

fun extractSocialDeepLinkData(intent: Intent): SocialLoginData? {
    return intent.data?.let { uri ->
        Log.d("SOCIAL_LOGIN", "URI 전체: $uri")
        Log.d("SOCIAL_LOGIN", "host: ${uri.host}, path: ${uri.path}")

        if (uri.host == "linkuserver.store" && uri.path == "/auth") {
            val provider = uri.getQueryParameter("path")
            val token    = uri.getQueryParameter("token")

            if (token != null && provider != null) {
                Log.d("SOCIAL_LOGIN", "SocialLoginData 생성 성공!")
                SocialLoginData(provider = provider, token = token)
            } else {
                Log.e("SOCIAL_LOGIN", "token 또는 provider가 null")
                null
            }
        } else {
            Log.d("SOCIAL_LOGIN", "소셜 로그인 딥링크 아님")
            null
        }
    }
}

/**
 * 서원이 수정되면 다음과 같이 수정함.
 * fun extractSocialDeepLinkData(intent: Intent): SocialLoginData? {
 *     return intent.data?.let { uri ->
 *         if (uri.host == "linkuserver.store" && uri.path == "/auth") {
 *             val provider  = uri.getQueryParameter("provider") ?: return@let null
 *             val result    = uri.getQueryParameter("result")   ?: return@let null
 *
 *             Log.d("SOCIAL_LOGIN", "provider: $provider, result: $result")
 *
 *             SocialLoginData(
 *                 provider     = provider,
 *                 result       = result,
 *                 status       = uri.getQueryParameter("status"),
 *                 accessToken  = uri.getQueryParameter("accessToken"),
 *                 refreshToken = uri.getQueryParameter("refreshToken"), // 신규
 *                 userId       = uri.getQueryParameter("userId")?.toLongOrNull(), // 신규
 *                 socialToken  = uri.getQueryParameter("socialToken"),
 *                 errorCode    = uri.getQueryParameter("errorCode")
 *             )
 *         } else null
 *     }
 * }
 *
 * */