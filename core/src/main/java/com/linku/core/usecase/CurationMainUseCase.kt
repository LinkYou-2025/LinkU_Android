package com.linku.core.usecase

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
