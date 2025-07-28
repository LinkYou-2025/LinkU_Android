package com.example.core.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://linkuserver.store/"

    fun createRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val noAuth = originalRequest.header("No-Auth") != null // ✅ JWT 없이 보낼지 확인

                val newRequest = if (noAuth) {
                    //  회원가입 관련 API → JWT 제거
                    originalRequest.newBuilder()
                        .removeHeader("No-Auth")
                        .build()
                } else {
                    // 로그인 포함 → JWT 추가
                    val token = getToken(context)
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //  JWT 가져오는 함수
    private fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }
}