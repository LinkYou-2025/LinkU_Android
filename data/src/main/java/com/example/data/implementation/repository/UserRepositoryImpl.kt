package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LoginResult
import com.example.core.repository.UserRepository
import com.example.data.api.UserApi
import com.example.data.api.dto.server.JoinDTO
import com.example.data.api.dto.server.LoginRequestDTO
import com.example.data.preference.AuthPreference
import com.example.data.api.dto.server.DeleteReasonDTO
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val authPreference: AuthPreference
) : UserRepository {

    override suspend fun checkNickname(nickname: String): Boolean {
        val response = userApi.checkNickDuplication(nickname)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    override suspend fun login(email: String, password: String): LoginResult {
        val response = userApi.signIn(LoginRequestDTO(email, password))
        val result = response.result ?: throw IllegalStateException("로그인 실패: ${response.message}")

        //  accessToken 저장 (if 사용 → 타입 추론 오류 방지)
        val accessToken: String? = result.accessToken
        if (accessToken != null) {
            // AuthPreference에 맞는 실제 메서드명으로 교체 필요
            authPreference.accessToken = accessToken
        }

        return LoginResult(
            userId = result.userId?.toInt() ?: -1,
            token = result.accessToken ?: "",
            status = result.status ?: "",
            inactiveDate = result.inactiveDate?.toString()
        )
    }

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        gender: Int,
        jobId: Int,
        purposeList: List<String>,
        interestList: List<String>
    ): Boolean {
        val dto = JoinDTO(
            nickName = nickname,
            email = email,
            password = password,
            gender = gender,
            jobId = jobId,
            purposeList = purposeList,
            interestList = interestList
        )

        val response = userApi.signUp(dto)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    override suspend fun sendEmailCode(email: String): Boolean {
        val response = userApi.sendVerificationEmail(email)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    override suspend fun verifyEmailCode(email: String, code: String): Boolean {
        val response = userApi.checkVerificationEmail(email, code)
        return response.isSuccess == true   // Boolean? → Boolean 변환
    }

    //inactiveDate 추가.

    override suspend fun deleteUser(reason: String): Boolean {
        val dto = DeleteReasonDTO(reason)
        val response = userApi.deleteUser(dto)
        return response.isSuccess == true
    }

}