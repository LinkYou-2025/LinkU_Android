package com.linku.home.model

import com.linku.core.model.LinkSimpleInfo

/** 최근 조회 링크 요청의 현재 단계를 나타냅니다. */
enum class RecentLinksLoadStatus {
    /** 최근 조회 링크를 불러오는 중인 상태입니다. */
    Loading,

    /** 최근 조회 링크 요청이 정상적으로 완료된 상태입니다. */
    Success,

    /** 최근 조회 링크 요청이 실패한 상태입니다. */
    Error,
}

/**
 * 홈 화면의 최근 조회 링크와 요청 상태를 하나의 원자적인 값으로 관리합니다.
 *
 * 재조회 중이거나 재조회가 실패하더라도 [links]에 기존 데이터를 유지합니다. 따라서 최초 요청에서만
 * 스켈레톤 또는 오류 UI를 표시하고, 이미 노출 중인 카드는 화면에서 제거하지 않을 수 있습니다.
 *
 * @property links 마지막으로 정상 수신한 최근 조회 링크 목록
 * @property loadStatus 현재 최근 조회 링크 요청 단계
 */
data class RecentLinksUiState(
    val links: List<LinkSimpleInfo> = emptyList(),
    val loadStatus: RecentLinksLoadStatus = RecentLinksLoadStatus.Loading,
) {
    /** 표시할 기존 링크 없이 최초 데이터를 불러오는 중인지 여부입니다. */
    val isInitialLoading: Boolean
        get() = links.isEmpty() && loadStatus == RecentLinksLoadStatus.Loading

    /** 표시할 기존 링크 없이 최초 요청이 실패했는지 여부입니다. */
    val isInitialError: Boolean
        get() = links.isEmpty() && loadStatus == RecentLinksLoadStatus.Error
}
