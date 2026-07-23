package com.linku.core.repository

import com.linku.core.model.InvitationInfo

interface InvitationRepository {
    suspend fun getInvitationInfo(token: String): InvitationInfo

    suspend fun acceptInvitation(token: String): Long
}
