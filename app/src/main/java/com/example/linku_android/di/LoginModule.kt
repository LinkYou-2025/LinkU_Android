package com.example.linku_android.di

import com.example.core.api.LoginApi
import com.example.core.domain.LoginRepository
import com.example.data.repository.LoginRepositoryImpl
import com.example.core.network.NetworkModule
import com.example.core.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginApi(
        tokenManager: TokenManager   //  Hilt가 자동으로 주입 -> 07.29 수정.
    ): LoginApi {
        val retrofit: Retrofit = NetworkModule.createRetrofit(tokenManager)
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(api: LoginApi): LoginRepository {
        return LoginRepositoryImpl(api)
    }
}
//import android.content.Context
//import com.example.core.api.LoginApi
//import com.example.core.domain.LoginRepository
//import com.example.data.repository.LoginRepositoryImpl
//import com.example.core.network.NetworkModule
//import com.example.core.utils.TokenManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import retrofit2.Retrofit
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object LoginModule {
//
//    /**
//     * ✅ Retrofit(LoginApi) 제공
//     * - NetworkModule.createRetrofit() 사용
//     * - TokenManager를 활용하여 JWT 인증 관리
//     */
//    @Provides
//    @Singleton
//    fun provideLoginApi(
//        @ApplicationContext context: Context,
//        tokenManager: TokenManager
//    ): LoginApi {
//        val retrofit: Retrofit = NetworkModule.createRetrofit(context, tokenManager)
//        return retrofit.create(LoginApi::class.java)
//    }
//
//    /**
//     * ✅ LoginRepositoryImpl을 LoginRepository 타입으로 제공
//     */
//    @Provides
//    @Singleton
//    fun provideLoginRepository(api: LoginApi): LoginRepository {
//        return LoginRepositoryImpl(api)
//    }
//}