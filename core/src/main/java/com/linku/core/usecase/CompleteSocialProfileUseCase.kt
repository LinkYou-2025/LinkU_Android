package com.linku.core.usecase

import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.Purpose
import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 소셜 로그인으로 가입한 신규(TEMP) 회원의 프로필 입력을 완료하는 UseCase입니다.
 *
 * 응답 토큰으로 자동 로그인 세션을 저장하는 일은 레포지토리가 이미 처리합니다.
 *
 * @property authRepository 프로필 완료 API를 호출하는 레포지토리
 */
class CompleteSocialProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 소셜 회원의 프로필을 완료합니다.
     *
     * @param socialToken 소셜 로그인 응답으로 받은 토큰
     * @param nickName 닉네임
     * @param gender 성별
     * @param job 직업
     * @param purposes 사용 목적 목록
     * @param interests 관심사 목록
     * @param termsMap 약관 동의 여부. 키는 약관 종류입니다.
     * @param loginType 인증에 사용한 소셜 공급자
     * @return 프로필 완료에 성공했으면 `true`를 담은 결과
     */
    suspend operator fun invoke(
        socialToken: String,
        nickName: String,
        gender: Gender,
        job: Job,
        purposes: List<Purpose>,
        interests: List<Interest>,
        termsMap: Map<String, Boolean>,
        loginType: LoginType,
    ): Result<Boolean> = authRepository.completeSocialProfile(
        socialToken = socialToken,
        nickName = nickName,
        gender = gender,
        job = job,
        purposes = purposes,
        interests = interests,
        termsMap = termsMap,
        loginType = loginType,
    )
}
