package com.linku.mypage.intent

import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose

// 회원 정보 수정
sealed interface EditUserInfoIntent {
    data class UpdateNickname(val nickname: String) : EditUserInfoIntent

    // data class UpdateGender(val gender: Gender) : EditUserInfoIntent // 미래의 혀누씨 꿈을 이룰 수 있게
    data class UpdateJobId(val jobId: Long) : EditUserInfoIntent
    data class UpdatePurpose(val purpose: Purpose) : EditUserInfoIntent
    data class UpdateInterest(val interest: Interest) : EditUserInfoIntent
}

// 비밀번호 변경
//sealed interface ChangePasswordIntent {
//    data class UpdatePassword(val newPassword: String) : ChangePasswordIntent
//}