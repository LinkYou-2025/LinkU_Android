package com.linku.data.api

import com.linku.core.error.ApiError
import com.linku.data.api.dto.BaseResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 모든 API 안전 호출의 에러 핸들링을 담당하는 공통 인라인 함수
 */
private inline fun <T> handleApiExceptions(block: () -> Result<T>): Result<T> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiError) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(mapHttpError(e))
    } catch (e: UnknownHostException) {
        Result.failure(ApiError.Network.NoConnection())
    } catch (e: SocketTimeoutException) {
        Result.failure(ApiError.Network.Timeout())
    } catch (e: IOException) {
        Result.failure(ApiError.Network.NoConnection())
    } catch (e: Exception) {
        Result.failure(
            ApiError.Unknown(
                code = "UNKNOWN",
                message = e.message ?: "알 수 없는 오류가 발생했습니다."
            )
        )
    }
}

suspend fun <T> safeApiCall(
    block: suspend () -> BaseResponse<T>
): Result<T> = handleApiExceptions {
    val response = block()
    if (!response.isSuccess) {
        return@handleApiExceptions Result.failure(mapToApiError(response.code, response.message))
    }
    val result = response.result ?: return@handleApiExceptions Result.failure(
        ApiError.Common.InternalServer(code = "COMMON500", message = "결과값이 없습니다.")
    )
    Result.success(result)
}

suspend fun safeApiCallUnit(
    block: suspend () -> BaseResponse<*>
): Result<Unit> = handleApiExceptions {
    val response = block()
    if (!response.isSuccess) {
        return@handleApiExceptions Result.failure(mapToApiError(response.code, response.message))
    }
    Result.success(Unit)
}

suspend fun safeApiCall204(
    block: suspend () -> Response<Unit>
): Result<Unit> = handleApiExceptions {
    val response = block()
    if (response.isSuccessful) {
        Result.success(Unit)
    } else {
        Result.failure(mapHttpError(HttpException(response)))
    }
}