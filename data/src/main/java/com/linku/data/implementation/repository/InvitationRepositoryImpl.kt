package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.InvitationInfo
import com.linku.core.repository.InvitationRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.toDomain
import javax.inject.Inject

class InvitationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi
) : InvitationRepository {

    override suspend fun getInvitationInfo(token: String): InvitationInfo {
        Log.d("InvitationRepositoryImpl", "getInvitationInfo token: $token")

        return safeApiCall {
            serverApi.getInvitationInfo(token)
        }.fold(
            onSuccess = { it.toDomain() },
            onFailure = { throw it }
        )
    }

    override suspend fun acceptInvitation(token: String): Long {
        Log.d("InvitationRepositoryImpl", "acceptInvitation token: $token")

        return safeApiCall {
            serverApi.acceptInvitation(token)
        }.fold(
            onSuccess = { it },
            onFailure = { throw it }
        )
    }
}
