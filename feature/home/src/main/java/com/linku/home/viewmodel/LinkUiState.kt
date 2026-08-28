package com.linku.home.viewmodel

import com.linku.core.model.CategoryColorList
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.TempImageFile

/**
 * 링크 저장과 상세·수정 화면에서 사용하는 상태를 보관합니다.
 *
 * @property jobId 마지막으로 조회에 성공한 로그인 사용자의 직업 ID입니다.
 * @property userJobRequestId 가장 최근에 시작한 사용자 직업 조회의 세대 ID입니다.
 * @property isUserJobReady 가장 최근 사용자 직업 조회가 성공해 현재 직업을 신뢰할 수 있는지 나타냅니다.
 * @property linkEditCategories 링크 수정 카테고리 드롭다운에 표시할 서버 카테고리 목록입니다.
 * @property isLoadingLinkEditCategories 링크 수정용 카테고리 목록을 불러오는 중인지 나타냅니다.
 * @property requestedLinkDetailId 현재 상세 조회가 대상으로 삼는 사용자 링크 ID입니다.
 * @property linkDetailLoadError 현재 상세 조회 대상의 로딩 실패 원인입니다.
 */
data class LinkUiState(
    val saveImage: TempImageFile?,
    val saveUrl: String,
    val saveTitle: String,
    val saveMemo: String,
    val selectedSaveEmotionId: Long?,
    val selectedSaveSituationId: Long?,
    val jobId: Long?,
    val userJobRequestId: Long,
    val isUserJobReady: Boolean,
    val isSaving: Boolean,
    val linkDetail: LinkResultInfo?,
    val requestedLinkDetailId: Long?,
    val isLoadingLinkDetail: Boolean,
    val linkDetailLoadError: Throwable?,
    val isUpdatingLink: Boolean,
    val isDeletingLink: Boolean,
    val linkEditCategories: List<CategoryColorList> = emptyList(),
    val isLoadingLinkEditCategories: Boolean = false,
) {
    val isSaveButtonEnabled: Boolean
        get() = saveUrl.isNotBlank() && !isSaving
}
