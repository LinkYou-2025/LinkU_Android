package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.folder.LinkuFolderChangeResultDTO
import com.linku.data.api.dto.folder.UpdateLinkFolderDTO
import com.linku.data.api.dto.search.LinkuSearchResponseDTO
import com.linku.data.api.dto.server.LinkuIsExistDTO
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
    @GET("linku/{userLinkuId}")
    suspend fun viewDetailLink(
        @Path("userLinkuId") userLinkuId: Long
    ): BaseResponse<LinkuResultDTO>

    // 링크 체크
    @GET("linku/exist")
    suspend fun checkLink(
        @Query("url") url: String
    ): BaseResponse<LinkuIsExistDTO>

    /**
     * 변경된 링크 값을 multipart part로 전달해 수정합니다.
     *
     * 서버가 이미지 유무와 관계없이 `multipart/form-data` 요청만 허용하므로 텍스트와 ID도
     * 모두 part로 전송합니다. 변경되지 않은 값은 `null`로 생략하고, 빈 메모 RequestBody는
     * 기존 메모를 지우는 유효한 part로 유지합니다.
     *
     * @param userLinkuId 수정할 사용자 저장 링크 ID
     * @param memo 변경할 메모. `null`이면 변경하지 않습니다.
     * @param emotionId 변경할 감정 ID. `null`이면 변경하지 않습니다.
     * @param situationId 변경할 상황 ID. `null`이면 변경하지 않습니다.
     * @param categoryId 변경할 카테고리 ID. `null`이면 변경하지 않습니다.
     * @param title 변경할 제목. `null`이면 변경하지 않습니다.
     * @param image 새로 등록할 이미지. `null`이면 기존 이미지를 유지합니다.
     * @return 수정된 링크 상세 정보가 포함된 응답
     */
    @Multipart
    @PATCH("linku/{userLinkuId}")
    suspend fun updateLink(
        @Path("userLinkuId") userLinkuId: Long,
        @Part("memo") memo: RequestBody?,
        @Part("emotionId") emotionId: RequestBody?,
        @Part("situationId") situationId: RequestBody?,
        @Part("categoryId") categoryId: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): BaseResponse<LinkuResultDTO>

    /**
     * 사용자가 저장한 링크를 지정한 폴더로 이동합니다.
     *
     * @param userLinkuId 이동할 사용자 저장 링크 ID
     * @param body 이동할 폴더 정보
     * @return 변경된 링크 폴더 정보가 포함된 응답
     */
    @PATCH("linku/{userLinkuId}/folder")
    suspend fun updateLinkFolder(
        @Path("userLinkuId") userLinkuId: Long,
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

}
