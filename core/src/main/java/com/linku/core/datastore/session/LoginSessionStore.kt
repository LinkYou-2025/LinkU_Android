package com.linku.core.datastore.session


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [지현이 사용법 요약]
 *
 * 1. 세션 데이터 읽기 (UI 표시용)
 *    - session.nickname, session.email, session.purposes 등
 *
 * 2. 사용자 정보 수정
 *    - MyPageViewModel.updateUserInfo() 호출하면 끝!
 *    - 서버 API + 세션 업데이트 모두 자동 처리됨
 *
 * 3. 닉네임만 수정할 때
 *    - sessionStore.updateNickname(nickname) 사용 가능
 */
// 파일 최상위에 위치해야 합니다.

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class LoginSessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val LOGGED_IN      = booleanPreferencesKey("logged_in")
        val USER_ID = longPreferencesKey("user_id")
        val USER_NICK      = stringPreferencesKey("user_nickname")
        val USER_EMAIL     = stringPreferencesKey("user_email")
        val USER_GENDER    = stringPreferencesKey("user_gender")
        val USER_JOB_ID = longPreferencesKey("user_job_id")
        val USER_JOB_NAME  = stringPreferencesKey("user_job_name")
        val USER_MY_LINKU = longPreferencesKey("user_my_linku")
        val USER_MY_FOLDER = longPreferencesKey("user_my_folder")
        val USER_MY_AI_LINKU = longPreferencesKey("user_my_ai_linku")
        val USER_PURPOSES = stringPreferencesKey("user_purposes")
        val USER_INTERESTS = stringPreferencesKey("user_interests")

        // 기기 정보 추가
        val DEVICE_ID = stringPreferencesKey("device_id")
        val DEVICE_TYPE = stringPreferencesKey("device_type")
    }

    /** 앱 시작 시 오토로그인 분기용 */
    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.LOGGED_IN] ?: false
        }

    suspend fun saveLogin(
        userId: Long,
        nickname: String,
        email: String,
        gender: String,
        jobId: Long,
        jobName: String,
        myLinku: Long,
        myFolder: Long,
        myAiLinku: Long,
        purposes: List<String>,    // 추가 - 마이페이지 수정.
        interests: List<String>,
    ) {
        context.dataStore.edit { p ->
            p[Keys.LOGGED_IN] = true
            p[Keys.USER_ID] = userId
            p[Keys.USER_NICK] = nickname
            p[Keys.USER_EMAIL] = email
            p[Keys.USER_GENDER] = gender
            p[Keys.USER_JOB_ID] = jobId
            p[Keys.USER_JOB_NAME] = jobName
            p[Keys.USER_MY_LINKU] = myLinku
            p[Keys.USER_MY_FOLDER] = myFolder
            p[Keys.USER_MY_AI_LINKU] = myAiLinku
            p[Keys.USER_PURPOSES] = purposes.joinToString(",")      //추가  - 마이페이지 수정.
            p[Keys.USER_INTERESTS] = interests.joinToString(",")
        }
    }

    // 기기 정보 저장(최초 1회만)
    suspend fun saveDeviceInfoIfAbsent(deviceId: String, deviceType: String) {
        context.dataStore.edit { p ->
            if (p[Keys.DEVICE_ID] == null) {
                p[Keys.DEVICE_ID] = deviceId
            }
            if (p[Keys.DEVICE_TYPE] == null) {
                p[Keys.DEVICE_TYPE] = deviceType
            }
        }
    }

    // 기기 정보 읽기
    val deviceId: Flow<String?> = context.dataStore.data.map { p ->
        p[Keys.DEVICE_ID]
    }

    val deviceType: Flow<String?> = context.dataStore.data.map { p ->
        p[Keys.DEVICE_TYPE]
    }

    suspend fun clear() {
        context.dataStore.edit { p ->
            // 기기 정보는 유지하고 세션 정보만 삭제
            val deviceId = p[Keys.DEVICE_ID]
            val deviceType = p[Keys.DEVICE_TYPE]

            p.clear()

            // 기기 정보 복원
            deviceId?.let { p[Keys.DEVICE_ID] = it }
            deviceType?.let { p[Keys.DEVICE_TYPE] = it }
            p[Keys.LOGGED_IN] = false
        }
    }

    // TODO : 지현이에게 전달
    // 지현이를 위한 실시간 프로필 업데이트 지원
    // 프로필 수정 시 purposes/interests도 업데이트
    suspend fun updateProfile(
        nickname: String,
        jobId: Long,
        jobName: String,
        purposes: List<String>,
        interests: List<String>
    ) {
        context.dataStore.edit { p ->
            p[Keys.USER_NICK] = nickname
            p[Keys.USER_JOB_ID] = jobId
            p[Keys.USER_JOB_NAME] = jobName
            p[Keys.USER_PURPOSES] = purposes.joinToString(",")
            p[Keys.USER_INTERESTS] = interests.joinToString(",")
        }
    }

    /** MyPageViewModel에서  사용방법
     * fun updateUserInfo(nickname: String, jobId: Long, jobName: String) {
     *     viewModelScope.launch {
     *         // 1. 서버 API 호출 (UserRepository)
     *         val isSuccess = userRepository.updateUserInfo(nickname, jobId, ...)
     *
     *         if (isSuccess) {
     *             // 2. 서버 성공 시 세션 스토어만 업데이트 (이것만 하면 UI가 알아서 바뀜!)
     *             sessionStore.updateProfile(nickname, jobId, jobName)
     *         }
     *     }
     * }
     * */

    // 닉네임만 수정할 때 -> TODO : 지현이에게 전달
    suspend fun updateNickname(nickname: String) {
        context.dataStore.edit { p -> p[Keys.USER_NICK] = nickname }
    }

    data class SessionSnapshot(
        val loggedIn: Boolean,
        val userId: Long?,
        val nickname: String?,
        val email: String?,
        val gender: String?,
        val jobId: Long?,
        val jobName: String?,
        val myLinku: Long?,
        val myFolder: Long?,
        val myAiLinku: Long?,
        val purposes: List<String>,    // 추가 - 마이페이지 수정을 위해.
        val interests: List<String>,
    )

    val session: Flow<SessionSnapshot> =
        context.dataStore.data.map { p ->
            SessionSnapshot(
                loggedIn = p[Keys.LOGGED_IN] ?: false,
                userId = p[Keys.USER_ID],
                nickname = p[Keys.USER_NICK],
                email = p[Keys.USER_EMAIL],
                gender = p[Keys.USER_GENDER],
                jobId = p[Keys.USER_JOB_ID],
                jobName = p[Keys.USER_JOB_NAME],
                myLinku = p[Keys.USER_MY_LINKU],
                myFolder = p[Keys.USER_MY_FOLDER],
                myAiLinku = p[Keys.USER_MY_AI_LINKU],
                purposes = p[Keys.USER_PURPOSES]?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
                interests = p[Keys.USER_INTERESTS]?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
            )
        }


}