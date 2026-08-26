package com.linku.mypage.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linku.core.model.AiArticleLink
import com.linku.design.theme.ThemeProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** [AILinkuItem] 카드 본문의 상세 이동 클릭 활성화 계약을 검증합니다. */
@RunWith(AndroidJUnit4::class)
class AILinkuItemTest {

    /** 실제 Compose semantics와 포인터 입력을 검증하는 테스트 규칙입니다. */
    @get:Rule
    val composeTestRule = createComposeRule()

    /** 상호작용이 활성화된 카드는 클릭한 링크 ID를 정확히 한 번 전달합니다. */
    @Test
    fun enabledCardClickPassesUserLinkuIdExactlyOnce() {
        var clickedUserLinkuId: Long? = null
        var clickCount = 0

        composeTestRule.setContent {
            ThemeProvider {
                AILinkuItem(
                    link = testLink(),
                    onClick = { userLinkuId ->
                        clickedUserLinkuId = userLinkuId
                        clickCount += 1
                    },
                    isDeleteMenuVisible = false,
                    onMoreClick = {},
                    onDeleteClick = {},
                    modifier = Modifier.testTag(CARD_TEST_TAG),
                    isInteractionEnabled = true,
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CARD_TEST_TAG)
            .assertHasClickAction()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(TEST_USER_LINKU_ID, clickedUserLinkuId)
            assertEquals(1, clickCount)
        }
    }

    /** 상호작용이 비활성화된 카드는 클릭 semantics와 상세 이동 콜백을 제공하지 않습니다. */
    @Test
    fun disabledCardHasNoClickActionAndDoesNotInvokeCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            ThemeProvider {
                AILinkuItem(
                    link = testLink(),
                    onClick = { clickCount += 1 },
                    isDeleteMenuVisible = false,
                    onMoreClick = {},
                    onDeleteClick = {},
                    modifier = Modifier.testTag(CARD_TEST_TAG),
                    isInteractionEnabled = false,
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CARD_TEST_TAG)
            .assertHasNoClickAction()
            .performTouchInput { click() }

        composeTestRule.runOnIdle {
            assertEquals(0, clickCount)
        }
    }

    /** 카드 클릭 테스트에 사용할 유효한 사용자 저장 링크를 생성합니다. */
    private fun testLink(): AiArticleLink =
        AiArticleLink(
            userLinkuId = TEST_USER_LINKU_ID,
            linku = "https://example.com/article",
            emotionId = 1L,
            domain = "example.com",
            domainImageUrl = null,
            title = "AI 요약 링크",
            linkuImageUrl = null,
            categoryId = 4L,
            categoryName = "IT·개발",
        )

    private companion object {
        /** 카드 루트 semantics를 안정적으로 찾기 위한 테스트 태그입니다. */
        const val CARD_TEST_TAG = "ai_linku_card"

        /** 상세 이동 콜백으로 전달되어야 하는 유효한 사용자 저장 링크 ID입니다. */
        const val TEST_USER_LINKU_ID = 42L
    }
}
