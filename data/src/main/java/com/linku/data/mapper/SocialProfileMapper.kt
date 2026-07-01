package com.linku.data.mapper

import com.linku.core.model.auth.*
import com.linku.data.api.dto.auth.signup.social.SocialProfileRequestDTO

object SocialProfileMapper {

    fun toRequest(
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>
    ) = SocialProfileRequestDTO(
            nickName = nickName,
            gender = gender.name,
            jobId = job.id,
            purposeList = purposes.map { it.serverKey },
            interestList = interests.map { it.serverKey }
        )

}