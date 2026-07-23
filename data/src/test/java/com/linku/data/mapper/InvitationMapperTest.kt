package com.linku.data.mapper

import com.linku.data.api.dto.folder.InvitationInfoResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class InvitationMapperTest {

    @Test
    fun `invitation info response maps to domain`() {
        val dto = InvitationInfoResponseDTO(
            folderName = "project",
            ownerName = "owner"
        )

        val domain = dto.toDomain()

        assertEquals("project", domain.folderName)
        assertEquals("owner", domain.ownerName)
    }
}
