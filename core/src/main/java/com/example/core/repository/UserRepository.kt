package com.example.core.repository

import com.example.core.model.LoginResult
import com.example.core.model.TokenReissueResult
import com.example.core.model.UserInfo


// 목적 (Purposes)
private val purposeMap = mapOf(
    "자기개발" to "SELF_DEVELOPMENT",
    "사이드 프로젝트/창업준비" to "SIDE_PROJECT",
    "기타" to "OTHERS",
    "그냥 나중에 읽고 싶은 글 저장" to "LATER_READING",
    "취업 커리어 준비" to "CAREER",
    "블로그/콘텐츠 작성 참고용" to "CREATION_REFERENCE",
    "인사이트 모으기" to "INSIGHTS",
    "업무자료 아카이빙" to "WORK"
)

// 관심 분야 (Interests)
private val interestMap = mapOf(
    "비즈니스/마케팅" to "BUSINESS",
    "학업/리포트" to "STUDY",
    "커리어/채용" to "CAREER",
    "심리/자기개발" to "PSYCHOLOGY",
    "디자인/크리에이티브" to "DESIGN",
    "it 개발" to "IT",
    "글쓰기/콘텐츠 작성" to "WRITING",
    "시사/트렌드" to "CURRENT_EVENTS",
    "스타트업/창업" to "STARTUP",
    "그냥 모아두고 싶은 글들" to "COLLECT",
    "사회/문화/환경" to "SOCIETY",
    "책/인 사이트 요약" to "INSIGHTS"
)

interface UserRepository {
    suspend fun checkNickname(nickname: String): Boolean
    //suspend fun getNickname(userId: Long): String?
    suspend fun login(email: String, password: String): LoginResult
    suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<String>,
        interestList: List<String>
    ): Boolean //api 명세서 : 회원가입 맞춰서 수정!

    suspend fun sendEmailCode(email: String, code: String): Boolean
    suspend fun verifyEmailCode(email: String, code: String): Boolean
    suspend fun deleteUser(reason: String): Boolean

    // 닉네임 전용 메서드 추가
    suspend fun getNickname(userId: Long): String?


    suspend fun reissue(refreshToken: String): TokenReissueResult
    //유저 비밀번호 재설정
    suspend fun requestTempPassword(email: String): Boolean

    // 마이페이지 조회
    suspend fun getUserInfo(userId: Long): UserInfo

    // 마이페이지 계정 수정
    suspend fun updateUserInfo(
        nickname: String,
        jobId: Long,
        purposes: List<String>,
        interests: List<String>
    ): Boolean

    // 로그아웃
    suspend fun logout()
}