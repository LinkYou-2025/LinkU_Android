package com.example.core.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

//Hilt가 TokenManager를 싱글톤으로 관리하게 해서, 필요할 때 자동으로 주입받을 수 있음.으로 수정.

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPref.edit().putString(KEY_JWT, token).apply()
    }

    fun getToken(): String {
        return sharedPref.getString(KEY_JWT, "") ?: ""
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_JWT = "jwt_token"
    }
}

//import android.content.Context
//
//object TokenManager {
//
//    private const val PREFS_NAME = "auth_prefs"
//    private const val KEY_JWT = "jwt_token"
//
//    //  JWT 저장
//    fun saveToken(context: Context, token: String) {
//        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        sharedPref.edit().putString(KEY_JWT, token).apply()
//    }
//
//    //  JWT 불러오기
//    fun getToken(context: Context): String {
//        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return sharedPref.getString(KEY_JWT, "") ?: ""
//    }
//}