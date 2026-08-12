package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.auth.login.RecoverUserRequestDTO
import com.linku.data.api.dto.user.CheckUserTermsDTO
import com.linku.data.api.dto.user.DeleteUserRequestDTO
import com.linku.data.api.dto.user.DeleteUserResponseDTO
import com.linku.data.api.dto.user.NicknameResponseDTO
import com.linku.data.api.dto.user.RecoverUserResponseDTO
import com.linku.data.api.dto.user.UpdateUserProfileRequestDTO
import com.linku.data.api.dto.user.UserInfoResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    // 닉네임 조회
    @GET("auth/nickname")
    suspend fun checkNickname(
    ): BaseResponse<NicknameResponseDTO>

    // 회원 탈퇴
    @POST("users/inactive")
    suspend fun deleteUser(
        @Body body: DeleteUserRequestDTO
    ): BaseResponse<DeleteUserResponseDTO>

    // 로그아웃 - 요청의 deviceId에 해당하는 현재 디바이스 세션만 로그아웃
    @POST("auth/logout")
    suspend fun logout(
        @Query("deviceId") deviceId: String
    ): BaseResponse<*> // result {}

    // 회원 탈퇴 복구 (계정 활성화) - 탈퇴 유예기간(14일) 내 재로그인 시 호출.
    // 로그인 응답의 accessToken(purpose=RECOVER)이 Authorization 헤더로 자동 첨부됨.
    @POST("users/recover")
    suspend fun recoverUser(
        @Body body: RecoverUserRequestDTO
    ): BaseResponse<RecoverUserResponseDTO>


    // 마이페이지 조회
    @GET("users/me")
    suspend fun getUserInfo(
    ): BaseResponse<UserInfoResponseDTO>

    // 마이페이지 수정
    @PATCH("users/profile")
    suspend fun updateUserInfo(
        @Body body: UpdateUserProfileRequestDTO
    ): BaseResponse<*> // BaseResponse 형태임. 별도 클래스 생성 없음.

    // 마케팅 동의 여부 업데이트 api
    @PATCH("users/terms/marketing/toggle")
    suspend fun updateMarketingTerms(
    ): BaseResponse<*> // BaseResponse 형태임. 별도 클래스 생성 없음.

    @GET("users/terms/status")
    suspend fun checkTermsStatus(
    ): BaseResponse<CheckUserTermsDTO> // BaseResponse 형태임. 별도 클래스 생성 없음.

}