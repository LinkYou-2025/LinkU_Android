package com.linku.data.implementation.preference


import android.content.Context
import android.util.Log
import com.linku.data.preference.AuthPreference

/*
* 아 수정해야함...
* */
class AuthPreferenceImpl(context: Context) : AuthPreference {
    companion object {
        // SharedPreferences 파일 이름
        private const val PREF_NAME = "auth"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        //EncryptedSharedPreferences을 적용을 고민했으나, coderabbitai에 피드백이 있어서 주석 남깁니다.
        // 암호화로 나중에 오류 발생시 찾기 어려워 보이는데,어떻게 할지?
        private const val USER_ID_KEY = "user_id"

        private const val SOCIAL_TOKEN_KEY = "social_token"

        private const val TAG = "AuthPreferenceImpl" //태그 상수 정의
    }
    // 실제 저장소.
    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // 모든 인증 api 요청에 사용하는 토큰, OkHttp NetworkInterceptor 읽고 Authorization 헤더에 붙음.
    override var accessToken: String
        get() = pref.getString(ACCESS_TOKEN_KEY, "") ?: ""
        set(value) {
            pref.edit().apply {
                if (value == null) remove(ACCESS_TOKEN_KEY)
                else putString(ACCESS_TOKEN_KEY, value)
            }.apply()
        }
    // 엑세스 토큰은 수명이 짧음. 재발급에 사용함. 스플래쉬에서 자동 로그인 가능 여부 판단의 기준임.
    // 여기 값이 있음 -> 로그인 상태임 , 값이 없으면 로그인 화면임.
    override var refreshToken: String?
        get() = pref.getString(REFRESH_TOKEN_KEY, null)
            ?.takeIf { it.isNotBlank() }
        set(value) {
            pref.edit().apply {
                //로그아웃 시 리프래쉬 토큰 제거함. -> 자동 로그인 깨짐.
                if (value == null) remove(REFRESH_TOKEN_KEY)
                else putString(REFRESH_TOKEN_KEY, value)
            }.apply()
        }

    override var userId: Long?
        get() {
            // 키 자체가 없으면 → 유저 없음
            if (!pref.contains(USER_ID_KEY)) {
                return null
            }

            // 키가 있으면 값 가져오기
            val value = pref.getLong(USER_ID_KEY, -1L)

            // -1L이면 유효하지 않은 값 → null
            return if (value == -1L) null else value
        }
        set(value) {
            pref.edit().apply {
                if (value == null) {
                    remove(USER_ID_KEY)
                } else {
                    putLong(USER_ID_KEY, value)
                }
            }.apply()
        }

    // 모든 인증 정보 삭제(로그가웃, 회원탈퇴시 사용)
    override fun clear() {
        pref.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(USER_ID_KEY)
            .remove(SOCIAL_TOKEN_KEY)
            .apply()

        Log.d(TAG, "인증 정보 삭제 완료")
    }

    // 토큰 저장(로그인 성공시)
    override fun saveTokens(
        accessToken: String,
        refreshToken: String?, //TODO : 수정하기.
        userId: Long
    ) {
        pref.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .putLong(USER_ID_KEY, userId)
            .apply()

        Log.d(TAG, "토큰 저장 완료")
    }

    // socialToken 구현 추가
    override var socialToken: String?
        get() = pref.getString(SOCIAL_TOKEN_KEY, null)
        set(value) {
            pref.edit().apply {
                if (value == null) remove(SOCIAL_TOKEN_KEY)
                else putString(SOCIAL_TOKEN_KEY, value)
            }.apply()
        }

}