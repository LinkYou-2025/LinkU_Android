package com.linku.data.preference

import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthPreference {

    val isLoggedIn: Flow<Boolean>
    val sessionState: Flow<UserSession>

    suspend fun initDeviceInfo(deviceId: String, deviceType: String)

    suspend fun getDeviceId(): String
    suspend fun getDeviceType(): String

    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserId(): Long?

    suspend fun getLoginType(): LoginType

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        loginType: LoginType
    )

    // 소셜 가입(TEMP) 유저가 나중에 가입을 최종 완료했을 때 수단만 따로 업데이트하기 위함
    suspend fun updateLoginType(loginType: LoginType)

    suspend fun updateAccessToken(
        accessToken: String,
        refreshToken: String?
    )

    suspend fun clear()

    suspend fun getCachedNickname(): String?
    suspend fun saveNickname(nickname: String)

    /**
     * 홈 화면에서 마지막으로 노출한 클립보드 URL을 반환합니다.
     *
     * 같은 클립보드 값이 화면 재진입이나 앱 재실행 후 반복 노출되는 것을 막는 데 사용합니다.
     *
     * @param userId URL을 조회할 사용자 ID
     */
    suspend fun getLastPresentedClipboardUrl(userId: Long): String?

    /**
     * 홈 화면에서 노출한 클립보드 URL을 저장합니다.
     *
     * @param url 프론트 및 백엔드 유효성 검사를 모두 통과한 URL
     * @param userId URL을 노출한 사용자 ID
     * @return 현재 로그인 사용자가 [userId]와 같아 저장했으면 `true`
     */
    suspend fun saveLastPresentedClipboardUrl(url: String, userId: Long): Boolean

    /**
     * 현재 사용자가 마지막으로 저장한 링크 URL을 반환합니다.
     *
     * @param userId URL을 조회할 사용자 ID
     */
    suspend fun getLastSavedLinkUrl(userId: Long): String?

    /**
     * 링크 저장 성공 시 해당 URL을 현재 사용자에게 귀속해 저장합니다.
     *
     * @param url 저장에 성공한 URL
     * @param userId 링크를 저장한 사용자 ID
     * @return 현재 로그인 사용자가 [userId]와 같아 저장했으면 `true`
     */
    suspend fun saveLastSavedLinkUrl(url: String, userId: Long): Boolean

}


