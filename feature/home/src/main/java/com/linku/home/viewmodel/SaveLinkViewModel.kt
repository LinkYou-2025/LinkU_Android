package com.linku.home.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.link.ToastEvent
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.home.util.UrlValidationResult
import com.linku.home.util.toToastMessage
import com.linku.home.util.validateUrlInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    /**
     * 저장 버튼은 URL이 비어 있지 않고 저장 중이 아닐 때만 활성화합니다.
     *
     * 실제 링크 유효성 검사는 저장 버튼 클릭 시 진행합니다.
     */
    val isSaveButtonEnabled: Boolean
        get() = urlState.value.isNotBlank() && !isSavingState.value

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
        isSavingState.value = false
    }

    /**
     * 저장 버튼 클릭 시 실행됩니다.
     *
     * 처리 순서:
     * 1. 프론트 URL 형식 검사
     * 2. 백엔드 링크 검사 API 호출
     * 3. 이미 저장한 링크인지 확인
     * 4. 통과 시 링크 저장
     */
    fun onSaveButtonClick(
        onSucceed: (saved: LinkSimpleInfo) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        if (isSavingState.value) {
            sendToast("링크를 저장하고 있어요.")
            return
        }

        val currentUrl = urlState.value.trim()

        val frontValidationResult = validateUrlInput(currentUrl)

        if (frontValidationResult != UrlValidationResult.Valid) {
            sendToast(frontValidationResult.toToastMessage())
            return
        }

        viewModelScope.launch {
            isSavingState.value = true

            try {
                when (linkuRepository.checkLink(currentUrl)) {
                    LinkCheckResult.Available -> {
                        saveLink(
                            url = currentUrl,
                            onSucceed = onSucceed
                        )
                    }

                    LinkCheckResult.AlreadySaved -> {
                        sendToast("이미 저장된 링크예요.")
                    }
                }
            } catch (e: Exception) {
                sendToast(e.toLinkCheckToastMessage())
                onFailed(e)
            } finally {
                isSavingState.value = false
            }
        }
    }

    /**
     * 프론트 검증과 백엔드 링크 검사를 모두 통과한 뒤 실제 링크 저장을 수행합니다.
     */
    private suspend fun saveLink(
        url: String,
        onSucceed: (saved: LinkSimpleInfo) -> Unit,
    ) {
        val saved = linkuRepository.saveNewLink(
            image = imageState.value,
            url = url,
            memo = memoState.value.ifBlank { null },
            emotionId = emotionIdState.value,
            // TODO: 저장 API에 situationId가 연결되면 아래 값도 전달
            // situationId = situationIdState.value
        )

        sendToast("링크가 저장되었어요.")
        onSucceed(saved)
    }

    private fun sendToast(message: String) {
        viewModelScope.launch {
            _toastEvent.send(
                ToastEvent(message = message)
            )
        }
    }
}

private fun Throwable.toLinkCheckToastMessage(): String {
    val rawMessage = message.orEmpty()

    return when {
        rawMessage.contains("LINKU4001", ignoreCase = true) ||
                rawMessage.contains("영상", ignoreCase = true) -> {
            "영상 콘텐츠는 지원하지 않아요!"
        }

        rawMessage.contains("LINKU4002", ignoreCase = true) ||
                rawMessage.contains("유효하지 않은 링크", ignoreCase = true) -> {
            "유효하지 않은 링크입니다!"
        }

        else -> {
            "유효하지 않은 링크입니다!"
        }
    }
}