<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/user/UpdateUserProfileRequestDTO.kt
package com.linku.data.api.dto.user
========
package com.linku.data.api.dto.server
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/server/UpdateProfileDTO.kt

import com.squareup.moshi.Json

data class UpdateUserProfileRequestDTO(

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "jobId")
    val jobId: Long,

    @Json(name = "purposes")
    val purposes: List<String>,

    @Json(name = "interests")
    val interests: List<String>

)