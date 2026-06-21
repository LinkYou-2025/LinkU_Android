package com.linku.data.api

import com.linku.core.error.ApiError
import com.linku.core.error.AppError
import com.linku.core.error.NetworkError
import com.linku.data.api.dto.BaseResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 서버 API 호출을 안전하게 실행하고, 응답 상태 제어, 널 안전성(Null-Safety) 및 도메인 모델 매핑을 처리하는 공용 함수입니다.
 *
 * 내부적으로 [runCatching] 블록을 활용하며, 서버 응답이 실패하거나 결과 데이터(`result`)가 `null`일 경우
 * 아키텍처 전용 비즈니스 예외를 발생시켜 [onFailure] 영역으로 안전하게 포장하여 전달합니다.
 *
 * @param DTO 서버가 반환하는 원본 데이터 모델 타입 (Data Transfer Object)
 * @param apiCall [BaseResponse]를 반환하는 네트워크 통신 서스펜드 람다 블록
 * @return 가공이 완료된 도메인 객체가 캡슐화된 [Result<Domain>] 구조체
 */
suspend fun <DTO> safeApiCall(
    apiCall: suspend () -> BaseResponse<DTO>
): Result<DTO> {

    //요청 - 네트워크/HTTP 예외는 Result.failure 로 포장 후 apiExceptions() 에서 도메인 에러로 변환
    val response = try {
        apiCall()
    } catch (e: Exception) {
        return Result.failure<DTO>(e).apiExceptions()  // 기존 매핑 로직 재사용
    }

    //에러 처리
    if (!response.isSuccess) {
        return Result.failure<DTO>(
            mapToApiError(response.code, response.message
            )
        ).apiExceptions()
    }

    // 결과값이 없는 경우 처리
    val result = response.result ?: return Result.failure(
        ApiError.Common.InternalServer(message = "결과값이 없습니다.")
    )

    //성공 반환
    return Result.success(result)
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
): Result<Unit> {

    //요청 - 네트워크/HTTP 예외는 Result.failure 로 포장 후 apiExceptions() 에서 도메인 에러로 변환
    val response = try {
        apiCall()
    } catch (e: Exception) {
        return Result.failure<Unit>(e).apiExceptions()
    }

    if (!response.isSuccess) {
        return Result.failure<Unit>(
            mapToApiError(
                response.code,
                response.message
            )
        ).apiExceptions()
    }

    return Result.success(Unit)
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
): Result<Unit> {

    //요청 - 네트워크/HTTP 예외는 Result.failure 로 포장 후 apiExceptions() 에서 도메인 에러로 변환
    val response = try {
        apiCall()
    } catch (e: Exception) {
        return Result.failure<Unit>(e).apiExceptions()
    }

    if (!response.isSuccessful) {
        return Result.failure<Unit>(
            mapHttpError(
                HttpException(response)
            )
        ).apiExceptions()
    }

    return Result.success(Unit)
}

/**
 * 공용 runCatching 내부에서 발생한 수많은 시스템/네트워크 예외들을 링클 아키텍처 전용 [AppError] 계열로 정밀 맵핑하는 헬퍼 확장 함수입니다.
 *
 * 코루틴의 생명 주기를 제어하는 [CancellationException]은 가로채지 않고 상위 스코프로 즉시 재전파(re-throw)하며,
 * 타임아웃, 커넥션 끊김, HTTP 에러 코드를 분석하여 안전하게 [Result.failure] 주머니로 변환해 줍니다.
 */
private fun <Data> Result<Data>.apiExceptions(): Result<Data> {
    return fold(
        onSuccess = { result ->
            Result.success(result)
        },
        onFailure = { exception ->
            val mappedException = when (exception) {
                is CancellationException -> throw exception
                is AppError -> exception
                is SocketTimeoutException -> NetworkError.Timeout()
                is HttpException -> mapHttpError(exception)
                is UnknownHostException, is IOException -> NetworkError.NoConnection()
                else -> ApiError.Unknown(
                    message = exception.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
            Result.failure(mappedException)
        }
    )
}