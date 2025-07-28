package com.example.linku_android.di

import com.example.core.api.LoginApi
import com.example.core.domain.LoginRepository
import com.example.data.repository.LoginRepositoryImpl
import com.example.core.network.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    /**
     *  Retrofit(LoginApi) 제공
     * NetworkModule의 createRetrofit() 사용
     */
    @Provides
    @Singleton
    fun provideLoginApi(): LoginApi {
        // 🔥 여기서 tokenProvider는 실제 토큰을 가져오는 함수로 교체 필요
        val retrofit: Retrofit = NetworkModule.createRetrofit()
        return retrofit.create(LoginApi::class.java)
    }

    /**
     * LoginRepositoryImpl을 LoginRepository 타입으로 제공
     */
    @Provides
    @Singleton
    fun provideLoginRepository(api: LoginApi): LoginRepository {
        return LoginRepositoryImpl(api)
    }
}