package com.linku.mypage

import com.linku.mypage.component.aiLinkuClickAction
import com.linku.mypage.screen.aiLinkuItemKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** AI 링크 목록의 사용자 저장 링크 ID 검증과 Paging 슬롯 대체 키를 확인합니다. */
class AILinkuNavigationIdentityTest {

    @Test
    fun `positive user linku id creates stable key and navigation action`() {
        var navigatedId: Long? = null

        val action = aiLinkuClickAction(userLinkuId = 42L) { navigatedId = it }
        action?.invoke()

        assertEquals("ai-linku-user-42", aiLinkuItemKey(index = 3, userLinkuId = 42L))
        assertEquals(42L, navigatedId)
    }

    @Test
    fun `unloaded paging slot uses index key`() {
        assertEquals("ai-linku-index-3", aiLinkuItemKey(index = 3, userLinkuId = null))
    }

    @Test
    fun `non-positive user linku id uses index key and disables navigation`() {
        val action = aiLinkuClickAction(userLinkuId = 0L) {
            error("Invalid userLinkuId must not navigate.")
        }

        assertEquals("ai-linku-index-3", aiLinkuItemKey(index = 3, userLinkuId = 0L))
        assertNull(action)
    }
}
