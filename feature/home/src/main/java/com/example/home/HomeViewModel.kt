package com.example.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.LinkuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository
) : ViewModel() {
    // 새로운 링크 저장
    private val imageState = mutableStateOf<File?>(null)
    private val urlState = mutableStateOf("")
    private val memoState = mutableStateOf("")
    private val emotionIdState = mutableStateOf<Long?>(null)
    private val isSavingState = mutableStateOf(false)

    // URL 유효성 검사
    private val isCheckingUrlState = mutableStateOf(false)
    private val isDuplicateUrlState = mutableStateOf<Boolean?>(null)
    private var checkJob: Job? = null

    val image get() = imageState.value
    val url get() = urlState.value
    val memo get() = memoState.value
    val selectedEmotionId get() = emotionIdState.value
    val isSaving get() = isSavingState.value

    val isCheckingUrl get() = isCheckingUrlState.value
    val isDuplicateUrl get() = isDuplicateUrlState.value
    private val isInvalidUrlState = mutableStateOf(false)
    val isInvalidUrl get() = isInvalidUrlState.value

    fun setImage(file: File?) { imageState.value = file }
    fun setUrl(newUrl: String) {
        urlState.value = newUrl

        // invalid 판정
        isInvalidUrlState.value =
            newUrl.isNotBlank() && !android.webkit.URLUtil.isValidUrl(newUrl)

        // 디바운스 검사
        checkJob?.cancel()
        isDuplicateUrlState.value = null
        if (newUrl.isBlank()) {
            isCheckingUrlState.value = false
            return
        }
        checkJob = viewModelScope.launch {
            isCheckingUrlState.value = true
            delay(300)
            runCatching { linkuRepository.checkLink(newUrl) }
                .onSuccess { exists -> isDuplicateUrlState.value = exists }
                .onFailure { isDuplicateUrlState.value = null }
            isCheckingUrlState.value = false
        }
    }
    fun setMemo(newMemo: String) { memoState.value = newMemo }
    fun selectEmotion(id: Long?) { emotionIdState.value = id }



    // 저장 폼 초기화
    fun resetForm() {
        imageState.value = null
        urlState.value = ""
        memoState.value = ""
        emotionIdState.value = null
    }

    private val recentLinksState = mutableStateOf(listOf<LinkSimpleInfo>())

    init {
//        loadRecentLinks()
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

    // 링크 유효성 검사


    // 최근 조회 링크 로딩
//    fun loadRecentLinks() {
//        viewModelScope.launch {
//            runCatching {
//                linkuRepository.getRecentLinks()
//            }.onSuccess {
//                recentLinks = it
//            }.onFailure {
//                recentLinks = emptyList()
//            }
//        }
//    }
}
