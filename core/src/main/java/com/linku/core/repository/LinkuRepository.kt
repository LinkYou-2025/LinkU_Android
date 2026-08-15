package com.linku.core.repository

import androidx.paging.PagingData
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.RecommendationPage
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.search.LinkuSearchInfo
import kotlinx.coroutines.flow.Flow

interface LinkuRepository {
    // 링크 저장
    suspend fun saveNewLink(
        image: TempImageFile?,
        url: String,
        title: String?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
    ): LinkSimpleInfo

    // 링크 체크
    suspend fun checkLink(url: String): LinkCheckResult

    // 링크 추천
    suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        cursor: String? = null,
        size: Int = 5,
    ): RecommendationPage

    // 최근 조회 링크 조회(10개)
    suspend fun getRecentLinks(limit: Int = 10): List<LinkSimpleInfo>

    // 링크 상세 보기
    suspend fun getLinkDetail(userLinkuId: Long): LinkResultInfo

    /**
     * 사용자가 저장한 링크에서 전달된 필드만 수정합니다.
     *
     * nullable 변경값의 `null`은 해당 필드를 변경하지 않는다는 의미입니다. 메모는
     * 빈 문자열을 전달하여 기존 값을 삭제할 수 있으므로 `null`과 빈 문자열을 구분합니다.
     *
     * @param userLinkuId 수정할 사용자 저장 링크 ID
     * @param image 새로 등록할 이미지. `null`이면 이미지를 변경하지 않습니다.
     * @param memo 변경할 메모. `null`이면 변경하지 않고, 빈 문자열이면 기존 메모를 삭제합니다.
     * @param emotionId 변경할 감정 ID. `null`이면 변경하지 않습니다.
     * @param situationId 변경할 상황 ID. `null`이면 변경하지 않습니다.
     * @param categoryId 변경할 카테고리 ID. `null`이면 변경하지 않습니다.
     * @param title 변경할 제목. `null`이면 변경하지 않습니다.
     * @return 수정된 링크 상세 정보
     */
    suspend fun updateLink(
        userLinkuId: Long,
        image: TempImageFile?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
        categoryId: Long?,
        title: String?,
    ): LinkResultInfo

    // 링크 삭제
    suspend fun deleteLink(userLinkuId: Long)

    // 링크 검색
    fun searchLinks(searchQuery: String): Flow<PagingData<LinkuSearchInfo>>
}
