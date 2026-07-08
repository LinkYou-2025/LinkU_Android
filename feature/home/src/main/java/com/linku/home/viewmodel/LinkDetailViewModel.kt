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
import com.linku.data.util.DomainIdMapper
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
import javax.inject.Inject

@HiltViewModel
class LinkDetailViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val aiArticleRepository: AIArticleRepository,
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

    private val _categoryColorMap = MutableStateFlow<Map<String, CategoryColorStyle>>(emptyMap())
    val categoryColorMap: StateFlow<Map<String, CategoryColorStyle>> = _categoryColorMap.asStateFlow()

    private var categoryLoaded = false

    fun loadCategoryColors(force: Boolean = false) {
        if (!force && categoryLoaded && _categoryColorMap.value.isNotEmpty()) return

        viewModelScope.launch {
            runCatching {
                categoryRepository.getCategoryColor().toCategoryColorStyleMap()
            }.onSuccess { map ->
                _categoryColorMap.value = map
                categoryLoaded = true
            }.onFailure { e ->
                Log.e("LinkDetailVM", "loadCategoryColors failed", e)
            }
        }
    }

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

    fun loadAiArticle(linkuId: Long) {
        if (isLoadingAiArticleState.value) return

        isLoadingAiArticleState.value = true
        _aiProgress.value = 0.1f

        aiProgressJob?.cancel()
        aiProgressJob = viewModelScope.launch {
            val cap = 0.85f

            while (isActive && _aiProgress.value < cap) {
                delay(100)
                _aiProgress.value = (_aiProgress.value + 0.02f).coerceAtMost(cap)
            }
        }

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            runCatching {
                aiArticleRepository.getAiArticle(linkuId)
            }.onSuccess { article ->
                aiArticleDetailState.value = article
            }.onFailure { e ->
                Log.e("LinkDetailVM", "load AI article failed", e)
                aiArticleDetailState.value = null
            }

            aiProgressJob?.cancel()
            _aiProgress.value = 1f
            isLoadingAiArticleState.value = false

            launch {
                delay(300)
                _aiProgress.value = 0f
            }
        }
    }

    fun cancelAiArticleJob() {
        aiJob?.cancel()
        aiProgressJob?.cancel()
        isLoadingAiArticleState.value = false
        _aiProgress.value = 0f
    }

    fun updateLink(
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
        val fixedLinku = current.linku

        val computedDomainId = DomainIdMapper.resolve(
            url = fixedLinku,
            domain = current.domain,
        )

        viewModelScope.launch {
            isUpdatingLinkState.value = true

            runCatching {
                linkuRepository.updateLink(
                    linkuId = fixedLinkuId,
                    categoryId = categoryId ?: current.categoryId ?: 0L,
                    linku = fixedLinku,
                    memo = memo ?: current.memo.orEmpty(),
                    emotionId = emotionId ?: current.emotionId ?: 0L,
                    situationId = situationId ?: current.situationId ?: 0L,
                    domainId = computedDomainId,
                    title = title.ifBlank { current.title },
                )
            }.onSuccess { updated ->
                linkDetailState.value = updated
                linkCache[fixedLinkuId] = Cached(updated)
                onSucceed(updated)
            }.onFailure { e ->
                onFailed(e)
            }

            isUpdatingLinkState.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        aiJob?.cancel()
        aiProgressJob?.cancel()
    }
}