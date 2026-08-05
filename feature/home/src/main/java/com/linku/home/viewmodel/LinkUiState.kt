package com.linku.home.viewmodel

import com.linku.core.model.LinkResultInfo
import com.linku.core.model.TempImageFile

data class LinkUiState(
    val saveImage: TempImageFile? = null,
    val saveUrl: String = "",
    val saveTitle: String = "",
    val saveMemo: String = "",
    val selectedSaveEmotionId: Long? = null,
    val selectedSaveSituationId: Long? = null,
    val jobId: Long? = null,
    val isSaving: Boolean = false,
    val linkDetail: LinkResultInfo? = null,
    val isLoadingLinkDetail: Boolean = false,
    val isUpdatingLink: Boolean = false,
    val isDeletingLink: Boolean = false,
) {
    val isSaveButtonEnabled: Boolean
        get() = saveUrl.isNotBlank() && !isSaving
}
