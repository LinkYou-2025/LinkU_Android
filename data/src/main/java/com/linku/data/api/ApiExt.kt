package com.linku.data.api

import com.linku.core.error.ApiError
import com.linku.data.api.dto.BaseResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * result가 포함된 API 호출을 안전하게 실행하고 결과를 반환하는 래퍼 함수.
 *
 * 서버 응답의 [BaseResponse.isSuccess]를 확인하여 성공 시 [BaseResponse.result]를 반환하고,
 * 실패 시 또는 네트워크 오류 발생 시 적절한 [ApiError]로 변환하여 throw한다.
 *
 * @param T 반환될 데이터의 타입
 * @param block 실행할 API 호출 람다
 * @return 서버로부터 받은 결과 데이터 ([BaseResponse.result])
 * @throws ApiError 서버 에러, 데이터 부재(null), 또는 네트워크 관련 에러
 * @throws CancellationException 코루틴 취소 시 발생 (재throw)
 */
suspend fun <T> safeApiCall(
    block: suspend () -> BaseResponse<T>
): T {
    return try {
        val response = block()
        if (!response.isSuccess) {
            throw mapToApiError(
                code = response.code,
                message = response.message
            )
        }
        response.result ?: throw ApiError.Common.InternalServer(
            code = "COMMON500",
            message = "결과값이 없습니다."
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiError) {
        throw e
    } catch (e: HttpException) {
        throw mapHttpError(e)
    } catch (e: UnknownHostException) {
        throw ApiError.Network.NoConnection()
    } catch (e: SocketTimeoutException) {
        throw ApiError.Network.Timeout()
    } catch (e: IOException) {
        throw ApiError.Network.NoConnection()
    } catch (e: Exception) {
        throw ApiError.Unknown(
            code = "UNKNOWN",
            message = e.message ?: "알 수 없는 오류가 발생했습니다."
        )
    }
}

/**
 * result 없는 API 호출 래퍼. (result: {})
 *
 * 서버 응답의 [BaseResponse.isSuccess]만 확인하고,
 * 실패 시 [ApiErrorMapper]를 통해 [ApiError]로 변환해 throw한다.
 *
 * @param block 실행할 API 호출 람다
 * @throws ApiError 서버 에러 또는 네트워크 에러
 * @throws CancellationException 코루틴 취소 시 (재throw)
 */
suspend fun safeApiCallUnit(
    block: suspend () -> BaseResponse<Any>
) {
    try {
        val response = block()
        if (!response.isSuccess) {
            throw mapToApiError(
                code = response.code,
                message = response.message
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiError) {
        throw e
    } catch (e: HttpException) {
        throw mapHttpError(e)
    } catch (e: UnknownHostException) {
        throw ApiError.Network.NoConnection()
    } catch (e: SocketTimeoutException) {
        throw ApiError.Network.Timeout()
    } catch (e: IOException) {
        throw ApiError.Network.NoConnection()
    } catch (e: Exception) {
        throw ApiError.Unknown(
            code = "UNKNOWN",
            message = e.message ?: "알 수 없는 오류가 발생했습니다."
        )
    }
}