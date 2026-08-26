package com.linku.home.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** 링크 수정 직전 상황 ID와 최신 사용자 직업 조회의 일치 계약을 검증합니다. */
class LinkSituationUpdateValidationTest {

    /** 상황을 변경하지 않으면 직업 조회 상태와 무관하게 다른 필드를 수정할 수 있는지 검증합니다. */
    @Test
    fun `unchanged situation does not require a ready job request`() {
        assertTrue(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = null,
                expectedUserJobRequestId = null,
                currentUserJobRequestId = 0L,
                isUserJobReady = false,
                jobId = null,
            )
        )
    }

    /** 상세 진입 이후 성공한 최신 조회의 현재 직업 상황만 허용하는지 검증합니다. */
    @Test
    fun `changed situation must belong to a successful current job request`() {
        assertTrue(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = 5L,
                currentUserJobRequestId = 5L,
                isUserJobReady = true,
                jobId = 2L,
            )
        )
        assertFalse(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = 5L,
                currentUserJobRequestId = 6L,
                isUserJobReady = true,
                jobId = 2L,
            )
        )
        assertFalse(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = 5L,
                currentUserJobRequestId = 4L,
                isUserJobReady = true,
                jobId = 2L,
            )
        )
        assertFalse(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = 5L,
                currentUserJobRequestId = 5L,
                isUserJobReady = false,
                jobId = 2L,
            )
        )
        assertFalse(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = 5L,
                currentUserJobRequestId = 5L,
                isUserJobReady = true,
                jobId = 3L,
            )
        )
        assertFalse(
            isLinkSituationUpdateAllowed(
                situationIdToUpdate = 10L,
                expectedUserJobRequestId = null,
                currentUserJobRequestId = 5L,
                isUserJobReady = true,
                jobId = 2L,
            )
        )
    }

    /** 저장을 누른 링크와 ViewModel의 조회·표시 대상이 모두 같을 때만 허용하는지 검증합니다. */
    @Test
    fun `link update target requires matching expected requested and displayed ids`() {
        assertTrue(
            isCurrentLinkUpdateTarget(
                expectedUserLinkuId = 10L,
                requestedLinkDetailId = 10L,
                currentLinkDetailId = 10L,
            )
        )
        assertFalse(
            isCurrentLinkUpdateTarget(
                expectedUserLinkuId = 10L,
                requestedLinkDetailId = 11L,
                currentLinkDetailId = 11L,
            )
        )
        assertFalse(
            isCurrentLinkUpdateTarget(
                expectedUserLinkuId = 10L,
                requestedLinkDetailId = 10L,
                currentLinkDetailId = 11L,
            )
        )
        assertFalse(
            isCurrentLinkUpdateTarget(
                expectedUserLinkuId = 0L,
                requestedLinkDetailId = 0L,
                currentLinkDetailId = 0L,
            )
        )
    }
}
