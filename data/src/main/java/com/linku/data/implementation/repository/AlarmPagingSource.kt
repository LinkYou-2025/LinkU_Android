package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.error.ApiError
import com.linku.core.error.AppError
import com.linku.core.error.NetworkError
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.mapHttpError
import com.linku.data.api.mapToApiError
import com.linku.data.mapper.AlarmMapper.toDomain
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * [AlarmApi]를 통해 [AlarmSummary] 데이터를 페이징하여 가져오기 위한 [PagingSource] 구현체입니다.
 *
 * 특정 [AlarmType]에 대한 커서(Cursor) 기반 페이징을 처리하며, API 응답 데이터를
 * 도메인 모델로 매핑하고 네트워크 및 서버 에러에 대한 예외 처리를 수행합니다.
 *
 * @property alarmApi 알람 데이터를 요청하기 위한 API 인터페이스
 * @property type 조회하고자 하는 알람의 종류
 */
class AlarmPagingSource(
    val alarmApi: AlarmApi,
    val type: AlarmType
) : PagingSource<Long, AlarmSummary>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, AlarmSummary> {

        // safeApiCall을 사용하지 않은 이유:
        // PagingSource는 Result 기반의 공통 API 래퍼(safeApiCall) 대신,
        // Paging3가 요구하는 LoadResult(코틀린 Result와 유사하나 Paging3 라이브러리에서 쓰는 타입입니다)
        // 로 직접 변환하는 책임을 가집니다.
        // 따라서 Result → LoadResult로 이중 래핑하는 구조를 피하고,
        // 여기서 바로 Paging 전용 에러/성공 처리로 통합합니다.
        return try {

            // 응답
            val response = alarmApi.getAlarms(
                alarmType = type.name,
                cursor = params.key,
                size = params.loadSize
            )

            // http응답이 성공이 아닐 시 도메인 apiError로의 매핑 후
            // LoadResult.Error 안에 캡슐화하여 반환
            if (!response.isSuccess) {
                return LoadResult.Error(mapToApiError(response.code, response.message))
            }

            // 성공 시 응답을 도메인 변환 후 LoadResult.Page에 캡슐화해 반환
            val alarmList = response.result.toDomain()
            LoadResult.Page(
                data = alarmList.alarms,
                prevKey = null,
                nextKey = alarmList.nextCursor
            )

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // 호출 자체가 실패했을 때의 에러를 LoadResult.Error안에 캡슐화하여 반환
            LoadResult.Error(
                when (e) {
                    is AppError -> e
                    is SocketTimeoutException -> NetworkError.Timeout()
                    is HttpException -> mapHttpError(e)
                    is UnknownHostException, is IOException -> NetworkError.NoConnection()
                    else -> ApiError.Unknown(message = e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Long, AlarmSummary>): Long? = null
}