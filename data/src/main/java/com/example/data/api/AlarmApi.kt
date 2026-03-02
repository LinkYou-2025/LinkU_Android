package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.AlarmFcmTokenDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AlarmApi {
    // FCM 토큰 등록
    @POST("alarm/fcmtoken")
    suspend fun registerFcmToken(
        @Body body: AlarmFcmTokenDTO
    ): BaseResponse<String>

}