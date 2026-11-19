package com.linku.core.preference

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

    /**
     * 로그인이 완료됐을 때 세션 정보를 한 번의 트랜잭션으로 저장합니다.
     * 토큰, 사용자 ID, 로그인 수단, 가입 연도가 일부만 저장되는 일은 없습니다.
     *
     * @param accessToken 서버가 발급한 액세스 토큰
     * @param refreshToken 서버가 발급한 리프레시 토큰. null이면 기존 값을 유지함
     * @param userId 로그인한 사용자 ID
     * @param loginType 로그인 수단(이메일, 카카오, 구글)
     * @param joinedYear 가입 연도. null이면 기존 값을 유지함
     */
    suspend fun saveLoginSession(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        loginType: LoginType,
        joinedYear: Int? = null,
    )

    /**
     * 사용자의 가입 연도를 저장합니다.
     *
     * @param joinedYear 저장할 가입 연도
     */
    suspend fun saveJoinedYear(joinedYear: Int)

    /**
     * 로그인 수단을 업데이트합니다.
     * 주로 소셜 가입(TEMP) 유저가 추가 정보를 입력하여 가입을 최종 완료했을 때 사용합니다.
     *
     * @param loginType 업데이트할 로그인 수단
     */
    suspend fun updateLoginType(loginType: LoginType)

    suspend fun updateAccessToken(
        accessToken: String,
        refreshToken: String?
    )

    suspend fun clear()

    suspend fun getCachedNickname(): String?
    suspend fun saveNickname(nickname: String)

    /**
     * 전달된 링크가 현재 사용자의 마지막 배너 노출 클립보드 항목과 같은지 확인합니다.
     *
     * @param url 클립보드에서 읽은 URL
     * @param copiedAtMillis 해당 `ClipData`가 전역 클립보드에 복사된 시각
     * @param userId 기록을 조회할 사용자 ID
     * @return 마지막으로 노출한 클립보드 항목과 URL 및 복사 시각이 모두 같으면 `true`
     */
    suspend fun wasClipboardLinkPresented(
        url: String,
        copiedAtMillis: Long,
        userId: Long,
    ): Boolean

    /**
     * 홈 화면에서 노출할 클립보드 항목을 사용자별로 저장합니다.
     *
     * @param url 프론트 및 백엔드 유효성 검사를 모두 통과한 URL
     * @param copiedAtMillis 해당 `ClipData`가 전역 클립보드에 복사된 시각
     * @param userId URL을 노출한 사용자 ID
     * @return 현재 로그인 사용자가 [userId]와 같아 저장했으면 `true`
     */
    suspend fun savePresentedClipboardLink(
        url: String,
        copiedAtMillis: Long,
        userId: Long,
    ): Boolean

    /**
     * 해당 클립보드 링크가 복사된 뒤 같은 URL을 저장했는지 확인합니다.
     *
     * @param url 클립보드에서 읽은 URL
     * @param copiedAtMillis 해당 `ClipData`가 전역 클립보드에 복사된 시각
     * @param userId 기록을 조회할 사용자 ID
     * @return 동일 URL의 저장 완료 시각이 복사 시각과 같거나 늦으면 `true`
     */
    suspend fun wasLinkSavedAfterClipboardCopy(
        url: String,
        copiedAtMillis: Long,
        userId: Long,
    ): Boolean

    /**
     * 링크 저장 성공 시 해당 URL과 저장 완료 시각을 현재 사용자에게 귀속해 저장합니다.
     *
     * @param url 저장에 성공한 URL
     * @param savedAtMillis 링크 저장이 완료된 시각
     * @param userId 링크를 저장한 사용자 ID
     * @return 현재 로그인 사용자가 [userId]와 같아 저장했으면 `true`
     */
    suspend fun saveLastSavedLinkUrl(
        url: String,
        savedAtMillis: Long,
        userId: Long,
    ): Boolean

    suspend fun getJoinedYear(): Int?

}


