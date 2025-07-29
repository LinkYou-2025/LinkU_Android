package com.example.core.network

import android.content.Context
import com.example.core.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://linkuserver.store/"

    /**
     * TokenManager(Hilt 싱글톤) 사용 → context 필요 없음
     */
    fun createRetrofit(
        tokenManager: TokenManager
    ): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val noAuth = originalRequest.header("No-Auth") != null

                val newRequest = if (noAuth) {
                    originalRequest.newBuilder()
                        .removeHeader("No-Auth")
                        .build()
                } else {
                    val token = tokenManager.getToken()   // ✅ context 필요 없음
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
}

//import android.content.Context
//import com.example.core.utils.TokenManager
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object NetworkModule {
//
//    private const val BASE_URL = "https://linkuserver.store/"
//
//    /**
//     *  TokenManager 우선 사용, 없으면 기존 SharedPreferences 사용
//     */
//    fun createRetrofit(
//        context: Context,
//        tokenManager: TokenManager? = null
//    ): Retrofit {
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val originalRequest = chain.request()
//                val noAuth = originalRequest.header("No-Auth") != null
//
//                val newRequest = if (noAuth) {
//                    //  JWT 필요 없는 요청
//                    originalRequest.newBuilder()
//                        .removeHeader("No-Auth")
//                        .build()
//                } else {
//                    // JWT 가져오기 (TokenManager가 있으면 사용, 없으면 Prefs)
//                    val token: String = if (tokenManager != null) {
//                        tokenManager.getToken(context)
//                    } else {
//                        getTokenFromPrefs(context)   //  context 확실히 전달
//                    }
//
//                    originalRequest.newBuilder()
//                        .addHeader("Authorization", "Bearer $token")
//                        .build()
//                }
//
//                chain.proceed(newRequest)
//            }
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    /**
//     *  기존 SharedPreferences 방식 유지
//     */
//    private fun getTokenFromPrefs(context: Context): String {
//        val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
//        return sharedPref.getString("jwt_token", "") ?: ""
//    }
//}