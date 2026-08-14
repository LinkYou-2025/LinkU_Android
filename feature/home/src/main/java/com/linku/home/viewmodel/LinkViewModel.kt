package com.linku.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.AppError
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.link.ToastEvent
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.CheckLinkUseCase
import com.linku.data.preference.AuthPreference
import com.linku.home.util.UrlValidationResult
import com.linku.home.util.toToastMessage
import com.linku.home.util.validateUrlInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
    private val categoryRepository: CategoryRepository,
    private val checkLinkUseCase: CheckLinkUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LinkUiState(
            saveImage = null,
            saveUrl = "",
            saveTitle = "",
            saveMemo = "",
            selectedSaveEmotionId = null,
            selectedSaveSituationId = null,
            jobId = null,
            isSaving = false,
            linkDetail = null,
            isLoadingLinkDetail = false,
            isUpdatingLink = false,
            isDeletingLink = false,
        ),
    )
    val uiState: StateFlow<LinkUiState> = _uiState.asStateFlow()

    private val _toastEvent = Channel<ToastEvent>(Channel.BUFFERED)
    val toastEvent = _toastEvent.receiveAsFlow()

    /*
     * Link detail state
     */

    private data class Cached<T>(val value: T, val ts: Long = System.currentTimeMillis())

    private val linkCache = mutableMapOf<Long, Cached<LinkResultInfo>>()

    private val detailTtl = 60_000L

    init {
        loadUserBasics()
    }

    /*
     * Save link
     */

    fun loadUserBasics() {
        viewModelScope.launch {
            val userId = authPreference.getUserId()

            if (userId == null || userId <= 0L) {
                return@launch
            }

            userRepository.getUserInfo(userId)
                .onSuccess { userInfo ->
                    _uiState.update { state -> state.copy(jobId = userInfo.jobId) }
                }
                .onFailure { error ->
                    Log.e("LinkViewModel", "loadUserBasics failed", error)
                }
        }
    }

    fun setSaveImage(file: TempImageFile?) {
        _uiState.update { state -> state.copy(saveImage = file) }
    }

    fun deleteSaveImage() {
        _uiState.update { state -> state.copy(saveImage = null) }
    }

    fun setSaveUrl(newUrl: String) {
        _uiState.update { state -> state.copy(saveUrl = newUrl) }
    }

    fun setSaveTitle(newTitle: String) {
        _uiState.update { state -> state.copy(saveTitle = newTitle) }
    }

    fun setSaveMemo(newMemo: String) {
        _uiState.update { state -> state.copy(saveMemo = newMemo) }
    }

    fun selectSaveEmotion(id: Long?) {
        _uiState.update { state -> state.copy(selectedSaveEmotionId = id) }
    }

    fun onSaveSituationClick(id: Long) {
        _uiState.update { state ->
            state.copy(selectedSaveSituationId = id.takeUnless { state.selectedSaveSituationId == id })
        }
    }

    fun resetSaveForm() {
        _uiState.update { state ->
            state.copy(
                saveImage = null,
                saveUrl = "",
                saveTitle = "",
                saveMemo = "",
                selectedSaveEmotionId = null,
                selectedSaveSituationId = null,
            )
        }
    }

    fun onSaveButtonClick(onSucceed: (LinkSimpleInfo) -> Unit = {}, onFailed: (Exception) -> Unit = {}) {
        val currentState = _uiState.value

        if (currentState.isSaving) {
            sendToast("링크를 저장하고 있어요.")
            return
        }

        val currentUrl = currentState.saveUrl.trim()
        val validationResult = validateUrlInput(currentUrl)

        if (validationResult != UrlValidationResult.Valid) {
            sendToast(validationResult.toToastMessage())
            return
        }

        _uiState.update { state -> state.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val linkCheckResult = try {
                    checkLinkUseCase(currentUrl)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: AppError) {
                    sendToast(error.displayMessage)
                    onFailed(error)
                    return@launch
                }

                when (linkCheckResult) {
                    LinkCheckResult.Available,
                    LinkCheckResult.AlreadySaved -> {
                        try {
                            saveLink(state = currentState, url = currentUrl, onSucceed = onSucceed)
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            Log.e("LinkViewModel", "save link failed", error)
                            sendToast("링크 저장에 실패했어요. 다시 시도해 주세요.")
                            onFailed(error)
                        }
                    }
                }
            } finally {
                _uiState.update { state -> state.copy(isSaving = false) }
            }
        }
    }

    private suspend fun saveLink(state: LinkUiState, url: String, onSucceed: (LinkSimpleInfo) -> Unit) {
        val saved = linkuRepository.saveNewLink(
            image = state.saveImage,
            url = url,
            title = state.saveTitle.ifBlank { null },
            memo = state.saveMemo.ifBlank { null },
            emotionId = state.selectedSaveEmotionId,
            situationId = state.selectedSaveSituationId,
        )

        sendToast("링크가 저장되었어요.")
        onSucceed(saved)
    }

    private fun sendToast(message: String) {
        viewModelScope.launch {
            _toastEvent.send(ToastEvent(message = message))
        }
    }

    /*
     * Link detail
     */

    /**
     * 링크 수정 카테고리 드롭다운에 사용할 카테고리 목록을 불러옵니다.
     *
     * 서버가 제공하는 카테고리 ID, 이름, 색상 코드를 그대로 보존하며, 이미 목록이 있거나
     * 요청이 진행 중이면 중복 호출하지 않습니다. 빈 목록을 받으면 다음 화면 진입에서 다시
     * 시도할 수 있도록 로드 완료 상태로 캐시하지 않습니다.
     *
     * @param forceRefresh 기존 목록이 있어도 서버에서 다시 조회할지 여부입니다.
     */
    fun loadLinkEditCategories(forceRefresh: Boolean = false) {
        val currentState = _uiState.value

        if (currentState.isLoadingLinkEditCategories) return
        if (!forceRefresh && currentState.linkEditCategories.isNotEmpty()) return

        _uiState.update { state ->
            state.copy(isLoadingLinkEditCategories = true)
        }

        viewModelScope.launch {
            try {
                val categories = categoryRepository.getCategoryColor()

                if (categories.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(linkEditCategories = categories)
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "load link edit categories failed", error)
            } finally {
                _uiState.update { state ->
                    state.copy(isLoadingLinkEditCategories = false)
                }
            }
        }
    }

    fun loadLinkDetail(userLinkuId: Long, forceRefresh: Boolean = false) {
        val now = System.currentTimeMillis()
        val cached = linkCache[userLinkuId]

        if (!forceRefresh && cached != null && now - cached.ts < detailTtl) {
            _uiState.update { state -> state.copy(linkDetail = cached.value, isLoadingLinkDetail = false) }

            viewModelScope.launch {
                try {
                    val refreshed = linkuRepository.getLinkDetail(userLinkuId)
                    linkCache[userLinkuId] = Cached(refreshed)
                    _uiState.update { state -> state.copy(linkDetail = refreshed) }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.e("LinkViewModel", "refresh detail failed", error)
                }
            }

            return
        }

        viewModelScope.launch {
            _uiState.update { state -> state.copy(linkDetail = cached?.value, isLoadingLinkDetail = cached == null) }

            try {
                val detail = linkuRepository.getLinkDetail(userLinkuId)
                linkCache[userLinkuId] = Cached(detail)
                _uiState.update { state -> state.copy(linkDetail = detail) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "load detail failed", error)

                if (cached == null) {
                    _uiState.update { state -> state.copy(linkDetail = null) }
                }
            } finally {
                _uiState.update { state -> state.copy(isLoadingLinkDetail = false) }
            }
        }
    }

    fun updateLink(
        image: TempImageFile?,
        title: String,
        memo: String?,
        categoryId: Long?,
        emotionId: Long?,
        situationId: Long?,
        onSucceed: (LinkResultInfo) -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val currentState = _uiState.value
        val current = currentState.linkDetail ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }

        if (currentState.isUpdatingLink) return

        val userLinkuId = current.userLinkuId

        val normalizedTitle = title.trim()

        val changedTitle = normalizedTitle.takeIf { it != current.title }
        val changedMemo = memo?.trim()?.takeIf { normalizedMemo -> normalizedMemo != current.memo.orEmpty() }
        val changedCategoryId = categoryId.takeIf { it != current.categoryId }
        val changedEmotionId = emotionId.takeIf { it != current.emotionId }
        val changedSituationId = situationId.takeIf { it != current.situationId }

        val hasChanges = image != null ||
            changedTitle != null ||
            changedMemo != null ||
            changedCategoryId != null ||
            changedEmotionId != null ||
            changedSituationId != null

        if (!hasChanges) {
            onSucceed(current)
            return
        }

        _uiState.update { state -> state.copy(isUpdatingLink = true) }

        viewModelScope.launch {
            try {
                val updated = linkuRepository.updateLink(
                    userLinkuId = userLinkuId,
                    image = image,
                    memo = changedMemo,
                    emotionId = changedEmotionId,
                    situationId = changedSituationId,
                    categoryId = changedCategoryId,
                    title = changedTitle,
                )
                linkCache[userLinkuId] = Cached(updated)
                _uiState.update { state -> state.copy(linkDetail = updated) }
                onSucceed(updated)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "update link failed", error)
                onFailed(error)
            } finally {
                _uiState.update { state -> state.copy(isUpdatingLink = false) }
            }
        }
    }

    fun deleteCurrentLink(onSucceed: () -> Unit = {}, onFailed: (Throwable) -> Unit = {}) {
        val current = _uiState.value.linkDetail ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }

        deleteLink(
            userLinkuId = current.userLinkuId,
            onSucceed = {
                _uiState.update { state -> state.copy(linkDetail = null) }
                onSucceed()
            },
            onFailed = onFailed,
        )
    }

    fun deleteLink(
        userLinkuId: Long,
        onSucceed: () -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        if (_uiState.value.isDeletingLink) return

        _uiState.update { state -> state.copy(isDeletingLink = true) }

        viewModelScope.launch {
            try {
                linkuRepository.deleteLink(userLinkuId = userLinkuId)
                linkCache.remove(userLinkuId)
                onSucceed()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "delete link failed", error)
                onFailed(error)
            } finally {
                _uiState.update { state -> state.copy(isDeletingLink = false) }
            }
        }
    }

    fun clearLinkDetail() {
        _uiState.update { state -> state.copy(linkDetail = null, isLoadingLinkDetail = false) }
    }
}
