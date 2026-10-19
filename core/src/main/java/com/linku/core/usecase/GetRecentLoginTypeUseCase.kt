package com.linku.core.usecase

import com.linku.core.model.auth.LoginType
import com.linku.core.preference.AuthPreference
import javax.inject.Inject

/**
 * 마지막으로 로그인했던 수단을 조회하는 UseCase입니다.
 *
 * 로그인 화면의 "최근 로그인" 말풍선을 표시하는 데 사용합니다.
 *
 * @property authPreference 로그인 수단을 저장하는 저장소
 */
class GetRecentLoginTypeUseCase @Inject constructor(
    private val authPreference: AuthPreference,
) {

    /**
     * 마지막 로그인 수단을 조회합니다.
     *
     * @return 마지막으로 로그인한 수단. 기록이 없으면 [LoginType.NONE]입니다.
     */
    suspend operator fun invoke(): LoginType = authPreference.getLoginType()
}
