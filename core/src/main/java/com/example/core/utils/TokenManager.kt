package com.example.core.utils

import android.content.Context

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_JWT = "jwt_token"

    //  JWT 저장
    fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_JWT, token).apply()
    }

    //  JWT 불러오기
    fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_JWT, "") ?: ""
    }
}