package com.linku.login.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.model.auth.LoginErrorType
import com.linku.core.model.auth.LoginState
import com.linku.core.model.auth.LoginType
import com.linku.core.repository.AuthRepository
import com.linku.data.api.ApiError
import com.linku.data.api.toLoginErrorType
import com.linku.data.preference.AuthPreference
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.LoginUiEffect
import com.linku.login.viewmodel.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
open class LoginViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference,
) : AndroidViewModel(application),
    MviContainer<LoginUiState, LoginUiEffect> by mviContainer(LoginUiState()) {

    private val _autoLoginState = MutableStateFlow<AutoLoginState>(AutoLoginState.Idle)
    val autoLoginState: StateFlow<AutoLoginState> = _autoLoginState

    fun onEmailChanged(email: String) {
        updateState { copy(email = email) }
        clearError()
    }

    fun onPasswordChanged(password: String) {
        updateState { copy(password = password) }
        clearError()
    }

    fun clearError() {
        if (state.value.loginState is LoginState.Error) {
            updateState { copy(loginState = LoginState.Idle) }
        }
    }

    fun onSignUpClicked() {
        postSideEffect(LoginUiEffect.NavigateToSignUp)
    }

    fun onResetPasswordClicked() {
        postSideEffect(LoginUiEffect.NavigateToResetPassword)
    }

    fun login(
        email: String = state.value.email.trim(),
        password: String = state.value.password.trim()
    ) {
        if (email.isBlank() || password.isBlank()) {
            updateState { copy(loginState = LoginState.Error(LoginErrorType.INVALID_CREDENTIALS)) }
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(loginState = LoginState.Loading) }
                Log.d(TAG, "로그인 시도")

                val deviceId = authPreference.getDeviceId()
                val deviceType = authPreference.getDeviceType()


                val loginResult = authRepository.login(
                    email = email,
                    password = password,
                    deviceId = deviceId,
                    deviceType = deviceType
                ).getOrThrow() //TODO :  .fold() 형식으로 수정하기

                authPreference.saveTokens(
                    accessToken = loginResult.accessToken,
                    refreshToken = loginResult.refreshToken,
                    userId = loginResult.userId,
                    loginType = LoginType.EMAIL
                )
                Log.d(TAG, "인증 토큰 및 유저 ID 저장 완료 (ID: ${loginResult.userId})")

                // 성공 상태 -> MainApp에서 isLoggedIn Flow 변화를 감지해 홈으로 이동하게 함.
                updateState { copy(loginState = LoginState.Success(loginResult)) }
                postSideEffect(LoginUiEffect.LoginSuccess)

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(TAG, "로그인 실패", exception)
                updateState { copy(loginState = LoginState.Error(exception.toLoginErrorType())) }
            }
        }
    }

    fun tryAutoLogin(
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        if (_autoLoginState.value == AutoLoginState.Checking) return

        viewModelScope.launch {
            try {
                // dataStore 기반의 isLoggedIn Flow 최신 값을 받아옴
                val isAlreadyLoggedIn = authPreference.isLoggedIn.first()

                if (!isAlreadyLoggedIn) {
                    _autoLoginState.value = AutoLoginState.Failed
                    onFail()
                    return@launch
                }

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

    companion object {
        private const val TAG = "LoginViewModel"
    }

}
