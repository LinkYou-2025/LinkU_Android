package com.linku.home.viewmodel

import com.linku.core.model.JobType

/**
 * 링크 상황 변경값이 최신 사용자 직업 조회 결과에 속하는지 검증합니다.
 *
 * 상황을 변경하지 않은 `null` 값은 다른 필드 수정을 막지 않도록 항상 허용합니다. 실제 상황
 * 변경값은 저장 클릭 시 캡처한 직업 조회가 여전히 최신이며 성공 상태이고, 선택한 상황이
 * 해당 직업의 후보에 포함될 때만 허용합니다.
 *
 * @param situationIdToUpdate 사용자가 명시적으로 변경해 PATCH에 포함하려는 상황 ID입니다.
 * @param expectedUserJobRequestId 저장 클릭 시 신뢰했던 사용자 직업 조회의 정확한 세대 ID입니다.
 * @param currentUserJobRequestId 현재 상태가 가리키는 최신 사용자 직업 조회의 세대 ID입니다.
 * @param isUserJobReady 최신 사용자 직업 조회가 성공했는지 여부입니다.
 * @param jobId 마지막 조회에서 확인한 로그인 사용자의 직업 ID입니다.
 * @return 상황 PATCH를 안전하게 전송할 수 있으면 `true`입니다.
 */
internal fun isLinkSituationUpdateAllowed(
    situationIdToUpdate: Long?,
    expectedUserJobRequestId: Long?,
    currentUserJobRequestId: Long,
    isUserJobReady: Boolean,
    jobId: Long?,
): Boolean {
    if (situationIdToUpdate == null) return true

    if (
        expectedUserJobRequestId == null ||
        currentUserJobRequestId != expectedUserJobRequestId ||
        !isUserJobReady
    ) {
        return false
    }

    val currentJob = JobType.entries.firstOrNull { jobType -> jobType.id == jobId }
        ?: return false

    return currentJob.situations.any { situation ->
        situation.id.value == situationIdToUpdate
    }
}

/**
 * 링크 수정 요청이 현재 화면이 조회 중인 상세 링크를 대상으로 하는지 확인합니다.
 *
 * @param expectedUserLinkuId 저장을 누른 상세 화면의 사용자 링크 ID입니다.
 * @param requestedLinkDetailId ViewModel이 현재 조회 대상으로 보관한 사용자 링크 ID입니다.
 * @param currentLinkDetailId ViewModel에 표시 데이터로 남아 있는 사용자 링크 ID입니다.
 * @return 세 ID가 모두 같은 유효한 링크 ID이면 `true`입니다.
 */
internal fun isCurrentLinkUpdateTarget(
    expectedUserLinkuId: Long,
    requestedLinkDetailId: Long?,
    currentLinkDetailId: Long?,
): Boolean =
    expectedUserLinkuId > 0L &&
        requestedLinkDetailId == expectedUserLinkuId &&
        currentLinkDetailId == expectedUserLinkuId
