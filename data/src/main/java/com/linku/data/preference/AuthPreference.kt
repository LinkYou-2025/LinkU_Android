package com.linku.data.preference

//엑세스 토큰 직접 Retrofit 인터셉터가 아닌, 함수 단위로 현재 사용중임.
/*
* 로그인 /api/users/login은 최초 로그인 전용으로, 이메일 로그인에서 사용함.
* 여기서 엑세스 토큰 + 리프레쉬 토큰 응답을 함.
* 이를 AuthPreference에 저장함.
*
* */


interface AuthPreference {

    val isLoggedIn : Boolean
        get() = !refreshToken.isNullOrBlank() //로그인 상태 확인
    var accessToken: String? // 모든 인증 api 요청에 사용함.
    var refreshToken: String? // 자동로그인/ 엑세스 토큰 재발급의 기준임. 엑세스 토큰은 기간이 짧기에
    var userId: Long? // 사용자 확인용.


    fun clear() //모든 인증 정보 삭제(로그아웃, 회원탈퇴)

    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        userId: Long
    )

}

