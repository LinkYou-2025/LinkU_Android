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

// 로그인 상태 리펙토링
sealed  class LoginState {
    object Idle : LoginState()           // 초기 상태
    object Loading : LoginState()        // 로그인 진행 중
    data class Success(val result: LoginResult) : LoginState()  // 성공
    data class Error(val errorType: LoginErrorType) : LoginState()  // 실패
}

enum class LoginErrorType(val message: String) {
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다."),
    SERVER_ERROR("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    NETWORK_ERROR("네트워크 연결을 확인해주세요."),
    UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.")
}

sealed class AutoLoginState {
    object Idle : AutoLoginState()
    object Checking : AutoLoginState()
    object Success : AutoLoginState()
    object Failed : AutoLoginState()
}



@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionStore: SessionStore,
    private val authPreference: AuthPreference,
) : ViewModel() {

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

                // 세션 저장
                saveUserSession(result)

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

    // 세션 저장.
    // TODO : 하진언니(로그인 담당자) 로그인할 때, 세션정보(사용자 정보 받을 수 있도록 api 수정 요청하기)
    private suspend fun saveUserSession(result: LoginResult) {
        val userIdLong = result.userId.toLong()

        // 보안 토큰 및 유저 식별자 저장
        authPreference.saveTokens(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            userId = userIdLong
        )

        // 사용자 세션 정보 저장 (마이페이지에서 활용하기)
        // 이후 API에서 닉네임이나 이메일 등을 함께 준다면 이 부분을 result.nickname 등으로 채울 수 있음.
        sessionStore.saveLogin(
            userId = userIdLong,
            nickname = "", // 초기값, 필요시 result에 추가하여 전달 가능
            email = "",
            gender = "",
            jobId = -1L,
            jobName = "",
            myLinku = -1L,
            myFolder = -1L,
            myAiLinku = -1L
        )

        Log.d(TAG, "유저 세션 및 토큰 저장 완료 (ID: $userIdLong)")
    }

    // ServerApi.refreshToken() 호출 -> 성공하면 새로운 엑세스 토큰 발급하고 리프레쉬 토큰 저장함.
    // 실패하는 경우 토큰 정리함.
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

                val userId = authPreference.userId ?: -1L
                val userInfo = userRepository.getUserInfo(userId)

                sessionStore.saveLogin(
                    userId = userId,
                    nickname = userInfo.nickname,
                    email = userInfo.email,
                    gender = userInfo.gender,
                    jobId = userInfo.jobId,
                    jobName = userInfo.jobName,
                    myLinku = userInfo.myLinku,
                    myFolder = userInfo.myFolder,
                    myAiLinku = userInfo.myAiLinku
                )

                Log.d(TAG, "자동 로그인 성공")
                _autoLoginState.value = AutoLoginState.Success
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
//    fun tryAutoLogin(onSuccess: () -> Unit, onFail: () -> Unit) {
//        if (_autoLoginState.value == AutoLoginState.Checking) return
//
//        viewModelScope.launch {
//            try {
//                _autoLoginState.value = AutoLoginState.Checking
//
//                if (!authPreference.isLoggedIn) {
//                    _autoLoginState.value = AutoLoginState.Failed
//                    onFail()
//                    return@launch
//                }
//
//                val userId = authPreference.userId ?: -1L
//                val userInfo = userRepository.getUserInfo(userId)
//
//                sessionStore.saveLogin(
//                    userId = userId,
//                    nickname = userInfo.nickname,
//                    email = userInfo.email,
//                    gender = userInfo.gender,
//                    jobId = userInfo.jobId,
//                    jobName = userInfo.jobName,
//                    myLinku = userInfo.myLinku,
//                    myFolder = userInfo.myFolder,
//                    myAiLinku = userInfo.myAiLinku
//                )
//
//                Log.d(TAG, "자동 로그인 및 세션 갱신 성공")
//                _autoLoginState.value = AutoLoginState.Success
//                onSuccess()
//
//            } catch (e: ApiError.TokenExpired) {
//                // 토큰 만료: 확실한 세션 종료 상황이므로 로그아웃 후 로그인 창으로
//                Log.e(TAG, "자동 로그인 실패: 세션 만료")
//                userRepository.logout()
//                _autoLoginState.value = AutoLoginState.Failed
//                onFail()
//            } catch (e: Exception) {
//                // 기타 예외 처리
//                Log.e(TAG, "자동 로그인 실패: 기타 오류(${e.message})")
//                _autoLoginState.value = AutoLoginState.Failed
//
//                if (e is ApiError.NetworkError || e is IOException) {
//                    // 네트워크 문제: 토큰은 유효할 수 있으므로 logout 시키지 않고 실패만 처리
//                    // Splash 화면 이후 네트워크 연결 확인 메시지를 띄우는 용도로 사용하나요????
//                    // TODO : 네트워크 문제로 아주 단시간 끊긴 경우 토큰은 유효하기에 이 상황에서 어떻게 해야하는지 다인언니랑 논의해야함.
//                    onFail()
//                } else {
//                    // 그 외 알 수 없는 인증 오류: 안전을 위해 로그아웃 처리
//                    userRepository.logout()
//                    onFail()
//                }
//            }
//        }
//    }

    // 태그 상수 추가함.
    companion object {
        private const val TAG = "LoginViewModel"
    }
}
