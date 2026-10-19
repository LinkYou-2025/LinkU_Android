package com.linku.core.usecase

import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.SignUpEmailResult
import com.linku.core.repository.AuthRepository
import javax.inject.Inject

/**
 * 이메일 회원가입을 요청하는 UseCase입니다.
 *
 * 응답 토큰으로 자동 로그인 세션을 저장하는 일은 레포지토리가 이미 처리합니다.
 *
 * @property authRepository 회원가입 API를 호출하는 레포지토리
 */
class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    /**
     * 이메일 회원가입을 수행합니다.
     *
     * @param nickname 닉네임
     * @param email 이메일
     * @param password 비밀번호
     * @param gender 성별
     * @param jobId 직업 ID
     * @param purposeList 사용 목적 목록
     * @param interestList 관심사 목록
     * @param termsMap 약관 동의 여부. 키는 약관 종류입니다.
     * @return 회원가입 결과
     */
    suspend operator fun invoke(
        nickname: String,
        email: String,
        password: String,
        gender: Gender,
        jobId: Int,
        purposeList: List<Purpose>,
        interestList: List<Interest>,
        termsMap: Map<String, Boolean>,
    ): Result<SignUpEmailResult> = authRepository.signUpWithEmail(
        nickname = nickname,
        email = email,
        password = password,
        gender = gender,
        jobId = jobId,
        purposeList = purposeList,
        interestList = interestList,
        termsMap = termsMap,
    )
}
