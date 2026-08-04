package com.linku.core.usecase

import com.linku.core.model.curation.SectionItem
import com.linku.core.repository.CurationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 메인 큐레이션 데이터를 가져오는 유스케이스.
 *
 * [CurationRepository]에서 큐레이션 섹션과 최신 큐레이션 정보를 동시에 조회해
 * [CurationMain] 객체를 구성한다.
 *
 * @property curationRepository 큐레이션 관련 데이터를 조회하는 데 사용하는 리포지토리.
 */
class CurationMainUseCase @Inject constructor(
    private val curationRepository: CurationRepository
) {
    suspend operator fun invoke(): Result<CurationMain> = runCatching {
        coroutineScope {
            val sectionsDeferred = async { curationRepository.getSections() }
            val latestCurationDeferred = async { curationRepository.getLatestCuration() }

            val sections = sectionsDeferred.await().getOrThrow()
            val latestCuration = latestCurationDeferred.await().getOrThrow()

            CurationMain(
                sections = sections,
                latestCurationId = latestCuration.curationId,
                latestCurationMonth = latestCuration.month
            )
        }
    }.onFailure {
        if (it is CancellationException) throw it
    }
}

/**
 * 큐레이션 콘텐츠의 최상위 데이터 구조.
 *
 * @property sections 큐레이션 화면을 구성하는 [com.linku.core.model.curation.SectionItem] 목록.
 * @property latestCurationId 가장 최근 큐레이션의 고유 식별자.
 * @property latestCurationMonth 가장 최근 큐레이션의 월을 나타내는 표시용 문자열.
 */
data class CurationMain(
    val sections: List<SectionItem>,
    val latestCurationId: Long,
    val latestCurationMonth: String,
)
