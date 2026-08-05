package com.linku.home.viewmodel

import com.linku.core.model.LinkResultInfo
import com.linku.core.model.TempImageFile

data class LinkUiState(
    val saveImage: TempImageFile?,
    val saveUrl: String,
    val saveTitle: String,
    val saveMemo: String,
    val selectedSaveEmotionId: Long?,
    val selectedSaveSituationId: Long?,
    val jobId: Long?,
    val isSaving: Boolean,
    val linkDetail: LinkResultInfo?,
    val isLoadingLinkDetail: Boolean,
    val isUpdatingLink: Boolean,
    val isDeletingLink: Boolean,
) {
    val isSaveButtonEnabled: Boolean
        get() = saveUrl.isNotBlank() && !isSaving
}
