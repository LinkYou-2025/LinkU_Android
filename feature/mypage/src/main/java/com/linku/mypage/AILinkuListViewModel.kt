package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.core.usecase.GetAiArticleLinksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * AI 요약이 생성된 링크를 선택한 카테고리 기준으로 제공합니다.
 *
 * `null` 카테고리는 미선택 상태가 아니라 "전체" 필터를 나타냅니다. 따라서 화면에서
 * [aiArticleLinks]를 처음 수집하는 즉시 전체 목록 요청이 시작됩니다.
 *
 * @property getAiArticleLinksUseCase 카테고리별 AI 요약 링크 목록 조회 유스케이스
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AILinkuListViewModel @Inject constructor(
    private val getAiArticleLinksUseCase: GetAiArticleLinksUseCase,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<CategoryType?>(null)

    /** 현재 선택한 카테고리이며, `null`이면 전체 목록을 조회합니다. */
    val selectedCategory: StateFlow<CategoryType?> = _selectedCategory.asStateFlow()

    /**
     * 현재 필터의 AI 요약 링크 목록입니다.
     *
     * 필터가 바뀌면 [flatMapLatest]가 이전 Pager 수집을 취소하고 최신 필터 요청만 유지합니다.
     * 생성된 Paging 데이터는 ViewModel 생명주기 동안 캐싱합니다.
     */
    val aiArticleLinks: Flow<PagingData<AiArticleLink>> =
        _selectedCategory
            .flatMapLatest(getAiArticleLinksUseCase::invoke)
            .cachedIn(viewModelScope)

    /** 선택한 [category]로 목록 조회 조건을 변경합니다. */
    fun selectCategory(category: CategoryType) {
        _selectedCategory.value = category
    }

    /** 목록 조회 조건을 전체 카테고리로 되돌립니다. */
    fun selectAll() {
        _selectedCategory.value = null
    }
}
