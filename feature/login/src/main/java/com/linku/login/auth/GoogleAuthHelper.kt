package com.linku.login.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// 참고 자료 : https://velog.io/@seongrak153/AndroidGoogle-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-with-Compose-CredentialManager

// findActivity() 헬퍼 추가.
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

class GoogleAuthHelper(
    private val webClientId: String
) {
    suspend fun getGoogleIdToken(activity: Activity): String {
        val credentialManager = CredentialManager.create(activity)

        // 구글 계정이 다수 등록된 기기(주로 Android 14+)에서 GetGoogleIdOption 사용 시
        // 내부 통신 버퍼(1MB) 초과로 UI 생성이 무음 실패하는 구글 SDK 이슈 회피를 위해
        // 버튼 전용 옵션인 GetSignInWithGoogleOption 사용.
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        check(
            credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "올바르지 않은 credential 타입" }

        return GoogleIdTokenCredential
            .createFrom(credential.data)
            .idToken
    }
}