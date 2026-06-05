package com.linku.data.api.alarm

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.alarm.AlarmSettingDTO
import com.linku.data.api.dto.server.alarm.AlarmSettingRequest
import com.linku.data.api.dto.server.alarm.AlarmsDTO
import com.linku.data.api.dto.server.alarm.FcmTokenRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmApi {

    @GET("alarm/list")
    suspend fun getAlarms(
        @Query("alarmType") alarmType: String = "ALL",
        @Query("cursor") cursor: Long? = null,
        @Query("size") size: Int
    ): BaseResponse<AlarmsDTO>


    @PATCH("alarm/settings")
    suspend fun updateAlarmSetting(
        @Body body: AlarmSettingRequest
    ): BaseResponse<Boolean>

    @GET("alarm/settings")
    suspend fun getAlarmSetting(): BaseResponse<AlarmSettingDTO>

    @POST("alarm/fcmtoken")
    suspend fun registerFcmToken(
        @Body body: FcmTokenRequest
    ): BaseResponse<Any?>

}