package com.linku.data.api

import com.linku.core.error.ApiError
import com.linku.data.api.dto.BaseResponse
import retrofit2.HttpException
import retrofit2.Response

/**
 * 서버 API 호출을 안전하게 실행하고, 응답 상태 제어, 널 안전성(Null-Safety) 및 도메인 모델 매핑을 처리하는 공용 함수입니다.
 *
 * 내부적으로 [runCatching] 블록을 활용하며, 서버 응답이 실패하거나 결과 데이터(`result`)가 `null`일 경우
 * 아키텍처 전용 비즈니스 예외를 발생시켜 [onFailure] 영역으로 안전하게 포장하여 전달합니다.
 *
 * @param DTO 서버가 반환하는 원본 데이터 모델 타입 (Data Transfer Object)
 * @param Domain 클라이언트의 비즈니스 및 UI 레이어가 소비하는 무결성 도메인 모델 타입
 * @param apiCall [BaseResponse]를 반환하는 네트워크 통신 서스펜드 람다 블록
 * @param transform 성공적으로 파싱된 DTO 알맹이를 도메인 모델로 번역해주는 매퍼 람다 블록
 * @return 가공이 완료된 도메인 객체가 캡슐화된 [Result<Domain>] 구조체
 */
@Deprecated("safeApiCall(transform파라미터 없는 것)으로 교체 예정")
suspend fun <DTO, Domain> safeApiCall(
    apiCall: suspend () -> BaseResponse<DTO>,
    transform: (DTO) -> Domain
): Result<Domain> = runCatching {
    val response = apiCall()

    if (!response.isSuccess) {
        throw mapToApiError(response.code, response.message)
    }

    val result = response.result ?: throw ApiError.Common.InternalServer(
        message = "결과값이 없습니다."
    )
    transform(result)
}

// TODO : 이거로 변경하기
suspend fun <DTO> safeApiCall(
    apiCall: suspend () -> BaseResponse<DTO>
): DTO {
    val response = apiCall()

    if (!response.isSuccess) {
        throw mapToApiError(response.code, response.message)
    }

    response.result ?: throw ApiError.Common.InternalServer("결과값이 없습니다.")

    return response.result
}

/**
 * 반환할 데이터 알맹이가 없는 빈 객체(`{}`) 응답 API를 안전하게 실행하는 공용 함수입니다.
 * * 백엔드 성공 응답 시 별도의 데이터 필드 없이 상태 코드와 성공 여부만 확인하여 [Result<Unit>]을 반환합니다.
 *
 * @param apiCall 성공 유무만 확인하는 와일드카드(`*`) 기반의 [BaseResponse] 서스펜드 람다 블록
 * @return 성공 신호만 캡슐화된 [Result<Unit>] 구조체
 */
suspend fun safeApiCallUnit(
    apiCall: suspend () -> BaseResponse<*>
): Result<Unit> = runCatching {
    val response = apiCall()
    if (!response.isSuccess) {
        throw mapToApiError(response.code, response.message)
    }
}


/**
 * HTTP 응답 본문이 완전히 비어있는 `204 No Content` 계열의 API를 안전하게 실행하는 공용 함수입니다.
 *
 * [BaseResponse] 껍데기조차 없이 Retrofit 고유의 [Response<Unit>] 형태로 내려오는 완전 비어있는 응답을 처리하며,
 * HTTP 상태 코드가 성공(`2xx`)이 아닐 경우 [HttpException]으로 변환하여 예외 체인을 가동합니다.
 *
 * @param apiCall 본문이 비어있는 Retrofit 고유의 [Response<Unit>] 서스펜드 람다 블록
 * @return 성공 신호만 캡슐화된 [Result<Unit>] 구조체
 */
suspend fun safeApiCall204(
    apiCall: suspend () -> Response<Unit>
): Result<Unit> = runCatching {
    val response = apiCall()
    if (!response.isSuccessful) {
        // FIXME : Response -> BaseResponse 하기
        throw Exception(response.message())
    }
}