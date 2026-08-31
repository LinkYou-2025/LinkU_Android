package com.linku.core.usecase

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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
 *
 */
class MonthlyCurationDetailedUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        curationId: Long,
        month: String
    ): Result<MonthlyCurationDetail> = runCatching {

        /**
         * 지정한 월간 큐레이션의 상세 화면에 필요한 데이터를 조회합니다.
         *
         * [curationId]와 [month]를 기준으로 필요한 API를 병렬 호출한 뒤,
         * 조회된 결과를 하나의 [MonthlyCurationDetail]로 조합하여 반환합니다.
         *
         * @param curationId 조회할 월간 큐레이션의 식별자입니다.
         * @param month 상위 태그 조회에 사용할 월(`yyyy-MM`)입니다.
         * @return 조회 결과를 담은 [MonthlyCurationDetail] 또는 실패 정보를 포함한 [Result]입니다.
         */

        // 태그 조회는 이정달로 사용
        val previousMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"))
            .minusMonths(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM"))

        coroutineScope {
            val detailDeferred = async { curationRepository.getCurationDetail(curationId) }
            val nicknameDeferred = async { userRepository.getNickname() }
            val recommendLinksDeferred = async { curationRepository.getRecommendLinks(curationId) }
            val topTagsDeferred = async { curationRepository.getMyTopTags(previousMonth) }

            MonthlyCurationDetail(
                detail = detailDeferred.await().getOrThrow(),
                nickname = nicknameDeferred.await().getOrNull() ?: "",
                recommendLink = recommendLinksDeferred.await().getOrThrow(),
                topTags = topTagsDeferred.await().getOrThrow()
            )
        }
    }.onFailure{
        // 사용자가 화면을 나가는 등의 이유로 발생하는 CancellationException는
        // 정상 흐름이므로 Result로 감싸지 않고 그대로 전파
        if (it is CancellationException) throw it
    }
}

/**
 * 월간 큐레이션의 상세 정보를 나타내는 데이터 클래스입니다.
 *
 * @property detail 큐레이션의 핵심 메타데이터 및 콘텐츠 정보입니다.
 * @property nickname 유저 닉네임이며, 조회 실패 시 빈 문자열입니다.
 * @property recommendLink 이 월간 큐레이션과 관련된 추천 링크 목록입니다.
 * @property topTags 이 월간 큐레이션의 상위 태그 목록입니다.
 */
data class MonthlyCurationDetail(
    val detail: CurationDetail,
    val nickname: String = "",
    val recommendLink: List<RecommendLink>,
    val topTags: List<MyTopTag>
)