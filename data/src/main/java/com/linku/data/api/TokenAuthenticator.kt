package com.linku.data.api

import android.util.Log
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.data.api.dto.auth.refreshToken.ReissueRequestDTO
import com.linku.data.preference.AuthPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TokenAuthenticator @Inject constructor(
    private val authPreference: AuthPreference,
    private val loginSessionStore: LoginSessionStore,
    private val authApi: AuthApi
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private val refreshMutex = Mutex()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Log.e(TAG, "[토큰 재발급 실패] 재시도 횟수 초과")
            return null
        }

        Log.d(TAG, "[401 감지] 토큰 재발급 시도")

        return runBlocking {
            refreshMutex.withLock {
                try {
                    val currentToken = authPreference.accessToken

                    if (currentToken.isNotBlank() &&
                        response.request.header("Authorization") != "Bearer $currentToken"
                    ) {
                        Log.d(TAG, "[토큰 이미 갱신됨] 새 토큰으로 재시도")
                        return@withLock response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    val refreshToken = authPreference.refreshToken
                        ?: run {
                            Log.e(TAG, "[토큰 재발급 실패] refreshToken 없음")
                            return@withLock null
                        }

                    val deviceId = loginSessionStore.deviceId.first()
                        ?: run {
                            Log.e(TAG, "[토큰 재발급 실패] deviceId 없음")
                            return@withLock null
                        }

                    val reissueResponse = authApi.reissue(
                        ReissueRequestDTO(
                            refreshToken = refreshToken,
                            deviceId = deviceId
                        )
                    )

                    if (!reissueResponse.isSuccess) {
                        Log.e(TAG, "[토큰 재발급 실패] 서버 응답 실패")
                        authPreference.clear()
                        return@withLock null
                    }

                    val newAccessToken = reissueResponse.result.accessToken
                    val newRefreshToken = reissueResponse.result.refreshToken


                    // 새 토큰 저장
                    authPreference.accessToken = newAccessToken
                    authPreference.refreshToken = newRefreshToken

                    Log.d(TAG, "[토큰 재발급 성공] 원래 요청 재시도")


                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()

                } catch (e: Exception) {
                    Log.e(TAG, "[토큰 재발급 실패] ${e.message}")
                    null
                }
            }
        }
    }

    /** 재시도 횟수 계산 */
    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}