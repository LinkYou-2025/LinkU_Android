package com.linku.core.usecase

import com.linku.core.preference.AuthPreference
import javax.inject.Inject

/**
 * 로컬에 저장된 인증 정보를 모두 지우는 UseCase입니다.
 *
 * 서버 로그아웃 API는 호출하지 않습니다. 복구 모달에서 "탈퇴 유지"를 선택하거나 모달을 닫았을 때처럼, 로그인
 * 직후 임시로 저장해 둔 복구 전용 토큰이 이후 다른 API 호출에 잘못 붙지 않도록 정리하는 용도입니다.
 *
 * @property authPreference 인증 정보를 저장하고 정리하는 저장소
 */
class ClearAuthSessionUseCase @Inject constructor(
    private val authPreference: AuthPreference,
) {

    /** 저장된 인증 정보를 모두 지웁니다. */
    suspend operator fun invoke() {
        authPreference.clear()
    }
}
