package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.LinkItemInfo
import com.linku.data.api.FolderApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.toDomain
import kotlinx.coroutines.CancellationException

/**
 * 한 소분류 폴더에 저장된 링크를 서버의 문자열 커서 순서로 불러오는 [PagingSource]입니다.
 *
 * 첫 요청에는 `null` 커서를 전달합니다. 응답의 다음 커서가 `null`이면 마지막 페이지이며,
 * 공백이거나 현재 커서와 같으면 무한 append를 막기 위해 로드 실패로 처리합니다.
 *
 * @property folderApi 폴더 내부 링크 조회 API
 * @property folderId 링크를 조회할 소분류 폴더 ID
 * @property sort 서버에 전달할 링크 정렬 기준
 * @property includeLinks 폴더 응답에 링크 목록을 포함할지 여부
 */
internal class FolderLinksPagingSource(
    private val folderApi: FolderApi,
    private val folderId: Long,
    private val sort: String = DEFAULT_SORT,
    private val includeLinks: Boolean = true,
) : PagingSource<String, LinkItemInfo>() {

    /** 현재 커서에 해당하는 링크 페이지를 조회하고 다음 커서를 Paging 키로 연결합니다. */
    override suspend fun load(
        params: LoadParams<String>,
    ): LoadResult<String, LinkItemInfo> {
        val cursor = params.key
        val result = safeApiCall {
            folderApi.getLinksFolders(
                folderId = folderId,
                limit = params.loadSize,
                cursor = cursor,
                sort = sort,
                includeLinks = includeLinks,
            )
        }

        return result.fold(
            onSuccess = { response ->
                try {
                    val links = response.links.map { link -> link.toDomain(folderId) }
                    val nextCursor = response.nextCursor

                    when {
                        nextCursor == null -> LoadResult.Page(
                            data = links,
                            prevKey = null,
                            nextKey = null,
                        )
                        nextCursor.isBlank() -> LoadResult.Error(
                            IllegalStateException(MISSING_NEXT_CURSOR_MESSAGE),
                        )
                        nextCursor == cursor -> LoadResult.Error(
                            IllegalStateException(REPEATED_NEXT_CURSOR_MESSAGE),
                        )
                        else -> LoadResult.Page(
                            data = links,
                            prevKey = null,
                            nextKey = nextCursor,
                        )
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    // DTO invariant 위반도 UI가 retry 가능한 Paging 오류로 관찰하도록 경계를 통일합니다.
                    LoadResult.Error(exception)
                }
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            },
        )
    }

    /** 새로고침 시 이전 커서를 복원하지 않고 첫 페이지부터 다시 조회합니다. */
    override fun getRefreshKey(
        state: PagingState<String, LinkItemInfo>,
    ): String? = null

    private companion object {
        const val DEFAULT_SORT = "name"
        const val MISSING_NEXT_CURSOR_MESSAGE =
            "Folder links response nextCursor is blank."
        const val REPEATED_NEXT_CURSOR_MESSAGE =
            "Folder links response repeated the current cursor."
    }
}
