package com.example.login.di


import com.example.login.auth.AuthApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://linkuserver.store"
    //private const val BASE_URL = "http://10.0.2.2:8080" //서버 수정 중으로 부득이하게 로컬로 했었음.

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱
        .client(okHttpClient)
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}