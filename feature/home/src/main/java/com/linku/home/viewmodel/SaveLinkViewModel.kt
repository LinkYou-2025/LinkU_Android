package com.linku.home.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.LinkSimpleInfo
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SaveLinkViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
) : ViewModel() {

    private val imageState = mutableStateOf<File?>(null)
    private val urlState = mutableStateOf("")
    private val titleState = mutableStateOf("")
    private val memoState = mutableStateOf("")
    private val emotionIdState = mutableStateOf<Long?>(null)
    private val situationIdState = mutableStateOf<Long?>(null)
    private val jobIdState = mutableStateOf<Long?>(null)
    private val isSavingState = mutableStateOf(false)

    private val isCheckingUrlState = mutableStateOf(false)
    private val isDuplicateUrlState = mutableStateOf<Boolean?>(null)
    private var checkJob: Job? = null

    private val _toastEvent = Channel<ToastEvent>(Channel.BUFFERED)
    val toastEvent = _toastEvent.receiveAsFlow()

    val image get() = imageState.value
    val url get() = urlState.value
    val title get() = titleState.value
    val memo get() = memoState.value
    val selectedEmotionId get() = emotionIdState.value
    val selectedSituationId get() = situationIdState.value
    val jobId get() = jobIdState.value
    val isSaving get() = isSavingState.value
    val isCheckingUrl get() = isCheckingUrlState.value
    val isDuplicateUrl get() = isDuplicateUrlState.value

    val isSaveButtonEnabled: Boolean
        get() {
            val urlValidationResult = validateUrlInput(urlState.value)

            return urlValidationResult == UrlValidationResult.Valid &&
                    !isCheckingUrlState.value &&
                    isDuplicateUrlState.value != true &&
                    !isSavingState.value
        }

    init {
        loadUserBasics()
    }

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
        }
    }

    fun setImage(file: File?) {
        imageState.value = file
    }

    fun deleteImage() {
        imageState.value = null
    }

    fun setUrl(newUrl: String) {
        urlState.value = newUrl

        checkJob?.cancel()
        isDuplicateUrlState.value = null

        val urlValidationResult = validateUrlInput(newUrl)

        if (urlValidationResult != UrlValidationResult.Valid) {
            isCheckingUrlState.value = false
            return
        }

        checkJob = viewModelScope.launch {
            isCheckingUrlState.value = true
            delay(300)

            runCatching {
                linkuRepository.checkLink(newUrl)
            }.onSuccess { exists ->
                isDuplicateUrlState.value = exists
            }.onFailure {
                isDuplicateUrlState.value = null
            }

            isCheckingUrlState.value = false
        }
    }

    fun setTitle(newTitle: String) {
        titleState.value = newTitle
    }

    fun setMemo(newMemo: String) {
        memoState.value = newMemo
    }

    fun selectEmotion(id: Long?) {
        emotionIdState.value = id
    }

    fun onSituationClick(id: Long) {
        situationIdState.value = if (situationIdState.value == id) {
            null
        } else {
            id
        }
    }

    fun resetForm() {
        imageState.value = null
        urlState.value = ""
        titleState.value = ""
        memoState.value = ""
        emotionIdState.value = null
        situationIdState.value = null
        isDuplicateUrlState.value = null
        isCheckingUrlState.value = false
        isSavingState.value = false
        checkJob?.cancel()
    }

    fun onSaveButtonClick(
        onSucceed: (saved: LinkSimpleInfo) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        val currentUrl = urlState.value
        val urlValidationResult = validateUrlInput(currentUrl)

        val blockReason = when {
            urlValidationResult != UrlValidationResult.Valid -> {
                urlValidationResult.toToastMessage()
            }

            isCheckingUrlState.value -> {
                "링크를 확인하고 있어요."
            }

            isDuplicateUrlState.value == true -> {
                "이미 저장된 링크예요."
            }

            isSavingState.value -> {
                "링크를 저장하고 있어요."
            }

            else -> null
        }

        if (blockReason != null) {
            sendToast(message = blockReason)
            return
        }

        saveLink(
            onSucceed = { saved ->
                sendToast(message = "링크가 저장되었어요.")
                onSucceed(saved)
            },
            onFailed = { e ->
                sendToast(message = e.message ?: "링크 저장에 실패했어요.")
                onFailed(e)
            }
        )
    }

    private fun saveLink(
        onSucceed: (saved: LinkSimpleInfo) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        if (isSavingState.value) return

        val currentUrl = urlState.value

        if (currentUrl.isBlank()) {
            onFailed(IllegalArgumentException("URL을 입력해 주세요."))
            return
        }

        if (isDuplicateUrlState.value == true) {
            onFailed(IllegalStateException("이미 저장된 링크입니다."))
            return
        }

        isSavingState.value = true

        viewModelScope.launch {
            try {
                val saved = linkuRepository.saveNewLink(
                    image = imageState.value,
                    url = currentUrl,
                    memo = memoState.value.ifBlank { null },
                    emotionId = emotionIdState.value,
                    // TODO: 저장 API에 situationId가 연결되면 아래 값도 전달
                    // situationId = situationIdState.value
                )

                onSucceed(saved)
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                isSavingState.value = false
            }
        }
    }

    private fun sendToast(
        message: String
    ) {
        viewModelScope.launch {
            _toastEvent.send(
                ToastEvent(message = message)
            )
        }
    }
}