package com.example.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.LinkuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository
) : ViewModel() {
    private val imageState = mutableStateOf<File?>(null)
    private val urlState = mutableStateOf("")
    private val memoState = mutableStateOf("")
    private val emotionIdState = mutableStateOf<Long?>(null)

    private val isSavingState = mutableStateOf(false)

    val image get() = imageState.value
    val url get() = urlState.value
    val memo get() = memoState.value
    val selectedEmotionId get() = emotionIdState.value
    val isSaving get() = isSavingState.value

    fun setImage(file: File?) { imageState.value = file }
    fun setUrl(newUrl: String) { urlState.value = newUrl }
    fun setMemo(newMemo: String) { memoState.value = newMemo }
    fun selectEmotion(id: Long?) { emotionIdState.value = id }

    var recentLinks by mutableStateOf<List<LinkSimpleInfo>>(emptyList())
        private set

    init {
        loadRecentLinks()
    }

    // 링크 저장
    fun saveLink(
        onSucceed: (saved: LinkSimpleInfo) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        if (isSavingState.value) return // 중복 클릭 방지

        val currentUrl = urlState.value
        if (currentUrl.isBlank()) {
            onFailed(IllegalArgumentException("URL을 입력해 주세요."))
            return
        }

        isSavingState.value = true
        viewModelScope.launch {
            try {
                val saved = linkuRepository.saveNewLink(
                    image = imageState.value,
                    url = currentUrl,
                    memo = memoState.value.ifBlank { null },
                    emotionId = emotionIdState.value
                )
                onSucceed(saved)
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                isSavingState.value = false
            }
        }
    }

    /** 저장 폼 초기화 (필요 시 호출) */
    fun resetForm() {
        imageState.value = null
        urlState.value = ""
        memoState.value = ""
        emotionIdState.value = null
    }

    fun loadRecentLinks() {
        viewModelScope.launch {
            runCatching {
                linkuRepository.getRecentLinks()
            }.onSuccess {
                recentLinks = it
            }.onFailure {
                recentLinks = emptyList()
            }
        }
    }
}
