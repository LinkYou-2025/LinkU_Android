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
import kotlinx.coroutines.Job
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
            requestedLinkDetailId = null,
            isLoadingLinkDetail = false,
            linkDetailLoadError = null,
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

    /** 현재 서버 상세 조회를 수행하는 작업입니다. 다른 링크 요청이 시작되면 취소합니다. */
    private var linkDetailRequestJob: Job? = null

    /** 동일 ID 요청까지 구분하여 취소된 작업이 최신 상태를 덮어쓰지 못하게 하는 세대 값입니다. */
    private var linkDetailRequestGeneration: Long = 0L

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
                val savingUserId = try {
                    authPreference.getUserId()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.e("LinkViewModel", "failed to read user before saving link", error)
                    null
                }

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
                            saveLink(
                                state = currentState,
                                url = currentUrl,
                                userId = savingUserId,
                                onSucceed = onSucceed,
                            )
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

    /**
     * 링크를 저장하고, 성공한 URL과 저장 완료 시각을 클립보드 배너 재노출 방지 정보로 함께 기록합니다.
     *
     * ViewModel 작업 취소는 전파하고, 그 외 배너용 기록 실패는 이미 완료된 서버 저장의 성공 흐름을 막지 않습니다.
     *
     * @param state 저장 폼의 클릭 시점 스냅샷
     * @param url 프론트 검증을 통과해 실제 저장에 사용하는 URL
     * @param userId 저장 요청을 시작한 사용자 ID. 조회하지 못한 경우 `null`
     * @param onSucceed 서버 저장 성공 후 호출할 콜백
     */
    private suspend fun saveLink(
        state: LinkUiState,
        url: String,
        userId: Long?,
        onSucceed: (LinkSimpleInfo) -> Unit,
    ) {
        val saved = linkuRepository.saveNewLink(
            image = state.saveImage,
            url = url,
            title = state.saveTitle.ifBlank { null },
            memo = state.saveMemo.ifBlank { null },
            emotionId = state.selectedSaveEmotionId,
            situationId = state.selectedSaveSituationId,
        )

        if (userId != null) {
            try {
                authPreference.saveLastSavedLinkUrl(
                    url = url,
                    savedAtMillis = System.currentTimeMillis(),
                    userId = userId,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "failed to persist saved link URL", error)
            }
        }

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

    /**
     * 지정한 링크의 상세 정보를 불러옵니다.
     *
     * 현재 화면에 동일한 링크가 표시 중이면 서버 갱신 중에도 해당 콘텐츠를 유지합니다. 다른 링크를
     * 요청할 때는 과거 캐시가 있더라도 새 응답이 도착할 때까지 이전 링크를 숨기고 스켈레톤을 표시할
     * 수 있도록 요청 대상 ID를 먼저 반영합니다.
     * 새 요청이 시작되면 기존 요청을 취소하고 요청 세대를 함께 확인해 늦게 끝난 응답이 최신 상세를
     * 덮어쓰지 않도록 합니다.
     *
     * @param userLinkuId 조회할 사용자 링크 ID입니다.
     * @param forceRefresh 유효한 캐시가 있어도 서버 조회를 강제로 수행할지 여부입니다.
     */
    fun loadLinkDetail(userLinkuId: Long, forceRefresh: Boolean = false) {
        val currentState = _uiState.value

        // 저장 직후 선조회와 상세 라우트 진입이 연달아 호출되어도 같은 요청을 중복 실행하지 않습니다.
        if (
            !forceRefresh &&
            currentState.requestedLinkDetailId == userLinkuId &&
            linkDetailRequestJob?.isActive == true
        ) {
            return
        }

        val now = System.currentTimeMillis()
        val cached = linkCache[userLinkuId]
        val visibleDetail = currentState.linkDetail
        val detailToKeep = visibleDetail?.takeIf { detail ->
            detail.userLinkuId == userLinkuId
        }

        linkDetailRequestGeneration += 1L
        val requestGeneration = linkDetailRequestGeneration

        linkDetailRequestJob?.cancel()

        if (
            !forceRefresh &&
            detailToKeep != null &&
            cached != null &&
            now - cached.ts < detailTtl
        ) {
            _uiState.update { state ->
                state.copy(
                    linkDetail = detailToKeep,
                    requestedLinkDetailId = userLinkuId,
                    isLoadingLinkDetail = false,
                    linkDetailLoadError = null,
                )
            }

            // 유효한 캐시는 즉시 표시하고, 최신 값 확인은 화면을 가리지 않는 백그라운드 갱신으로 처리합니다.
            linkDetailRequestJob = viewModelScope.launch {
                try {
                    val refreshed = linkuRepository.getLinkDetail(userLinkuId)

                    if (isCurrentLinkDetailRequest(userLinkuId, requestGeneration)) {
                        linkCache[userLinkuId] = Cached(refreshed)
                        _uiState.update { state -> state.copy(linkDetail = refreshed) }
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.e("LinkViewModel", "refresh detail failed", error)
                }
            }

            return
        }

        _uiState.update { state ->
            state.copy(
                // 다른 링크의 마지막 콘텐츠는 상태에 보존하되 route ID 필터로 가려 빠른 뒤로가기를 지원합니다.
                linkDetail = detailToKeep ?: visibleDetail,
                requestedLinkDetailId = userLinkuId,
                isLoadingLinkDetail = detailToKeep == null,
                linkDetailLoadError = null,
            )
        }

        linkDetailRequestJob = viewModelScope.launch {
            try {
                val detail = linkuRepository.getLinkDetail(userLinkuId)

                if (isCurrentLinkDetailRequest(userLinkuId, requestGeneration)) {
                    linkCache[userLinkuId] = Cached(detail)
                    _uiState.update { state ->
                        state.copy(
                            linkDetail = detail,
                            linkDetailLoadError = null,
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LinkViewModel", "load detail failed", error)

                if (isCurrentLinkDetailRequest(userLinkuId, requestGeneration)) {
                    _uiState.update { state ->
                        // 동일 링크의 콘텐츠가 있으면 일시적인 갱신 실패로 화면을 대체하지 않습니다.
                        if (state.linkDetail?.userLinkuId == userLinkuId) {
                            state.copy(linkDetailLoadError = null)
                        } else {
                            state.copy(
                                linkDetailLoadError = error,
                            )
                        }
                    }
                }
            } finally {
                if (isCurrentLinkDetailRequest(userLinkuId, requestGeneration)) {
                    _uiState.update { state -> state.copy(isLoadingLinkDetail = false) }
                }
            }
        }
    }

    /**
     * 완료된 상세 요청이 현재 화면이 기다리는 최신 요청인지 확인합니다.
     *
     * @param userLinkuId 완료된 요청의 사용자 링크 ID입니다.
     * @param requestGeneration 완료된 요청이 시작될 때 부여된 세대 값입니다.
     */
    private fun isCurrentLinkDetailRequest(
        userLinkuId: Long,
        requestGeneration: Long,
    ): Boolean =
        _uiState.value.requestedLinkDetailId == userLinkuId &&
            linkDetailRequestGeneration == requestGeneration

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
        linkDetailRequestGeneration += 1L
        linkDetailRequestJob?.cancel()
        linkDetailRequestJob = null

        _uiState.update { state ->
            state.copy(
                linkDetail = null,
                requestedLinkDetailId = null,
                isLoadingLinkDetail = false,
                linkDetailLoadError = null,
            )
        }
    }
}
