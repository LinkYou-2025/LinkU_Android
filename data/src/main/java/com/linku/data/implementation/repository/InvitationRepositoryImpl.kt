package com.linku.data.implementation.repository

import com.linku.core.model.InvitationInfo
import com.linku.core.repository.InvitationRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.toDomain
import javax.inject.Inject

/**
 * 서버 API를 통해 공유 폴더 초대 정보를 조회하고 초대 수락 요청을 처리하는 저장소 구현체입니다.
 *
 * API 응답 모델은 Repository 경계에서 도메인 모델로 변환하며, [safeApiCall]이 변환한
 * API 및 네트워크 오류는 상위 계층에서 처리할 수 있도록 그대로 전파합니다.
 * 초대 토큰은 공유 폴더 접근에 사용되는 민감 정보이므로 원문을 로그에 남기지 않습니다.
 *
 * @property serverApi 공유 폴더 초대 조회 및 수락 API를 제공하는 서버 인터페이스
 */
class InvitationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi
) : InvitationRepository {

    /**
     * 초대 토큰에 해당하는 공유 폴더 초대 정보를 조회합니다.
     *
     * @param token 서버에서 발급한 공유 폴더 초대 토큰
     * @return API 응답을 변환한 [InvitationInfo] 도메인 모델
     * @throws Throwable API 또는 네트워크 요청이 실패하거나 코루틴 작업이 취소된 경우
     */
    override suspend fun getInvitationInfo(token: String): InvitationInfo {
        // 초대 토큰은 접근 자격 정보이므로 요청 및 오류 로그에 원문을 남기지 않습니다.
        return safeApiCall {
            serverApi.getInvitationInfo(token)
        }.fold(
            // API DTO는 데이터 계층 밖으로 노출하지 않고 Repository에서 도메인 모델로 변환합니다.
            onSuccess = { it.toDomain() },
            onFailure = { throw it }
        )
    }

    /**
     * 초대 토큰을 사용하여 공유 폴더 초대를 수락합니다.
     *
     * @param token 수락할 공유 폴더의 초대 토큰
     * @return 서버가 반환한 수락된 공유 폴더의 식별자
     * @throws Throwable API 또는 네트워크 요청이 실패하거나 코루틴 작업이 취소된 경우
     */
    override suspend fun acceptInvitation(token: String): Long {
        // 초대 토큰은 로그에 남기지 않고 성공 시 서버가 반환한 폴더 식별자만 전달합니다.
        return safeApiCall {
            serverApi.acceptInvitation(token)
        }.fold(
            onSuccess = { it },
            // 변환된 오류는 상위 유스케이스가 결과 상태로 분류하도록 그대로 전파합니다.
            onFailure = { throw it }
        )
    }
}
