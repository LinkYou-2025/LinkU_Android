package com.linku.core.usecase

import com.linku.core.model.curation.MonthlyCurationDetail
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 월간 큐레이션 상세 화면에 필요한 데이터를 한 번에 조회하는 유스케이스입니다.
 *
 * 다음 데이터를 병렬로 요청하여 하나의 [MonthlyCurationDetail]로 조합합니다.
 *
 * - 월간 큐레이션 상세 정보
 * - 사용자 닉네임
 * - 추천 링크 목록
 * - 월별 상위 태그
 *
 * 각 API는 `coroutineScope`와 `async`를 사용하여 동시에 호출되며,
 * 모든 요청이 성공한 경우에만 [MonthlyCurationDetail]을 반환합니다.
 * 하나라도 실패하면 해당 예외를 전파하여 [Result.failure]를 반환합니다.
 *
 * @property curationRepository 월간 큐레이션 관련 데이터를 조회하는 저장소입니다.
 * @property userRepository 사용자 정보를 조회하는 저장소입니다.
 */
class MonthlyCurationDetailedUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        curationId: Long,
        month: String
    ): Result<MonthlyCurationDetail> = runCatching {

        coroutineScope {
            val detailDeferred = async { curationRepository.getCurationDetail(curationId) }
            val nicknameDeferred = async { userRepository.getNickname() }
            val recommendLinksDeferred = async { curationRepository.getRecommendLinks(curationId) }
            val topTagsDeferred = async { curationRepository.getMyTopTags(month) }

            MonthlyCurationDetail(
                detail = detailDeferred.await().getOrThrow(),
                nickname = nicknameDeferred.await(),
                recommendLink = recommendLinksDeferred.await().getOrThrow(),
                topTags = topTagsDeferred.await().getOrThrow()
            )
        }
    }

}