package com.example.data.api


import com.example.core.error.TokenExpiredException
import com.example.core.model.auth.LoginErrorType
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.RefreshTokenRequest
import com.example.data.preference.AuthPreference
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

// 추후 기능 확장용 - 네트웤, 서버 오류시 모달창에 활용 -> 기능 확장용
sealed class ApiError : Exception() {

    // 인증 관련
    data class Unauthorized(
        override val message: String = "인증이 필요합니다"
    ) : ApiError()

    data class TokenExpired(
        override val message: String = "로그인이 만료되었습니다"
    ) : ApiError()

    data class Forbidden(
        override val message: String = "접근 권한이 없습니다"
    ) : ApiError()

    // 클라이언트 에러
    data class NotFound(
        override val message: String = "요청한 데이터를 찾을 수 없습니다"
    ) : ApiError()

    data class BadRequest(
        val code: Int,
        override val message: String
    ) : ApiError()

    // 서버 에러
    data class ServerError(
        val code: Int,
        override val message: String = "서버 오류가 발생했습니다"
    ) : ApiError()

    // 네트워크 에러
    data class NetworkError(
        override val message: String = "네트워크 연결을 확인해주세요"
    ) : ApiError()

    // 비즈니스 로직 에러
    data class BusinessError(
        val errorCode: String?,
        override val message: String
    ) : ApiError()
}

// TODO : 나중에 뷰모델에서 에러 문구 띄우기, 밑에는 활용 예시.
/*viewModelScope.launch {
    try {
        val result = serverApi.withAuth(authPreference) { getUserProfile() }
        _state.value = Success(result)
    } catch (e: ApiError) {
        // 모달창으로 보여주기
        when (e) {
            is ApiError.TokenExpired -> _errorEvent.emit(ErrorType.LOGIN_REQUIRED)
            is ApiError.NetworkError -> _errorEvent.emit(ErrorType.NETWORK)
            is ApiError.ServerError -> _errorEvent.emit(ErrorType.SERVER)
            is ApiError.BusinessError -> _errorEvent.emit(ErrorType.BUSINESS(e.message))
            else -> _errorEvent.emit(ErrorType.UNKNOWN)
        }
    }
}
*/

// 2. 에러 변환 유틸

/**
 * HttpException → ApiError 변환
 */
private fun HttpException.toApiError(): ApiError = when (code()) {
    400 -> ApiError.BadRequest(code(), message())
    401 -> ApiError.Unauthorized()
    403 -> ApiError.Forbidden()
    404 -> ApiError.NotFound()
    in 500..599 -> ApiError.ServerError(code(), message())
    else -> ApiError.BadRequest(code(), message())
}

/**
 * 일반 Exception → ApiError 변환
 * TODO : 다인 언니로부터 멘트 확정시 수정하기.
 */
private fun Exception.toApiError(): ApiError = when (this) {
    is ApiError -> this
    is HttpException -> toApiError()
    is TokenExpiredException -> ApiError.TokenExpired(message ?: "토큰이 만료되었습니다")
    is UnknownHostException -> ApiError.NetworkError()
    is SocketTimeoutException -> ApiError.NetworkError("연결 시간이 초과되었습니다")
    is IOException -> ApiError.NetworkError()
    else -> ApiError.ServerError(0, message ?: "알 수 없는 오류가 발생했습니다")
}

/**
 * 401 여부 판단 (refresh 필요 여부)
 */
private fun shouldRefreshToken(e: Exception): Boolean = when (e) {
    is HttpException -> e.code() == 401
    is TokenExpiredException -> true
    is ApiError.Unauthorized -> true
    is ApiError.TokenExpired -> true
    else -> false
}


// 3. 토큰 갱신 로직

private val refreshMutex = Mutex()

private suspend fun ServerApi.refreshTokenIfNeeded(
    authPreference: AuthPreference
) = refreshMutex.withLock {
    val refresh = authPreference.refreshToken
        ?: throw ApiError.TokenExpired("다시 로그인해주세요")

    val pair = withCheck { refreshToken(RefreshTokenRequest(refresh)) }
    pair.refreshToken?.let { authPreference.refreshToken = it }
    pair.accessToken?.let { authPreference.accessToken = it }
}

/**
 * 토큰 갱신 + 1회 재시도 + 에러 변환
 */
private suspend fun <T> ServerApi.withTokenRefresh(
    authPreference: AuthPreference,
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        if (shouldRefreshToken(e)) {
            try {
                refreshTokenIfNeeded(authPreference)
                block() // 1회 재시도
            } catch (refreshError: Exception) {
                throw refreshError.toApiError()
            }
        } else {
            throw e.toApiError()
        }
    }
}

/**
 * BaseResponse 검증 - isSuccess 확인 후 result 반환
 * 내부용. isSuccess 확인 + result 꺼내기
 */
suspend fun <T> ServerApi.withCheck(
    getter: suspend ServerApi.() -> BaseResponse<T>
): T {
    val response = getter()
    if (!response.isSuccess) {
        throw ApiError.BusinessError(
            errorCode = response.code,
            message = response.message ?: "요청 처리 중 오류가 발생했습니다"
        )
    }
    return response.result ?: throw ApiError.ServerError(0, "결과값이 없습니다")
}
/**
 * 인증 불필요 + BaseResponse<T> 반환
 * 토큰 없는 API (로그인, 회원가입 등)
 */
//UserRepositoryImpl.kt 에서 사용함.
suspend fun <T> ServerApi.withErrorHandling(
    block: suspend ServerApi.() -> BaseResponse<T>
): T {
    return try {
        withCheck { block() }
    } catch (e: Exception) {
        throw e.toApiError()
    }
}

/**
 * 인증 불필요 + 임의 타입 반환 (BaseResponse 아닌 경우)
 * 예: ApiResponseString, Unit 등
 * 토큰 없는 API인데 BaseResponse 형식이 아닐 때
 */
suspend fun <T> ServerApi.withErrorHandlingRaw(
    block: suspend ServerApi.() -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        throw e.toApiError()
    }
}



/**
 * 기본 인증 API 호출 - BaseResponse
 * 대부분일 것 같은데
 */
suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> BaseResponse<T>,
): T = withTokenRefresh(authPreference) {
    withCheck { routine() }
}

/**
 * 인증 필요 + 임의 타입 반환 - baseResponse 아닌 경우.
 * 토큰 필요한데 BaseResponse 형식이 아닐 때
 * */
suspend fun <T> ServerApi.withAuthRaw(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> T
): T = withTokenRefresh(authPreference) {
    routine()
}

/**
 * 인증 필요 + Unit 반환 (logout 등)
 * 토큰 필요한데 반환값 없을 때 (로그아웃 등)
 */
suspend fun ServerApi.withAuthUnit(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> Unit
) {
    withTokenRefresh(authPreference) {
        routine()
    }
}



/**
 * 204 No Content 허용 버전
 * 토큰 필요한데 204 응답 올 때
 */
suspend fun <T> ServerApi.withAuthResp204Raw(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> Response<T>
): T? {
    suspend fun execute(): T? {
        val response = routine()
        return when {
            response.code() == 204 -> null
            response.isSuccessful -> response.body() ?: throw ApiError.ServerError(
                code = response.code(),
                message = "Empty response body"
            )
            else -> throw HttpException(response)
        }
    }

    return withTokenRefresh(authPreference) { execute() }
}

// 로그인 오류 타입 명시
fun Exception.toLoginErrorType(): LoginErrorType = when (this) {
    is ApiError.Unauthorized,
    is ApiError.BusinessError -> LoginErrorType.INVALID_CREDENTIALS
    is ApiError.NetworkError  -> LoginErrorType.NETWORK_ERROR
    is ApiError.ServerError   -> LoginErrorType.SERVER_ERROR
    is ApiError.TokenExpired  -> LoginErrorType.SERVER_ERROR
    is HttpException -> when (code()) {
        401, 403     -> LoginErrorType.INVALID_CREDENTIALS
        in 500..599  -> LoginErrorType.SERVER_ERROR
        else         -> LoginErrorType.UNKNOWN_ERROR
    }
    is IOException -> LoginErrorType.NETWORK_ERROR
    else           -> LoginErrorType.UNKNOWN_ERROR
}



