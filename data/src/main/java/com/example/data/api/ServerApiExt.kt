package com.example.data.api


import com.example.core.error.TokenExpiredException
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.RefreshTokenRequest
import com.example.data.preference.AuthPreference
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

suspend fun <T> ServerApi.withCheck(
    getter: suspend ServerApi.() -> BaseResponse<T>
): T {
    val response = getter()
    if (!response.isSuccess) throw Exception(response.message)
    return response.result
}

// refreshToken 갱신 有 (로그인 로직 변경 없음)
suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> BaseResponse<T>,
): T {
    try {
        return withCheck { routine() }
    } catch (_: Exception) {
        val refresh = authPreference.refreshToken
            ?: throw TokenExpiredException("Access token expired. Please log in again.")
        val pair = withCheck { refreshToken(RefreshTokenRequest(refresh)) }
        pair.refreshToken?.let { authPreference.refreshToken = it }
        pair.accessToken?.let  { authPreference.accessToken  = it }
        return withCheck { routine() }
    }
}

/**
 * 204 No Content 를 정상 케이스(null)로 처리하는 withAuth 변형
 * routine 은 retrofit2.Response<BaseResponse<T>> 를 반환해야 함
 */
// 204를 null로 처리하되, 응답 바디가 "그 자체 DTO"인 경우용
suspend fun <T> ServerApi.withAuthResp204Raw(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> Response<T>
): T? {
    try {
        val r1 = routine()
        if (r1.code() == 204) return null
        val b1 = r1.body() ?: throw Exception("Empty body")
        return b1
    } catch (_: Exception) {
        val refresh = authPreference.refreshToken
            ?: throw TokenExpiredException("Access token expired. Please log in again.")
        val pair = withCheck { refreshToken(RefreshTokenRequest(refresh)) }
        pair.refreshToken?.let { authPreference.refreshToken = it }
        pair.accessToken?.let  { authPreference.accessToken  = it }

        val r2 = routine()
        if (r2.code() == 204) return null
        val b2 = r2.body() ?: throw Exception("Empty body")
        return b2
    }
}

/**
 * Authorization 헤더 문자열("Bearer xxx")을 routine 에 넘기고
 * 임의 타입 T 를 그대로 반환. 401/만료 시 refresh 후 1회 재시도.
 */
suspend fun <T> ServerApi.withAuthHeaderRaw(
    authPreference: AuthPreference,
    routine: suspend ServerApi.(String) -> T
): T {
    try {
        val access = authPreference.accessToken
            ?: throw TokenExpiredException("Access token missing. Please log in.")
        return routine("Bearer $access")
    } catch (_: Exception) {
        val refresh = authPreference.refreshToken
            ?: throw TokenExpiredException("Access token expired. Please log in again.")
        val pair = withCheck { refreshToken(RefreshTokenRequest(refresh)) }
        pair.refreshToken?.let { authPreference.refreshToken = it }
        pair.accessToken?.let  { authPreference.accessToken  = it }

        val access2 = authPreference.accessToken
            ?: throw TokenExpiredException("Failed to refresh token.")
        return routine("Bearer $access2")
    }
}

// --- Call 전용: per-call timeout + 401시 refresh 후 1회 재시도 (Raw T 반환) ---
suspend fun <T> ServerApi.withAuthCallRaw(
    authPreference: AuthPreference,
    timeoutSec: Long = 60,
    build: ServerApi.() -> Call<T>
): T = withContext(Dispatchers.IO) {
    fun Call<T>.execWithTimeout(): Response<T> {
        timeout().timeout(timeoutSec, TimeUnit.SECONDS) // ← 이 호출만 타임아웃
        return execute()
    }

    var resp = build().execWithTimeout()

    // 401이면 refresh 후 1회 재시도
    if (resp.code() == 401) {
        val refresh = authPreference.refreshToken
            ?: throw TokenExpiredException("Access token expired. Please log in again.")
        val pair = withCheck { refreshToken(RefreshTokenRequest(refresh)) }
        pair.refreshToken?.let { authPreference.refreshToken = it }
        pair.accessToken?.let  { authPreference.accessToken  = it }

        resp = build().execWithTimeout() // Call은 재사용 불가 → 새로 build()
    }

    if (!resp.isSuccessful) throw HttpException(resp)
    resp.body() ?: throw IOException("Empty body")
}

// --- BaseResponse<R>를 받아 isSuccess 확인 후 R만 반환 ---
suspend fun <R> ServerApi.withAuthCallChecked(
    authPreference: AuthPreference,
    timeoutSec: Long = 60,
    build: ServerApi.() -> Call<BaseResponse<R>>
): R = withContext(Dispatchers.IO) {
    val base = withAuthCallRaw(authPreference, timeoutSec, build)
    if (!base.isSuccess) throw Exception(base.message)
    base.result ?: throw IOException("Empty result")
}