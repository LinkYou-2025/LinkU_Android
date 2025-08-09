package com.example.data.api.dto
import com.example.data.api.dto.server.RefreshTokenRequest
import com.example.data.api.dto.server.TokenPair
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    // TODO: 실제 서버 경로 확인 (예: "auth/refresh")
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body req: RefreshTokenRequest
    ): BaseResponse<TokenPair>
}