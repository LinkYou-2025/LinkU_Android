package com.linku.search

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.top.search.SearchBarTopSheet

/**
 * 하단 내비게이션 바까지 덮으며 화면 전체 위에 뜨는 검색 탑 시트를 그립니다.
 *
 * 홈과 파일 같은 화면은 시트의 상태를 직접 갖지 않고 [SearchViewModel.openSearch]로 열기만 요청하며,
 * 가시성과 검색어, 결과는 모두 [viewModel]이 전담합니다.
 *
 * @param viewModel 검색 상태와 결과를 제공하는 [SearchViewModel]
 * @param onLinkClick 검색 결과의 링크를 눌렀을 때 호출하는 함수. 시트는 먼저 닫힙니다.
 */
@Composable
fun SearchOverlay(
    viewModel: SearchViewModel,
    onLinkClick: (userLinkuId: Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visible by viewModel.visible.collectAsStateWithLifecycle()

    SearchBarTopSheet(
        visible = visible,
        onDismiss = viewModel::dismissSearch,
        onQueryChange = viewModel::search,
        onQueryDelete = viewModel::removeRecentQuery,
        onQueryClear = viewModel::clearRecentQueries,
        onLinkClick = { userLinkuId ->
            viewModel.dismissSearch()
            onLinkClick(userLinkuId)
        },
        searchResults = viewModel.searchResults,
        uiState = uiState,
    )
}

/**
 * 검색 탑 시트가 열려 있을 때 뒤로가기를 가로채 시트만 닫습니다.
 *
 * 내비게이션 호스트의 기본 뒤로가기보다 우선하려면 호스트와 같은 컴포지션에서 호스트보다 뒤에
 * 호출해야 합니다.
 *
 * @param viewModel 검색 시트의 가시성을 제공하는 [SearchViewModel]
 */
@Composable
fun SearchBackHandler(viewModel: SearchViewModel) {
    val visible by viewModel.visible.collectAsStateWithLifecycle()

    BackHandler(enabled = visible) {
        viewModel.dismissSearch()
    }
}
