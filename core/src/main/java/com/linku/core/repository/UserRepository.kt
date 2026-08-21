package com.linku.core.repository

import com.linku.core.model.Nickname
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.RecoverResult
import kotlinx.coroutines.flow.StateFlow


interface UserRepository {

    // 가장 최근에 성공한 getUserInfo() 응답을 담아두는 인메모리 캐시.
    // 로그인 직후 미리 채워두면 마이페이지 진입 시 API 응답을 기다리지 않고 헤더를 바로 그릴 수 있음.
    val cachedUserInfo: StateFlow<UserInfo?>

    suspend fun deleteUser(reason: String): Result<Unit>

    // 회원 탈퇴 복구 (탈퇴 유예기간 14일 이내 재로그인 시). 성공(서버 호출 성공)했을 때만 true.
    suspend fun recoverUser(): Result<RecoverResult>
//
//    // 닉네임 전용 메서드 추가
//    suspend fun getNickname(userId: Long): String?

    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): Result<UserInfo>

    // 마이페이지 계정 수정
    suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Result<Unit>

    // 로그아웃. 성공(서버 호출 + 로컬 세션 정리)했을 때만 Success.
    suspend fun logout(): Result<Unit>

    // 닉네임만 호출
    suspend fun getNickname(): Result<Nickname>

    suspend fun updateMarketingTerms(): Result<Unit>

    suspend fun checkMarketingTermsAgreed(): Result<Boolean>

}