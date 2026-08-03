package com.linku.home.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.AiArticle
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.link.ToastEvent
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.home.util.UrlValidationResult
import com.linku.home.util.toToastMessage
import com.linku.home.util.validateUrlInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LinkViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
) : ViewModel() {

    /*
     * Save link state
     */

    private val saveImageState = mutableStateOf<TempImageFile?>(null)
    private val saveUrlState = mutableStateOf("")
    private val saveTitleState = mutableStateOf("")
    private val saveMemoState = mutableStateOf("")
    private val saveEmotionIdState = mutableStateOf<Long?>(null)
    private val saveSituationIdState = mutableStateOf<Long?>(null)
    private val jobIdState = mutableStateOf<Long?>(null)
    private val isSavingState = mutableStateOf(false)

    val saveImage get() = saveImageState.value
    val saveUrl get() = saveUrlState.value
    val saveTitle get() = saveTitleState.value
    val saveMemo get() = saveMemoState.value
    val selectedSaveEmotionId get() = saveEmotionIdState.value
    val selectedSaveSituationId get() = saveSituationIdState.value
    val jobId get() = jobIdState.value
    val isSaving get() = isSavingState.value

    val isSaveButtonEnabled: Boolean
        get() = saveUrlState.value.isNotBlank() && !isSavingState.value

    private val _toastEvent = Channel<ToastEvent>(Channel.BUFFERED)
    val toastEvent = _toastEvent.receiveAsFlow()

    /*
     * Link detail state
     */

    private data class Cached<T>(
        val value: T,
        val ts: Long = System.currentTimeMillis(),
    )

    private val linkCache =
        mutableMapOf<Long, Cached<LinkResultInfo>>()

    private val detailTtl = 60_000L

    private val linkDetailState =
        mutableStateOf<LinkResultInfo?>(null)

    val linkDetail get() = linkDetailState.value

    private val isLoadingLinkDetailState =
        mutableStateOf(false)

    val isLoadingLinkDetail
        get() = isLoadingLinkDetailState.value

    private val aiArticleDetailState =
        mutableStateOf<AiArticle?>(null)

    val aiArticleDetail
        get() = aiArticleDetailState.value

    private val isLoadingAiArticleState =
        mutableStateOf(false)

    val isLoadingAiArticle
        get() = isLoadingAiArticleState.value

    private val _aiProgress = MutableStateFlow(0f)
    val aiProgress: StateFlow<Float> =
        _aiProgress.asStateFlow()

    private var aiJob: Job? = null
    private var aiProgressJob: Job? = null

    private val isUpdatingLinkState =
        mutableStateOf(false)

    val isUpdatingLink
        get() = isUpdatingLinkState.value

    private val isDeletingLinkState =
        mutableStateOf(false)

    val isDeletingLink
        get() = isDeletingLinkState.value

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
                    jobIdState.value = userInfo.jobId
                }
                .onFailure { error ->
                    Log.e(
                        "LinkViewModel",
                        "loadUserBasics failed",
                        error,
                    )
                }
        }
    }

    fun setSaveImage(file: TempImageFile?) {
        saveImageState.value = file
    }

    fun deleteSaveImage() {
        saveImageState.value = null
    }

    fun setSaveUrl(newUrl: String) {
        saveUrlState.value = newUrl
    }

    fun setSaveTitle(newTitle: String) {
        saveTitleState.value = newTitle
    }

    fun setSaveMemo(newMemo: String) {
        saveMemoState.value = newMemo
    }

    fun selectSaveEmotion(id: Long?) {
        saveEmotionIdState.value = id
    }

    fun onSaveSituationClick(id: Long) {
        saveSituationIdState.value =
            if (saveSituationIdState.value == id) {
                null
            } else {
                id
            }
    }

    fun resetSaveForm() {
        saveImageState.value = null
        saveUrlState.value = ""
        saveTitleState.value = ""
        saveMemoState.value = ""
        saveEmotionIdState.value = null
        saveSituationIdState.value = null
    }

    fun onSaveButtonClick(
        onSucceed: (LinkSimpleInfo) -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        if (isSavingState.value) {
            sendToast("링크를 저장하고 있어요.")
            return
        }

        val currentUrl = saveUrlState.value.trim()
        val validationResult = validateUrlInput(currentUrl)

        if (validationResult != UrlValidationResult.Valid) {
            sendToast(validationResult.toToastMessage())
            return
        }

        viewModelScope.launch {
            isSavingState.value = true

            try {
                when (linkuRepository.checkLink(currentUrl)) {
                    LinkCheckResult.Available -> {
                        saveLink(
                            url = currentUrl,
                            onSucceed = onSucceed,
                        )
                    }

                    LinkCheckResult.AlreadySaved -> {
                        sendToast("이미 저장된 링크예요.")
                    }
                }
            } catch (error: Exception) {
                sendToast(error.toLinkCheckToastMessage())
                onFailed(error)
            } finally {
                isSavingState.value = false
            }
        }
    }

    private suspend fun saveLink(
        url: String,
        onSucceed: (LinkSimpleInfo) -> Unit,
    ) {
        val saved = linkuRepository.saveNewLink(
            image = saveImageState.value,
            url = url,
            title = saveTitleState.value.ifBlank { null },
            memo = saveMemoState.value.ifBlank { null },
            emotionId = saveEmotionIdState.value,
            situationId = saveSituationIdState.value,
        )

        sendToast("링크가 저장되었어요.")
        onSucceed(saved)
    }

    private fun sendToast(message: String) {
        viewModelScope.launch {
            _toastEvent.send(
                ToastEvent(message = message),
            )
        }
    }

    /*
     * Link detail
     */

    fun loadLinkDetail(
        linkuId: Long,
        forceRefresh: Boolean = false,
    ) {
        val now = System.currentTimeMillis()
        val cached = linkCache[linkuId]

        if (
            !forceRefresh &&
            cached != null &&
            now - cached.ts < detailTtl
        ) {
            linkDetailState.value = cached.value
            isLoadingLinkDetailState.value = false

            viewModelScope.launch {
                runCatching {
                    linkuRepository.getLinkDetail(linkuId)
                }.onSuccess { refreshed ->
                    linkCache[linkuId] = Cached(refreshed)
                    linkDetailState.value = refreshed
                    aiArticleDetailState.value = null
                }.onFailure { error ->
                    Log.e(
                        "LinkViewModel",
                        "refresh detail failed",
                        error,
                    )
                }
            }

            return
        }

        viewModelScope.launch {
            isLoadingLinkDetailState.value = cached == null

            runCatching {
                linkuRepository.getLinkDetail(linkuId)
            }.onSuccess { detail ->
                linkCache[linkuId] = Cached(detail)
                linkDetailState.value = detail
                aiArticleDetailState.value = null
            }.onFailure { error ->
                Log.e(
                    "LinkViewModel",
                    "load detail failed",
                    error,
                )

                if (cached == null) {
                    linkDetailState.value = null
                }
            }

            isLoadingLinkDetailState.value = false
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
        val current = linkDetailState.value ?: run {
            onFailed(
                IllegalStateException("링크 상세가 없습니다."),
            )
            return
        }

        if (isUpdatingLinkState.value) return

        val linkuId = current.linkuId

        val normalizedTitle = title.trim()
        val normalizedMemo = memo?.trim().orEmpty()

        val changedTitle =
            normalizedTitle.takeIf { it != current.title }

        val changedMemo =
            normalizedMemo.takeIf {
                it != current.memo.orEmpty()
            }

        val changedCategoryId =
            categoryId.takeIf {
                it != current.categoryId
            }

        val changedEmotionId =
            emotionId.takeIf {
                it != current.emotionId
            }

        val changedSituationId =
            situationId.takeIf {
                it != current.situationId
            }

        val hasChanges =
            image != null ||
                    changedTitle != null ||
                    changedMemo != null ||
                    changedCategoryId != null ||
                    changedEmotionId != null ||
                    changedSituationId != null

        if (!hasChanges) {
            onSucceed(current)
            return
        }

        viewModelScope.launch {
            isUpdatingLinkState.value = true

            runCatching {
                linkuRepository.updateLink(
                    linkuId = linkuId,
                    image = image,
                    memo = changedMemo,
                    emotionId = changedEmotionId,
                    situationId = changedSituationId,
                    categoryId = changedCategoryId,
                    title = changedTitle,
                )
            }.onSuccess { updated ->
                linkDetailState.value = updated
                linkCache[linkuId] = Cached(updated)
                onSucceed(updated)
            }.onFailure { error ->
                Log.e(
                    "LinkViewModel",
                    "update link failed",
                    error,
                )
                onFailed(error)
            }

            isUpdatingLinkState.value = false
        }
    }

    fun deleteCurrentLink(
        onSucceed: () -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val current = linkDetailState.value ?: run {
            onFailed(
                IllegalStateException("링크 상세가 없습니다."),
            )
            return
        }

        val userLinkuId = current.userLinkuId ?: run {
            onFailed(
                IllegalStateException("userLinkuId가 없습니다."),
            )
            return
        }

        deleteLink(
            userLinkuId = userLinkuId,
            linkuId = current.linkuId,
            onSucceed = {
                linkDetailState.value = null
                aiArticleDetailState.value = null
                onSucceed()
            },
            onFailed = onFailed,
        )
    }

    fun deleteLink(
        userLinkuId: Long,
        linkuId: Long? = null,
        onSucceed: () -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        if (isDeletingLinkState.value) return

        viewModelScope.launch {
            isDeletingLinkState.value = true

            runCatching {
                linkuRepository.deleteLink(
                    userLinkuId = userLinkuId,
                )
            }.onSuccess {
                linkuId?.let(linkCache::remove)
                onSucceed()
            }.onFailure { error ->
                Log.e(
                    "LinkViewModel",
                    "delete link failed",
                    error,
                )
                onFailed(error)
            }

            isDeletingLinkState.value = false
        }
    }

    fun clearLinkDetail() {
        linkDetailState.value = null
        aiArticleDetailState.value = null
    }

    override fun onCleared() {
        aiJob?.cancel()
        aiProgressJob?.cancel()
        super.onCleared()
    }
}

private fun Throwable.toLinkCheckToastMessage(): String {
    val errorMessage = message.orEmpty()

    val errorBody = runCatching {
        (this as? HttpException)
            ?.response()
            ?.errorBody()
            ?.string()
    }.getOrNull().orEmpty()

    val errorContent = "$errorMessage $errorBody"

    return when {
        errorContent.contains(
            "LINKU4001",
            ignoreCase = true,
        ) ||
                errorContent.contains(
                    "영상",
                    ignoreCase = true,
                ) -> {
            "영상 콘텐츠는 지원하지 않아요!"
        }

        else -> {
            "유효하지 않은 링크입니다!"
        }
    }
}