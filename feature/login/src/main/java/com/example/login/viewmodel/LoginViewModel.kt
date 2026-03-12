package com.example.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LoginResult
import com.example.core.repository.UserRepository
import com.example.core.session.SessionStore
import com.example.data.api.ApiError
import com.example.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.example.core.model.auth.AutoLoginState
import com.example.core.model.auth.LoginErrorType
import com.example.core.model.auth.LoginState
import com.example.core.model.auth.SocialLoginData
import com.example.core.model.auth.SocialLoginEvent

/**
 * 세션 정리
 * 1. 로그인 -> 2. 로그인 api 호출 -> 3. 토큰 저장(authPreference)
 * 4. 사용자 정보 전체 조회 (GET /api/users/{userId}) -> 5. 세션 풀세팅
 * 6. 로그인 성공(Main 진입하면서 ui는 이미 완성된 세션을 구독함.)
 * 
 * 
 * 자동 로그인
 * 1. 앱 시작 -> 2. authPreference.isLoggedIn == true로 자동 로그인 판단.
 * 3. fetchAndSaveUserSession(userId)- 서버로부터 사용자 정보 받아서 앱 세션 저장소에 만들어 놓음.
 * 4. 세션 스토어 풀 세팅함.(api 호출 줄임) -> 5. AutoLoginState.Success한 뒤, 6. 메인 진입.
 * */




@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStore: SessionStore,
    private val authPreference: AuthPreference,
) : ViewModel() {

    // 로그인/자동로그인 공통 함수, 마이페이지 조회 → 세션 풀 세팅
    private suspend fun fetchAndSaveUserSession(userId: Long) {
        val userInfo = userRepository.getUserInfo(userId) // 사용자 정보 조회 api GET /api/users/{userId} 이용.

        sessionStore.saveLogin( // SessionStore에 세션 생성.
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
        // 입력 검증
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error(LoginErrorType.INVALID_CREDENTIALS)
            return
        }

        viewModelScope.launch {
            try {
                // 로딩 시작
                _loginState.value = LoginState.Loading
                Log.d(TAG, "로그인 시도")

                // API 호출
                val result = userRepository.login(
                    email = email.trim(),
                    password = password.trim()
                )

                Log.d(TAG, "로그인 성공")

                val userId = result.userId

                // 토큰 + userId 저장
                authPreference.saveTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    userId = userId
                )

                // 마이페이지 조회 → 세션 풀 세팅
                fetchAndSaveUserSession(userId)

                // 성공 상태
                _loginState.value = LoginState.Success(result)

            } catch (e: HttpException) {
                Log.e(TAG, "로그인 실패 - HTTP 에러: ${e.code()}")
                _loginState.value = LoginState.Error(
                    when (e.code()) {
                        401, 403 -> LoginErrorType.INVALID_CREDENTIALS
                        in 500..599 -> LoginErrorType.SERVER_ERROR
                        else -> LoginErrorType.UNKNOWN_ERROR
                    }
                )
            } catch (e: IOException) {
                // 네트워크 에러 별도 처리
                Log.e(TAG, "로그인 실패 - 네트워크 에러", e)
                _loginState.value = LoginState.Error(LoginErrorType.NETWORK_ERROR)  // _ 추가!
            }
            catch (e: Exception) {
                Log.e(TAG, "로그인 실패", e)
                _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
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

    // 마이페이지 로그아웃
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                //  서버에 로그아웃 알림? 필요할까요?
                // userRepository.logout()

                // 로컬 저장소 비우기 (토큰, 유저 아이디 삭제)
                authPreference.clear()

                // 인메모리 세션 스토어 비우기 -> 아예 비울 수 있도록.
                sessionStore.clear() // SessionStore에 clear() 함수가 있다고 가정

                Log.d("LoginVM", "로그아웃 및 세션 정리 완료")
                onComplete()
            } catch (e: Exception) {
                Log.e("LoginVM", "로그아웃 중 오류 발생", e)
                // 에러가 나더라도 로컬 데이터는 지워야 함
                authPreference.clear()
                onComplete()
            }
        }
    }

    // 태그 상수 추가함.
    companion object {
        private const val TAG = "LoginViewModel"
    }
    // 소셜 로그인 토큰 처리 (딥링크를 통해 받은 토큰 처리)
//  TODO: 백엔드 수정 완료 후 아래 내용 업데이트 필요
// 1. refreshToken 딥링크 응답에 추가되면 → authPreference.saveTokens에 실제값 저장
// 2. GET /api/users/me API 추가되면 → userId 조회 후 fetchAndSaveUserSession 호출
// 3. 현재는 자동 로그인 불가 상태 (refreshToken 빈값으로 isLoggedIn = false)
    // 소셜 로그인 토큰 처리(딥링크를 통해 받은 토큰 처리)
    private val _socialLoginEvent = MutableStateFlow<SocialLoginEvent?>(null)
    val socialLoginEvent: StateFlow<SocialLoginEvent?> = _socialLoginEvent

    fun consumeSocialLoginEvent() {
        _socialLoginEvent.value = null
    }

    fun handleSocialDeepLink(data: SocialLoginData) {
        viewModelScope.launch {
            Log.d("SOCIAL_VM", "handleSocialDeepLink 호출됨: $data")
            try {
                _loginState.value = LoginState.Loading

                when {
                    // 기존 유저 - 바로 홈으로
                    data.result == "SUCCESS" && data.status == "ACTIVE" -> {
                        Log.d("SOCIAL_VM", "ACTIVE 케이스 진입")
                        val accessToken  = data.accessToken  ?: run {
                            _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
                            return@launch
                        }
                        val refreshToken = data.refreshToken ?: run {
                            _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
                            return@launch
                        }
                        // TODO: 서원이 /api/users/me API 확인 후 아래 작업 필요
                        // 1. GET /api/users/me 호출 → 실제 userId 조회
                        // 2. authPreference.saveTokens(userId = 실제값) 으로 교체
                        // 3. fetchAndSaveUserSession(userId) 호출 → 세션 풀 세팅
                        // 4. 현재는 userId=0L 임시값이라 자동 로그인 불가 상태

                        // TODO: 서원이 /api/users/me 확인 후 userId 실제값으로 교체
                        authPreference.saveTokens(
                            accessToken  = accessToken,
                            refreshToken = refreshToken,
                            userId       = 0L // TODO: 실제 userId로 교체 필요 - 지금 자동 로그인 불가, 닉네임 제대로 안 내려옴.
                        )
                        Log.d(TAG, "소셜 ACTIVE 토큰 저장 완료")

                        _loginState.value = LoginState.Success(
                            LoginResult(
                                accessToken  = accessToken,
                                refreshToken = refreshToken,
                                userId       = 0,
                                status       = "ACTIVE",
                                inactiveDate = null
                            )
                        )
                    }

                    // 신규 유저 - 프로필 입력 화면으로
                    data.result == "SUCCESS" && data.status == "TEMP" -> {
                        val socialToken = data.socialToken ?: run {
                            _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
                            return@launch
                        }
                        Log.d(TAG, "소셜 TEMP → SocialEntry로 이동")

                        _socialLoginEvent.value = SocialLoginEvent.NavigateToSocialEntry(
                            socialToken = socialToken,
                            provider    = data.provider
                        )
                        _loginState.value = LoginState.Idle
                    }

                    data.result == "FAIL" -> {
                        Log.e(TAG, "소셜 로그인 실패: ${data.errorCode}")
                        _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "소셜 딥링크 처리 실패", e)
                _loginState.value = LoginState.Error(LoginErrorType.UNKNOWN_ERROR)
            }
        }
    }
}
