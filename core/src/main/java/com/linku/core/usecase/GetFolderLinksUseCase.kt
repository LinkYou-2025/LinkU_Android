package com.linku.core.usecase

import androidx.paging.PagingData
import com.linku.core.model.LinkItemInfo
import com.linku.core.repository.FolderRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 폴더에 저장된 링크의 커서 페이징 스트림을 제공하는 유스케이스입니다. */
class GetFolderLinksUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    /**
     * 지정한 [folderId]의 저장 링크를 첫 페이지부터 조회합니다.
     *
     * @param folderId 링크를 조회할 소분류 폴더 ID
     * @return 폴더별로 독립된 커서 페이징 스트림
     */
    operator fun invoke(folderId: Long): Flow<PagingData<LinkItemInfo>> =
        repository.getFolderLinks(folderId)
}
