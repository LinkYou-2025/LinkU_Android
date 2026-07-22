package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.folder.InvitationInfoResponseDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationApi {
    @GET("invitations/{token}")
    suspend fun getInvitationInfo(
        @Path("token") token: String
    ): BaseResponse<InvitationInfoResponseDTO>

    @POST("invitations/{token}")
    suspend fun acceptInvitation(
        @Path("token") token: String
    ): BaseResponse<Long>
}
