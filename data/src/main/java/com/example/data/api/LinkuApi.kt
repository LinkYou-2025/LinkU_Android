package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.LinkuIsExistDTO
import com.example.data.api.dto.server.LinkuResultDTO
import com.example.data.api.dto.server.LinkuSimpleDTO
import com.example.data.api.dto.server.LinkuUpdateDTO
import com.example.data.api.dto.server.UpdateLinkFolderDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LinkuApi {
    // 링크를 폴더에 저장(링크 생성)
    @Multipart
    @POST("/api/linku")
    suspend fun addLink(
        @Part image: MultipartBody.Part,
        @Part("linku") linku: RequestBody,
        @Part("memo") memo: RequestBody?,
        @Part("emotionId") emotionId: RequestBody?
    ): BaseResponse<LinkuResultDTO>

    // 링크 상세보기
    @GET("/api/linku/{linkuid}")
    suspend fun viewDetailLink(
        @Path("linkuid") linkuid: Long
    ): BaseResponse<LinkuResultDTO>

    // 링크 체크
    @GET("/api/linku/exist")
    suspend fun checkLink(
        @Query("url") url: String
    ): BaseResponse<LinkuIsExistDTO>

    // 링크 수정
    @PATCH("/api/linku/{linkuid}")
    suspend fun updateLink(
        @Path("linkuId") linkuId: Long,
        @Body body: LinkuUpdateDTO
    ): BaseResponse<LinkuResultDTO>

    // 링크의 폴더 바꾸기
    @PATCH("/api/linku/{linkuid}")
    suspend fun updateLinkFolder(
        @Path("linkuid") linkuId: Long,
        @Body body: UpdateLinkFolderDTO
    ): BaseResponse<LinkuResultDTO>

    // 최근 열람한 링크 불러오기
    @GET("/api/linku/recent")
    suspend fun recentLinks(
        @Query("limit") limit: Int = 10
    ) : BaseResponse<List<LinkuSimpleDTO>>

    // 링크 추천
    @GET("/api/linku/recommend")
    suspend fun recommendLink(
        @Query("situationId") situationId: Long,
        @Query("emotionId") emotionId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5
    ) : BaseResponse<LinkuSimpleDTO>

    // 빠른 링크 검색 -> 스웨거에 추가되는대로 업뎃 예정
//    @GET("/api/search/quick")
}