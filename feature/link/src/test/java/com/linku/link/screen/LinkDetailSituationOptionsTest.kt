package com.linku.link.screen

import com.linku.core.model.SituationId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 링크 상세 화면의 직업별 상황 후보와 기존 상황 표시 계약을 검증합니다. */
class LinkDetailSituationOptionsTest {

    /** 서버 직업 ID 1~6이 각각 정해진 여덟 개 상황 ID에만 매핑되는지 검증합니다. */
    @Test
    fun `supported jobs receive only their contracted situation ids`() {
        val expectedSituationIdsByJob = mapOf(
            1L to (1L..8L).toList(),
            2L to (9L..16L).toList(),
            3L to (17L..24L).toList(),
            4L to (25L..32L).toList(),
            5L to (33L..40L).toList(),
            6L to (41L..48L).toList(),
        )

        expectedSituationIdsByJob.forEach { (jobId, expectedSituationIds) ->
            val actualSituationIds = linkDetailSituationOptions(jobId).map { situation ->
                situation.id.value
            }

            assertEquals(expectedSituationIds, actualSituationIds)
            assertEquals(8, actualSituationIds.size)
        }
    }

    /** 직업을 확인하지 못했거나 지원하지 않는 ID에는 임의 상황을 제공하지 않는지 검증합니다. */
    @Test
    fun `unknown job receives no situations`() {
        listOf<Long?>(null, 0L, 7L, Long.MAX_VALUE).forEach { jobId ->
            assertTrue(linkDetailSituationOptions(jobId).isEmpty())
        }
    }

    /** 과거 직업 상황은 표시하되 현재 직업의 신규 선택 후보에서는 제외하는지 검증합니다. */
    @Test
    fun `legacy situation remains visible but is not selectable for current job`() {
        val legacySituationId = 19L
        val currentOptions = linkDetailSituationOptions(2L)

        assertEquals(
            SituationId.OFFICE_WORKING,
            linkDetailSituation(legacySituationId)?.id,
        )
        assertFalse(
            currentOptions.any { situation -> situation.id.value == legacySituationId }
        )
        assertNull(linkDetailSituation(null))
        assertNull(linkDetailSituation(Long.MAX_VALUE))
    }

    /** 상세 진입 이후 성공한 최신 조회만 직업 ID로 채택하는지 검증합니다. */
    @Test
    fun `detail job requires a successful request at or after the entry floor`() {
        assertNull(
            verifiedLinkDetailJobId(
                jobId = 2L,
                minimumUserJobRequestId = null,
                currentUserJobRequestId = 10L,
                isUserJobReady = true,
            )
        )
        assertNull(
            verifiedLinkDetailJobId(
                jobId = 2L,
                minimumUserJobRequestId = 10L,
                currentUserJobRequestId = 9L,
                isUserJobReady = true,
            )
        )
        assertNull(
            verifiedLinkDetailJobId(
                jobId = 2L,
                minimumUserJobRequestId = 10L,
                currentUserJobRequestId = 10L,
                isUserJobReady = false,
            )
        )
        assertEquals(
            2L,
            verifiedLinkDetailJobId(
                jobId = 2L,
                minimumUserJobRequestId = 10L,
                currentUserJobRequestId = 11L,
                isUserJobReady = true,
            )
        )
        assertNull(
            verifiedLinkDetailJobId(
                jobId = 7L,
                minimumUserJobRequestId = 10L,
                currentUserJobRequestId = 10L,
                isUserJobReady = true,
            )
        )
    }

    /** 기존 상황은 유지하고 현재 직업에서 새로 고른 상황만 변경값으로 추출하는지 검증합니다. */
    @Test
    fun `only an explicitly changed valid situation becomes an update value`() {
        val universityOptions = linkDetailSituationOptions(2L)

        assertTrue(
            isLinkDetailSituationChangeValid(
                selectedSituationId = 19L,
                baselineSituationId = 19L,
                isSituationSelectionReady = false,
                situationOptions = emptyList(),
            )
        )
        assertNull(
            linkDetailSituationIdToUpdate(
                selectedSituationId = 19L,
                baselineSituationId = 19L,
            )
        )
        assertTrue(
            isLinkDetailSituationChangeValid(
                selectedSituationId = 10L,
                baselineSituationId = 19L,
                isSituationSelectionReady = true,
                situationOptions = universityOptions,
            )
        )
        assertEquals(
            10L,
            linkDetailSituationIdToUpdate(
                selectedSituationId = 10L,
                baselineSituationId = 19L,
            )
        )
        assertFalse(
            isLinkDetailSituationChangeValid(
                selectedSituationId = 19L,
                baselineSituationId = 10L,
                isSituationSelectionReady = true,
                situationOptions = universityOptions,
            )
        )
        assertFalse(
            isLinkDetailSituationChangeValid(
                selectedSituationId = 10L,
                baselineSituationId = 19L,
                isSituationSelectionReady = false,
                situationOptions = universityOptions,
            )
        )
    }

    /** 직업 재조회 중에는 초안을 보존하고 성공 후 불일치가 확인될 때만 복원하는지 검증합니다. */
    @Test
    fun `situation draft resets only after a successful mismatched job lookup`() {
        val universityOptions = linkDetailSituationOptions(2L)

        assertFalse(
            shouldResetLinkDetailSituationDraft(
                isEditMode = true,
                isEditBaselineCaptured = true,
                selectedSituationId = 10L,
                baselineSituationId = 19L,
                isSituationSelectionReady = false,
                situationOptions = emptyList(),
            )
        )
        assertFalse(
            shouldResetLinkDetailSituationDraft(
                isEditMode = false,
                isEditBaselineCaptured = true,
                selectedSituationId = 19L,
                baselineSituationId = 20L,
                isSituationSelectionReady = true,
                situationOptions = universityOptions,
            )
        )
        assertFalse(
            shouldResetLinkDetailSituationDraft(
                isEditMode = true,
                isEditBaselineCaptured = true,
                selectedSituationId = 10L,
                baselineSituationId = 19L,
                isSituationSelectionReady = true,
                situationOptions = universityOptions,
            )
        )
        assertTrue(
            shouldResetLinkDetailSituationDraft(
                isEditMode = true,
                isEditBaselineCaptured = true,
                selectedSituationId = 19L,
                baselineSituationId = 10L,
                isSituationSelectionReady = true,
                situationOptions = universityOptions,
            )
        )
    }
}
