package com.linku.data.mapper

import com.linku.core.model.InvitationInfo
import com.linku.data.api.dto.folder.InvitationInfoResponseDTO

fun InvitationInfoResponseDTO.toDomain(): InvitationInfo =
    InvitationInfo(
        folderName = folderName,
        ownerName = ownerName
    )
