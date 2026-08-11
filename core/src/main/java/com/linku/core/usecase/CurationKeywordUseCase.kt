package com.linku.core.usecase

import com.linku.core.model.curation.KeyWord
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 큐레이션 2번 카드 상세(#43-1) 화면에 필요한 데이터를 한 번에 조회하는 유스케이스.
 *
 * 닉네임과 같은 직업 유저들의 이번 달 키워드 목록을 병렬로 요청해
 * [KeywordModel]로 조합한다.
 *
 * @property curationRepository 키워드 목록을 조회하는 저장소.
 * @property userRepository 사용자 닉네임을 조회하는 저장소.
 */
class CurationKeywordUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(month: String): Result<KeywordModel> = runCatching {
        coroutineScope {
            val userInfoDeferred = async { userRepository.getUserInfo(0L) }
            val keywordsDeferred = async { curationRepository.getJobTopKeywords(month) }

            val userInfo = userInfoDeferred.await().getOrThrow()

            KeywordModel(
                nickname = userInfo.nickname,
                jobName = userInfo.jobName,
                keywords = keywordsDeferred.await().getOrThrow(),
            )
        }
    }.onFailure {
        if (it is CancellationException) throw it
    }
}

/**
 * 큐레이션 2번 카드 상세 화면(#43-1)에서 사용되는 데이터 모델.
 *
 * 사용자의 닉네임, 직업군 정보 및 해당 직업군에서 유행하는 키워드 목록을 포함한다.
 *
 * @property nickname 사용자의 닉네임.
 * @property jobName 사용자의 직업 이름.
 * @property keywords 해당 직업군 유저들의 이번 달 주요 키워드 리스트.
 */
data class KeywordModel(
    val nickname: String,
    val jobName: String,
    val keywords: List<KeyWord>,
)
