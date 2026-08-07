package com.linku.mypage.intent

import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose

// 최상위 인터페이스
sealed interface EditUserInfoIntent {
    data class UpdateNickname(val nickname: String) : EditUserInfoIntent

    // data class UpdateGender(val gender: Gender) : EditUserInfoIntent // 현우의 꿈을 이룰 수 있게
    data class UpdateJobId(val jobId: Long) : EditUserInfoIntent
    data class UpdatePurpose(val purpose: Purpose) : EditUserInfoIntent
    data class UpdateInterest(val interest: Interest) : EditUserInfoIntent
}