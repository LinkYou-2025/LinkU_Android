package com.example.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LoginResult
import com.example.core.repository.UserRepository
import com.example.core.session.SessionStore
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
        // 1. 입력 검증
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error(LoginErrorType.INVALID_CREDENTIALS)
            return
        }

        viewModelScope.launch {
            try {
                // 2. 로딩 시작
                _loginState.value = LoginState.Loading
                Log.d(TAG, "로그인 시도")

                // 3. API 호출
                val result = userRepository.login(
                    email = email.trim(),
                    password = password.trim()
                )

                Log.d(TAG, "로그인 성공")  // userId 안 찍는 고르 - 보안 문제 때문. 그러나 확인이 필요하다면,
                //Log.d(TAG, "로그인 성공: userId=${res.userId}")

                // 4. 세션 저장 (별도 함수로 분리)
                saveUserSession(result)

                // 5. 성공 상태
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
    private suspend fun saveUserSession(result: LoginResult) {
        authPreference.userId = result.userId?.toLong()

        sessionStore.saveLogin(
            userId = result.userId?.toLong() ?: -1L,
            nickname = "",
            email = "",
            gender = "",
            jobId = -1L,
            jobName = "",
            myLinku = -1L,
            myFolder = -1L,
            myAiLinku = -1L
        )
    }

    // ServerApi.refreshToken() 호출 -> 성공하면 새로운 엑세스 토큰 발급하고 리프레쉬 토큰 저장함.
    // 실패하는 경우 토큰 정리함.
    fun tryAutoLogin(onSuccess: () -> Unit, onFail: () -> Unit) {
        if (_autoLoginState.value == AutoLoginState.Checking) {
            Log.d(TAG, "자동 로그인 이미 진행 중")
            return
        }

        viewModelScope.launch {
            try {
                _autoLoginState.value = AutoLoginState.Checking

                val refreshToken = authPreference.refreshToken
                if (refreshToken.isNullOrBlank()) {
                    throw IllegalStateException("저장된 리프레시 토큰이 없습니다.")
                }

                val newTokens = userRepository.reissue(refreshToken)
                authPreference.accessToken = newTokens.accessToken
                authPreference.refreshToken = newTokens.refreshToken

                Log.d(TAG, "자동 로그인 성공")
                _autoLoginState.value = AutoLoginState.Success
                onSuccess()

            } catch (e: HttpException) {
                // 401/403만 토큰 삭제 - 토큰이 실제로 무효한 경우
                Log.e(TAG, "자동 로그인 실패 - HTTP: ${e.code()}", e)
                if (e.code() == 401 || e.code() == 403) {
                    clearAuthData()
                }
                _autoLoginState.value = AutoLoginState.Failed
                onFail()
            } catch (e: IOException) {
                // 네트워크 오류 - 토큰은 유지 (나중에 재시도 가능)
                Log.e(TAG, "자동 로그인 실패 - 네트워크", e)
                _autoLoginState.value = AutoLoginState.Failed
                onFail()
            } catch (e: Exception) {
                // 기타 예외 - 안전하게 토큰 삭제
                Log.e(TAG, "자동 로그인 실패", e)
                clearAuthData()
                _autoLoginState.value = AutoLoginState.Failed
                onFail()
            }
        }
    }

    private fun clearAuthData() {
        authPreference.accessToken = null
        authPreference.refreshToken = null
        authPreference.userId = null
    }
    // 태그 상수 추가함.
    companion object {
        private const val TAG = "LoginViewModel"
    }
}
