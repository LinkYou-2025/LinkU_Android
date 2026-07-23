package com.linku.core.repository

import com.linku.core.model.UserInfo


interface UserRepository {

    suspend fun deleteUser(reason: String): Boolean

    // 회원 탈퇴 복구 (탈퇴 유예기간 14일 이내 재로그인 시). 성공(서버 호출 성공)했을 때만 true.
    suspend fun recoverUser(): Boolean
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

    // 로그아웃. 성공(서버 호출 + 로컬 세션 정리)했을 때만 true.
    suspend fun logout(): Boolean

    // 닉네임만 호출
    suspend fun getNickname(): String?

}