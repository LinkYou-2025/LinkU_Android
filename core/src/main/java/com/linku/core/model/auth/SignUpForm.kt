package com.linku.core.model.auth

// 모든 회원가입 입력 데이터를 담는 데이터 클래스
data class SignUpForm(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val gender: Gender = Gender.NONE,
    val jobId: Int = 0,
    val purposeList: List<Purpose> = emptyList(),
    val interestList: List<Interest> = emptyList(),
    val agreeTerms: Boolean = false,
    val agreePrivacy: Boolean = false,
    val agreeMarketing: Boolean = false
)
