<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/user/DeleteUserResponseDTO.kt
package com.linku.data.api.dto.user
========
package com.linku.data.api.dto.server
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/server/withDrawalResultDTO.kt

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class DeleteUserResponseDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "nickname")
    val nickname: String,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @Json(name = "status")
    val status: String,

    @Json(name = "inactiveDate")
    val inactiveDate: OffsetDateTime

)