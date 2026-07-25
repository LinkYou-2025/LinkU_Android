package com.linku.home.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.AiArticle
import com.linku.core.model.LinkResultInfo
import com.linku.core.repository.AIArticleRepository
import com.linku.core.repository.CategoryRepository
import com.linku.core.repository.LinkuRepository
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LinkDetailViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private data class Cached<T>(
        val value: T,
        val ts: Long = System.currentTimeMillis(),
    )

    private val linkCache = mutableMapOf<Long, Cached<LinkResultInfo>>()
    private val detailTtl = 60_000L

    private val linkDetailState = mutableStateOf<LinkResultInfo?>(null)
    val linkDetail get() = linkDetailState.value

    private val isLoadingLinkDetailState = mutableStateOf(false)
    val isLoadingLinkDetail get() = isLoadingLinkDetailState.value

    private val aiArticleDetailState = mutableStateOf<AiArticle?>(null)
    val aiArticleDetail get() = aiArticleDetailState.value

    private val isLoadingAiArticleState = mutableStateOf(false)
    val isLoadingAiArticle get() = isLoadingAiArticleState.value

    private val _aiProgress = MutableStateFlow(0f)
    val aiProgress: StateFlow<Float> = _aiProgress.asStateFlow()

    private var aiJob: Job? = null
    private var aiProgressJob: Job? = null

    private val isUpdatingLinkState = mutableStateOf(false)
    val isUpdatingLink get() = isUpdatingLinkState.value

    private val isDeletingLinkState = mutableStateOf(false)
    val isDeletingLink get() = isDeletingLinkState.value

    fun loadLinkDetail(
        linkuId: Long,
        forceRefresh: Boolean = false,
    ) {
        val now = System.currentTimeMillis()
        val cached = linkCache[linkuId]

        if (!forceRefresh && cached != null && now - cached.ts < detailTtl) {
            linkDetailState.value = cached.value
            isLoadingLinkDetailState.value = false

            viewModelScope.launch {
                runCatching {
                    linkuRepository.getLinkDetail(linkuId)
                }.onSuccess { refreshed ->
                    linkCache[linkuId] = Cached(refreshed)
                    linkDetailState.value = refreshed
                    aiArticleDetailState.value = null
                }.onFailure { e ->
                    Log.e("LinkDetailVM", "refresh detail failed", e)
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
            }.onFailure { e ->
                Log.e("LinkDetailVM", "load detail failed", e)
                if (cached == null) {
                    linkDetailState.value = null
                }
            }

            isLoadingLinkDetailState.value = false
        }
    }

    fun updateLink(
        image: File?,
        title: String,
        memo: String?,
        categoryId: Long?,
        emotionId: Long?,
        situationId: Long?,
        onSucceed: (LinkResultInfo) -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val current = linkDetailState.value ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }

        if (isUpdatingLinkState.value) return

        val fixedLinkuId = current.linkuId

        val normalizedTitle = title.trim()
        val normalizedMemo = memo?.trim().orEmpty()
        val changedTitle = normalizedTitle.takeIf { it != current.title }
        val changedMemo = normalizedMemo.takeIf { it != current.memo.orEmpty() }
        val changedCategoryId = categoryId.takeIf { it != current.categoryId }
        val changedEmotionId = emotionId.takeIf { it != current.emotionId }
        val changedSituationId = situationId.takeIf { it != current.situationId }

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
                    linkuId = fixedLinkuId,
                    image = image,
                    memo = changedMemo,
                    emotionId = changedEmotionId,
                    situationId = changedSituationId,
                    categoryId = changedCategoryId,
                    title = changedTitle,
                )
            }.onSuccess { updated ->
                linkDetailState.value = updated
                linkCache[fixedLinkuId] = Cached(updated)
                onSucceed(updated)
            }.onFailure { e ->
                Log.e("LinkDetailVM", "update link failed", e)
                onFailed(e)
            }

            isUpdatingLinkState.value = false
        }
    }

    fun deleteLink(
        onSucceed: () -> Unit = {},
        onFailed: (Throwable) -> Unit = {},
    ) {
        val current = linkDetailState.value ?: run {
            onFailed(IllegalStateException("링크 상세가 없습니다."))
            return
        }

        if (isDeletingLinkState.value) return

        val userLinkuId = current.userLinkuId ?: run {
            onFailed(IllegalStateException("userLinkuId가 없습니다."))
            return
        }

        val linkuId = current.linkuId

        viewModelScope.launch {
            isDeletingLinkState.value = true

            runCatching {
                linkuRepository.deleteLink(userLinkuId = userLinkuId)
            }.onSuccess {
                linkCache.remove(linkuId)
                linkDetailState.value = null
                aiArticleDetailState.value = null
                onSucceed()
            }.onFailure { e ->
                Log.e("LinkDetailVM", "delete link failed", e)
                onFailed(e)
            }

            isDeletingLinkState.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        aiJob?.cancel()
        aiProgressJob?.cancel()
    }
}