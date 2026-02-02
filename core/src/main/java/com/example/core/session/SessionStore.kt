package com.example.core.session


import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

// 파일 최상위에 위치해야 합니다.
private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val LOGGED_IN      = booleanPreferencesKey("logged_in")
        val USER_ID   = stringPreferencesKey("user_id")
        val USER_NICK      = stringPreferencesKey("user_nickname")
        val USER_EMAIL     = stringPreferencesKey("user_email")
        val USER_GENDER    = stringPreferencesKey("user_gender")
        val USER_JOB_ID    = stringPreferencesKey("user_job_id")
        val USER_JOB_NAME  = stringPreferencesKey("user_job_name")
        val USER_MY_LINKU  = stringPreferencesKey("user_my_linku")
        val USER_MY_FOLDER = stringPreferencesKey("user_my_folder")
        val USER_MY_AI_LINKU = stringPreferencesKey("user_my_ai_linku")
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { p -> p[Keys.LOGGED_IN] = value }
    }

    /** 앱 시작 시 오토로그인 분기용 */
    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences ->
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
    ) {
        context.dataStore.edit { p ->
            p[Keys.LOGGED_IN] = true
            p[Keys.USER_ID] = userId.toString()
            p[Keys.USER_NICK] = nickname
            p[Keys.USER_EMAIL] = email
            p[Keys.USER_GENDER] = gender
            p[Keys.USER_JOB_ID] = jobId.toString()
            p[Keys.USER_JOB_NAME] = jobName
            p[Keys.USER_MY_LINKU] = myLinku.toString()
            p[Keys.USER_MY_FOLDER] = myFolder.toString()
            p[Keys.USER_MY_AI_LINKU] = myAiLinku.toString()
        }
    }

    suspend fun clear() {
        context.dataStore.edit { p ->
            p[Keys.LOGGED_IN] = false
            p.remove(Keys.USER_ID)
            p.remove(Keys.USER_NICK)
            p.remove(Keys.USER_EMAIL)
            p.remove(Keys.USER_GENDER)
            p.remove(Keys.USER_JOB_ID)
            p.remove(Keys.USER_JOB_NAME)
            p.remove(Keys.USER_MY_LINKU)
            p.remove(Keys.USER_MY_FOLDER)
            p.remove(Keys.USER_MY_AI_LINKU)
        }
    }

    //프로필 수정 시 호출(닉네임, 작업만 변경) -> TODO : 지현이에게 전달
    suspend fun updateProfile(nickname: String, jobId: Long, jobName: String) {
        val current = session.first() // 현재 세션 스냅샷 가져오기
        saveLogin(
            userId = current.userId ?: -1L,
            nickname = nickname,
            email = current.email ?: "",
            gender = current.gender ?: "",
            jobId = jobId,
            jobName = jobName,
            myLinku = current.myLinku ?: -1L,
            myFolder = current.myFolder ?: -1L,
            myAiLinku = current.myAiLinku ?: -1L
        )
    }

    // 닉네임만 수정할 때 -> TODO : 지현이에게 전달
    suspend fun updateNickname(nickname: String) {
        val current = session.first()
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
    )

    val session: Flow<SessionSnapshot> =
        context.dataStore.data.map { p ->
            SessionSnapshot(
                loggedIn   = p[Keys.LOGGED_IN] ?: false,
                userId   = p[Keys.USER_ID]?.toLongOrNull(),
                nickname   = p[Keys.USER_NICK],
                email      = p[Keys.USER_EMAIL],
                gender     = p[Keys.USER_GENDER],
                jobId      = p[Keys.USER_JOB_ID]?.toLongOrNull(),
                jobName    = p[Keys.USER_JOB_NAME],
                myLinku    = p[Keys.USER_MY_LINKU]?.toLongOrNull(),
                myFolder   = p[Keys.USER_MY_FOLDER]?.toLongOrNull(),
                myAiLinku  = p[Keys.USER_MY_AI_LINKU]?.toLongOrNull(),
            )
        }


}