package com.linku.core.usecase

import com.linku.core.model.link.LinkCheckResult
import com.linku.core.repository.LinkuRepository
import javax.inject.Inject

/**
 * 백엔드에서 링크의 유효성과 현재 사용자의 저장 이력을 확인합니다.
 *
 * 링크 형식에 대한 프론트 검사는 호출자가 먼저 수행하며, 이 유스케이스는 검사를 통과한
 * URL만 [LinkuRepository]에 전달합니다. 이미 저장한 링크도 중복 저장이 가능하므로
 * [LinkCheckResult.AlreadySaved]를 오류로 변환하지 않고 그대로 반환합니다.
 *
 * @property linkuRepository 링크 검사 API를 제공하는 저장소
 */
class CheckLinkUseCase @Inject constructor(
    private val linkuRepository: LinkuRepository,
) {
    /**
     * 입력 URL을 백엔드에서 검사합니다.
     *
     * @param url 프론트 유효성 검사를 통과한 URL
     * @return 신규 링크 여부와 저장 이력을 나타내는 [LinkCheckResult]
     */
    suspend operator fun invoke(url: String): LinkCheckResult =
        linkuRepository.checkLink(url)
}
