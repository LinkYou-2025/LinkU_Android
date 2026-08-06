package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.folder.LinkuFolderChangeResultDTO
import com.linku.data.api.dto.folder.UpdateLinkFolderDTO
import com.linku.data.api.dto.search.LinkuSearchResponseDTO
import com.linku.data.api.dto.server.GetDetailLinkDTO
import com.linku.data.api.dto.server.LinkCheckDTO
import com.linku.data.api.dto.server.LinkuResultDTO
import com.linku.data.api.dto.server.LinkuSimpleDTO
import com.linku.data.api.dto.server.RecommendLinkPageDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    @POST("linku")
    suspend fun addLink(
        @Part image: MultipartBody.Part?,
        @Part("linku") linku: RequestBody,
        @Part("memo") memo: RequestBody?,
        @Part("emotionId") emotionId: RequestBody?,
        @Part("situationId") situationId: RequestBody?,
        @Part("title") title: RequestBody?,
    ): BaseResponse<LinkuResultDTO>

    // 링크 상세보기
    @GET("linku/{linkuid}")
    suspend fun viewDetailLink(
        @Path("linkuid") linkuid: Long
    ): BaseResponse<LinkuResultDTO>

    @GET("linku/{userId}/{linkuid}")
    suspend fun viewDetailLink(
        @Path("userId") userId: Long,
        @Path("linkuid") linkuid: Long
    ): BaseResponse<LinkuResultDTO>

    // 링크 체크
    @GET("linku/exist")
    suspend fun checkLink(
        @Query("url") url: String
    ): BaseResponse<LinkCheckDTO>

    // 링크 수정
    @Multipart
    @PATCH("linku/{linkuId}")
    suspend fun updateLink(
        @Path("linkuId") linkuId: Long,
        @Query("memo") memo: String?,
        @Query("emotionId") emotionId: Long?,
        @Query("situationId") situationId: Long?,
        @Query("categoryId") categoryId: Long?,
        @Query("title") title: String?,
        @Part image: MultipartBody.Part?,
    ): BaseResponse<LinkuResultDTO>

    /**
     * 사용자가 저장한 링크를 지정한 폴더로 이동합니다.
     *
     * @param linkuId 이동할 링크 ID
     * @param body 이동할 폴더 정보
     * @return 변경된 링크 폴더 정보가 포함된 응답
     */
    @PATCH("linku/{linkuId}/folder")
    suspend fun updateLinkFolder(
        @Path("linkuId") linkuId: Long,
        @Body body: UpdateLinkFolderDTO
    ): BaseResponse<LinkuFolderChangeResultDTO>

    // 최근 열람한 링크 불러오기
    @GET("linku/recent")
    suspend fun recentLinks(
        @Query("limit") limit: Int = 10
    ) : BaseResponse<List<LinkuSimpleDTO>>

    // 링크 추천
    @GET("linku/recommend")
    suspend fun recommendLink(
        @Query("situationId") situationId: Long,
        @Query("emotionId") emotionId: Long,
        @Query("cursor") cursor: String? = null,
        @Query("size") size: Int? = null
    ) : BaseResponse<RecommendLinkPageDTO>

    // 링크 검색
    @GET("linku/search")
    suspend fun searchLinks(
        @Query("searchQuery") searchQuery: String,
        @Query("cursor") cursor: Long = 0,
        @Query("size") size: Int = 10,
    ): BaseResponse<LinkuSearchResponseDTO>

    // 링크 삭제
    @DELETE("linku/{userLinkuId}")
    suspend fun deleteLink(
        @Path("userLinkuId") userLinkuId: Long
    ): Response<Unit>

    // 링크 상세보기(userLinkuId 갖고오기용)
    @GET("linku/{linkuid}")
    suspend fun getDetailLink(
        @Path("linkuid") linkuid: Long
    ): BaseResponse<GetDetailLinkDTO>
}
