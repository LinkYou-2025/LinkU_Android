package com.linku.core.usecase

import com.linku.core.model.curation.LinkByKeyWord
import com.linku.core.repository.CurationRepository
import com.linku.core.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 키워드에 해당하는 링크 목록과 사용자 닉네임을 병렬로 조회하는 유스케이스.
 *
 * @property curationRepository 키워드 링크 목록을 조회하는 저장소.
 * @property userRepository 사용자 닉네임을 조회하는 저장소.
 */
class GetKeywordLinksUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(keyword: String): Result<KeywordLinksModel> = runCatching {
        coroutineScope {
            val nicknameDeferred = async { userRepository.getNickname() }
            val linksDeferred = async { curationRepository.getLinksByKeyword(keyword) }

            KeywordLinksModel(
                nickname = nicknameDeferred.await().getOrThrow().nickname,
                links = linksDeferred.await().getOrThrow(),
            )
        }
    }.onFailure {
        if (it is CancellationException) throw it
    }
}

data class KeywordLinksModel(
    val nickname: String,
    val links: List<LinkByKeyWord>,
)
