package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

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

    /** 삭제가 완료된 링크가 서버 재조회 결과에 남아 있어도 다시 노출하지 않는 세션 오버레이입니다. */
    private val _deletedUserLinkuIds = MutableStateFlow<Set<Long>>(emptySet())

    /**
     * 삭제 오버레이 변경 시 같은 원본 [PagingData]를 다시 변환해도 페이지 이벤트를 안전하게
     * 재수집할 수 있도록 카테고리별 Pager 스트림을 먼저 캐시합니다.
     */
    private val pagedAiArticleLinks: Flow<PagingData<AiArticleLink>> =
        _selectedCategory
            .flatMapLatest(getAiArticleLinksUseCase::invoke)
            .cachedIn(viewModelScope)

    /**
     * 현재 필터의 AI 요약 링크 목록입니다.
     *
     * 필터가 바뀌면 [flatMapLatest]가 이전 Pager 수집을 취소하고 최신 필터 요청만 유지합니다.
     * 삭제 성공으로 기록된 양수 사용자 링크 ID는 현재 목록에서 즉시 제외하며, 이후 다른
     * 카테고리로 전환해 새 페이지를 받아도 다시 노출하지 않습니다. 최종 Paging 데이터는
     * ViewModel 생명주기 동안 캐싱합니다.
     */
    val aiArticleLinks: Flow<PagingData<AiArticleLink>> =
        combine(
            pagedAiArticleLinks,
            _deletedUserLinkuIds,
        ) { pagingData, deletedUserLinkuIds ->
            pagingData.filter { link ->
                val userLinkuId = link.userLinkuId
                userLinkuId == null || userLinkuId !in deletedUserLinkuIds
            }
        }.cachedIn(viewModelScope)

    /** 선택한 [category]로 목록 조회 조건을 변경합니다. */
    fun selectCategory(category: CategoryType) {
        _selectedCategory.value = category
    }

    /** 목록 조회 조건을 전체 카테고리로 되돌립니다. */
    fun selectAll() {
        _selectedCategory.value = null
    }

    /**
     * 서버에서 삭제가 완료된 사용자 저장 링크를 현재와 이후 AI 요약 목록에서 제외합니다.
     *
     * 이 함수는 삭제 API를 호출하지 않습니다. 상위 화면의 삭제 요청이 성공한 뒤 전달된 ID만
     * 오버레이에 기록하며, 상세 조회와 삭제 API에서 사용할 수 없는 `0` 이하 값은 무시합니다.
     * 동일한 ID를 여러 번 전달해도 [Set]에 한 번만 보관하므로 불필요한 중복 상태를 만들지 않습니다.
     *
     * @param userLinkuId 삭제가 완료된 사용자 저장 링크 ID
     */
    fun onLinkDeleted(userLinkuId: Long) {
        if (userLinkuId <= 0L) return

        _deletedUserLinkuIds.update { deletedUserLinkuIds ->
            deletedUserLinkuIds + userLinkuId
        }
    }
}
