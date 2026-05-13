package com.linku.login.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.datastore.session.LoginSessionStore
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.model.auth.LoginErrorType
import com.linku.core.model.auth.LoginState
import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
import com.linku.data.api.ApiError
import com.linku.data.api.toLoginErrorType
import com.linku.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
open class LoginViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository, //getUserInfo 사용함.
    private val loginSessionStore: LoginSessionStore,
    private val authPreference: AuthPreference,
) : AndroidViewModel(application) {

    // deviceId 앱 시작 시 한 번만 읽음
    private val deviceId: String by lazy {
        @SuppressLint("HardwareIds")
        "android-" + Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    // 로그인/자동로그인 공통 함수, 마이페이지 조회 → 세션 풀 세팅
    private suspend fun fetchAndSaveUserSession(userId: Long) {
        val userInfo =
            userRepository.getUserInfo(userId) // 사용자 정보 조회 api GET /api/users/{userId} 이용.

        loginSessionStore.saveLogin( // SessionStore에 세션 생성.
            userId = userId,
            nickname = userInfo.nickname,
            email = userInfo.email,
            gender = userInfo.gender,
            jobId = userInfo.jobId,
            jobName = userInfo.jobName,
            myLinku = userInfo.myLinku,
            myFolder = userInfo.myFolder,
            myAiLinku = userInfo.myAiLinku,
            purposes = userInfo.purposes,
            interests = userInfo.interests
        )

        Log.d(TAG, "유저 세션 풀 세팅 완료 (ID: $userId)")
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _autoLoginState = MutableStateFlow<AutoLoginState>(AutoLoginState.Idle)
    val autoLoginState: StateFlow<AutoLoginState> = _autoLoginState

    fun clearError() {
        if (_loginState.value is LoginState.Error) {
            _loginState.value = LoginState.Idle
        }
    }

    fun login(email: String, password: String) {
        // 이메일이나 비밀번호가 비어있는 경우
        if (email.isBlank() || password.isBlank()) {
            //error로 처리함. 이메일 주소 혹은 비밀번호 다시 확인하세요. 메시지를 담아서 전달함.
            // TODO : 어 그냥 이메일 입력해주세요, 비밀번호 입력해주세요 넣으면 되는거 아닌가?
            _loginState.value = LoginState.Error(LoginErrorType.INVALID_CREDENTIALS)
            // 서버 호출할 필요 없음. 바로 종료
            return
        }

        viewModelScope.launch {
            try {
                // 로딩 시작
                _loginState.value = LoginState.Loading
                Log.d(TAG, "로그인 시도")

                // API 호출
                val loginResult = authRepository.login(
                    email = email.trim(),
                    password = password.trim(),
                    deviceId = deviceId,       // 추가
                    deviceType = "PHONE"       // 추가
                )

                // 토큰 + userId 저장
                authPreference.saveTokens(
                    accessToken = loginResult.accessToken,
                    refreshToken = loginResult.refreshToken,
                    userId = loginResult.userId
                )

                // 마이페이지 조회 → 세션 풀 세팅
                fetchAndSaveUserSession(loginResult.userId)

                // 성공 상태
                _loginState.value = LoginState.Success(loginResult)

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(TAG, "로그인 실패", exception)
                _loginState.value = LoginState.Error(exception.toLoginErrorType())
            }
        }
    }


    // ServerApi.refreshToken() 호출 -> 성공하면 새로운 엑세스 토큰 발급하고 리프레쉬 토큰 저장함.
    // 실패하는 경우 토큰 정리함.
    // 자동 로그인 시점에 마이페이지 조회 → 세션 풀 세팅함.
    fun tryAutoLogin(
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        if (_autoLoginState.value == AutoLoginState.Checking) return

        viewModelScope.launch {
            try {
                _autoLoginState.value = AutoLoginState.Checking

                if (!authPreference.isLoggedIn) {
                    _autoLoginState.value = AutoLoginState.Failed
                    onFail()
                    return@launch
                }

                val userId = authPreference.userId //비정상 상태일 경우에는
                    ?: throw IllegalStateException("userId missing")
                fetchAndSaveUserSession(userId) //세션 풀세팅


                Log.d(TAG, "자동 로그인 성공")
                _autoLoginState.value = AutoLoginState.Success // 이 앱 안에 ui에 바로 쓸 세션이 있음.
                onSuccess()

            } catch (e: ApiError.TokenExpired) {
                // 이 경우만 logout
                Log.e(TAG, "자동 로그인 실패: 토큰 만료")
                authPreference.clear()
                _autoLoginState.value = AutoLoginState.Failed
                onFail()

            } catch (e: Exception) {
                // 나머지는 절대 logout 하지 않음
                Log.e(TAG, "자동 로그인 실패: ${e.message}")
                _autoLoginState.value = AutoLoginState.Failed
                onFail()
            }
        }
    }


    // 태그 상수 추가함.
    companion object {
        private const val TAG = "LoginViewModel"
    }

}
