package com.linku.data.mapper

import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.Purpose
import com.linku.data.api.dto.auth.signup.social.SocialProfileRequestDTO

object SocialProfileMapper {

    fun toRequest(
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>,
        termsMap: Map<String, Boolean>
    ) = SocialProfileRequestDTO(
            nickName = nickName,
            gender = gender.name,
            jobId = job.id,
            purposeList = purposes.map { it.serverKey },
        interestList = interests.map { it.serverKey },
        termsMap = termsMap
        )

}