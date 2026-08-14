package com.linku.data.mapper

import com.linku.core.model.FolderPermission
import com.linku.data.api.dto.folder.FolderTreeResponseDTO
import com.linku.data.api.dto.folder.LinkDTO
import com.linku.data.api.dto.folder.ShareFolderResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderMapperTest {

    @Test
    fun `folder permission maps to swagger request value`() {
        val dto = FolderPermission.WRITER.toRequestDto()

        assertEquals("writer", dto.permission)
    }

    @Test
    fun `permission response maps case insensitively`() {
        assertEquals(FolderPermission.VIEWER, "viewer".toFolderPermission())
        assertEquals(FolderPermission.OWNER, "OWNER".toFolderPermission())
        assertEquals(FolderPermission.NONE, "unknown".toFolderPermission())
    }

    @Test
    fun `shared folder response maps to domain`() {
        val dto = ShareFolderResponseDTO(
            folderId = 10L,
            userId = 20L,
            permission = "WRITER",
            sharedAt = "2026-07-14T10:00:00"
        )

        val domain = dto.toDomain()

        assertEquals(10L, domain.folderId)
        assertEquals(20L, domain.userId)
        assertEquals(FolderPermission.WRITER, domain.permission)
        assertEquals("2026-07-14T10:00:00", domain.sharedAt)
    }

    @Test
    fun `folder tree response maps children with parent id`() {
        val dto = FolderTreeResponseDTO(
            folderId = 1L,
            folderName = "parent",
            categoryId = 100L,
            isBookmarked = true,
            children = listOf(
                FolderTreeResponseDTO(
                    folderId = 2L,
                    folderName = "child",
                    categoryId = 100L,
                    isBookmarked = false
                )
            )
        )

        val domain = dto.toDomain()

        assertEquals(100L, domain.parentFolderId)
        assertEquals(1L, domain.children.single().parentFolderId)
        assertEquals("child", domain.children.single().folderName)
    }

    @Test
    fun `folder link maps user link id as saved link identity`() {
        val dto = LinkDTO(
            linkuId = 99L,
            userLinkuId = 123L,
            title = "saved link",
            url = "https://example.com",
            keyword = null,
            linkuImageUrl = null,
            createdAt = null,
        )

        val domain = dto.toDomain(parentFolderId = 10L)

        assertEquals(123L, domain.userLinkuId)
        assertEquals(10L, domain.parentFolderId)
    }
}
