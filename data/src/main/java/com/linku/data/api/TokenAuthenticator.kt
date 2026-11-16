package com.linku.data.api

import retrofit2.HttpException
import android.util.Log
import com.linku.data.api.dto.auth.refreshToken.ReissueRequestDTO
import com.linku.data.api.dto.auth.refreshToken.RefreshTokenResponseDTO
import com.linku.core.preference.AuthPreference
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TokenAuthenticator @Inject constructor(
    private val authPreference: AuthPreference,
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
                    val currentToken = authPreference.getAccessToken().orEmpty()

                    if (currentToken.isNotBlank() &&
                        response.request.header("Authorization") != "Bearer $currentToken"
                    ) {
                        Log.d(TAG, "[토큰 이미 갱신됨] 새 토큰으로 재시도")
                        return@withLock response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    val refreshToken = authPreference.getRefreshToken()
                    if (refreshToken.isNullOrBlank()) {
                        Log.e(TAG, "[토큰 재발급 실패] refreshToken 없음")
                        return@withLock null
                    }

                    val deviceId = authPreference.getDeviceId()
                    if (deviceId.isBlank()) {
                        Log.e(TAG, "[토큰 재발급 실패] deviceId 없음")
                        return@withLock null
                    }

                    val newTokens = when(val result = reissueOnce(refreshToken, deviceId)) {
                        is ReissueResult.Success -> result.tokens
                        is ReissueResult.Rejected -> {
                            Log.e(TAG, "[토큰 재발급 실패] 서버가 재발급을 거절했습니다. 세션을 정리합니다.")
                            authPreference.clear()
                            return@withLock null
                        }
                        is ReissueResult.Unavailable -> {
                            Log.e(TAG, "[토큰 재발급 실패] 서버가 일시적으로 응답하지 않습니다.")
                            return@withLock null
                        }
                    }

                    val newAccessToken = newTokens.accessToken
                    val newRefreshToken = newTokens.refreshToken


                    authPreference.updateAccessToken(
                        accessToken = newAccessToken,
                        refreshToken = newRefreshToken
                    )

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

    private sealed interface ReissueResult {
        data class Success(val tokens: RefreshTokenResponseDTO) : ReissueResult
        data object Rejected : ReissueResult      // 서버가 거절 → 로그아웃
        data object Unavailable : ReissueResult   // 일시적 실패 → 토큰 유지
    }

    /** 재발급을 한 번 시도하고, 결과를 위 세 가지 중 하나로 분류합니다. */
    private suspend fun reissueOnce(refreshToken: String, deviceId: String): ReissueResult {
        return try {
            val response = authApi.reissue(ReissueRequestDTO(refreshToken, deviceId))
            if (response.isSuccess) {
                ReissueResult.Success(response.result)
            } else {
                ReissueResult.Rejected
            }

        } catch (e: IOException) {
            ReissueResult.Unavailable // 오프라인, 타임아웃은 토큰의 문제가 아님.
        } catch (e: HttpException) {
            if (e.code() in 500..599) {
                ReissueResult.Unavailable
            } else {
                ReissueResult.Rejected
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