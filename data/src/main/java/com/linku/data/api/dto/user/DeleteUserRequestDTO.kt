<<<<<<<< HEAD:data/src/main/java/com/linku/data/api/dto/user/DeleteUserRequestDTO.kt
package com.linku.data.api.dto.user
========
package com.linku.data.api.dto.server
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:data/src/main/java/com/linku/data/api/dto/server/DeleteReasonDTO.kt

import com.squareup.moshi.Json

data class DeleteUserRequestDTO(

    @Json(name = "reason")
    val reason: String

)