package com.example.data.api


import com.example.core.error.TokenExpiredException
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.RefreshTokenRequest
import com.example.data.preference.AuthPreference
import retrofit2.Response

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