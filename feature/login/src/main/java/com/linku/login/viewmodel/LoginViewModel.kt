package com.linku.login.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.model.LoginResult
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.model.auth.LoginErrorType
import com.linku.core.model.auth.LoginState
import com.linku.core.model.auth.LoginType
import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference,
) : AndroidViewModel(application),
    MviContainer<LoginUiState, LoginUiEffect> by mviContainer(LoginUiState()) {

    private val _autoLoginState = MutableStateFlow<AutoLoginState>(AutoLoginState.Idle)
    val autoLoginState: StateFlow<AutoLoginState> = _autoLoginState

    // INACTIVE 로그인 응답을 복구 모달이 뜬 동안 임시로 들고 있는 값. "부활" 성공 시에만
    // saveTokens()로 정식 세션 저장에 사용하고, 그 전까지는 AuthPreference에 LOGGED_IN을 세팅하지 않음.
    private var pendingRecoverLoginResult: LoginResult? = null

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

    /**
     * EmailLoginScreen을 벗어날 때(뒤로가기, 회원가입/비밀번호 재설정 이동 등) 호출됨.
     * loginViewModel은 auth_graph 전체에 스코프돼 있어 화면을 나가도 값이 그대로 남아있으므로,
     * 다시 이메일 로그인 화면에 들어왔을 때 이전에 입력했던 이메일/비밀번호가 보이지 않도록 초기화한다.
     */
    fun clearInput() {
        updateState { copy(email = "", password = "", loginState = LoginState.Idle) }
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

                if (loginResult.status == "INACTIVE") {
                    // 탈퇴 유예기간 계정 -> 홈으로 보내지 않고 복구 여부를 묻는 모달을 띄움.
                    // LOGGED_IN을 세팅하면 다음 실행 시 tryAutoLogin()이 서버 상태를 재확인하지 않고
                    // 로컬 플래그만 보고 바로 홈으로 보내버리는 문제가 있어, 여기서는 saveTokens() 대신
                    // updateAccessToken()으로 복구 API 호출용 토큰만 임시 저장함.
                    Log.d(TAG, "탈퇴 유예기간 계정 감지 - 복구 모달 노출")
                    pendingRecoverLoginResult = loginResult
                    authPreference.updateAccessToken(
                        loginResult.accessToken,
                        loginResult.refreshToken
                    )
                    // 인증 자체는 이미 끝났으므로 입력해둔 이메일/비밀번호는 화면에 남겨두지 않음
                    // (로그아웃 후 다시 이 화면에 들어와도 이전 입력값이 남아있지 않도록).
                    updateState {
                        copy(
                            loginState = LoginState.Idle,
                            email = "",
                            password = ""
                        )
                    }
                    postSideEffect(LoginUiEffect.ShowRecoverModal)
                } else {
                    authPreference.saveTokens(
                        accessToken = loginResult.accessToken,
                        refreshToken = loginResult.refreshToken,
                        userId = loginResult.userId,
                        loginType = LoginType.EMAIL
                    )
                    Log.d(TAG, "인증 토큰 및 유저 ID 저장 완료 (ID: ${loginResult.userId})")

                    // 성공 상태 -> MainApp에서 isLoggedIn Flow 변화를 감지해 홈으로 이동하게 함.
                    // 인증에 사용한 이메일/비밀번호도 화면에서 지움 (재진입 시 이전 입력값 노출 방지).
                    updateState {
                        copy(
                            loginState = LoginState.Success(loginResult),
                            email = "",
                            password = ""
                        )
                    }
                    postSideEffect(LoginUiEffect.LoginSuccess)
                }

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(TAG, "로그인 실패", exception)
                updateState { copy(loginState = LoginState.Error(exception.toLoginErrorType())) }
            }
        }
    }

    /**
     * 복구 모달에서 "계정 복구"를 선택했을 때 호출됨. 로그인 응답으로 이미 저장된
     * 복구 전용 accessToken을 이용해 `users/recover`를 호출하고, 성공하면 정상 로그인과
     * 동일하게 홈으로 이동시킴.
     */
    fun recoverAccount() {
        viewModelScope.launch {
            val recoverResult = userRepository.recoverUser()
            val pendingResult = pendingRecoverLoginResult
            pendingRecoverLoginResult = null

            recoverResult.onSuccess { recovered ->
                if (pendingResult == null) return@onSuccess
                Log.d(TAG, "계정 복구 성공")
                // 복구가 확정된 시점에만 정식 세션(LOGGED_IN=true)으로 저장함.
                // recover API가 새로 내려준 정식 토큰을 씀 (로그인 때 받은 복구 전용 토큰이 아님).
                authPreference.saveTokens(
                    accessToken = recovered.accessToken,
                    refreshToken = recovered.refreshToken,
                    userId = pendingResult.userId,
                    loginType = LoginType.EMAIL
                )
                postSideEffect(LoginUiEffect.LoginSuccess)
            }.onFailure { error ->
                Log.e(TAG, "계정 복구 실패 (유예기간 만료 등)", error)
                // 복구 실패 시 계정은 여전히 비활성 상태이므로 임시 저장된 토큰을 정리함.
                authPreference.clear()
                updateState { copy(loginState = LoginState.Error(LoginErrorType.INACTIVE_USER_ERROR)) }
            }
        }
    }

    /**
     * 복구 모달에서 "탈퇴 유지"를 선택했을 때 호출됨. 임시 저장해둔 복구 전용 토큰을
     * 지우고 로그인 화면에 그대로 남김.
     */
    fun keepWithdrawn() {
        viewModelScope.launch {
            pendingRecoverLoginResult = null
            authPreference.clear()
        }
    }

    /** 복구 모달을 아무 선택 없이 닫았을 때(외부 영역 클릭 등) 호출됨. 임시 저장된 복구 전용
     * 토큰이 계속 남아있으면 이후 다른 API 호출에 잘못 붙을 수 있어 함께 정리함. */
    fun dismissRecoverModal() {
        viewModelScope.launch {
            pendingRecoverLoginResult = null
            authPreference.clear()
        }
    }

    fun tryAutoLogin() {
        if (_autoLoginState.value == AutoLoginState.Checking) return

        viewModelScope.launch {
            try {
                // dataStore 기반의 isLoggedIn Flow 최신 값을 받아옴
                val isAlreadyLoggedIn = authPreference.isLoggedIn.first()

                if (!isAlreadyLoggedIn) {
                    _autoLoginState.value = AutoLoginState.Failed
                    return@launch
                }

                Log.d(TAG, "자동 로그인 성공")
                _autoLoginState.value = AutoLoginState.Success

            } catch (e: ApiError.Auth.TokenExpired) {
                // 이 경우만 logout
                Log.e(TAG, "자동 로그인 실패: 토큰 만료")
                authPreference.clear()
                _autoLoginState.value = AutoLoginState.Failed

            } catch (e: Exception) {
                // 나머지는 절대 logout 하지 않음
                Log.e(TAG, "자동 로그인 실패: ${e.message}")
                _autoLoginState.value = AutoLoginState.Failed
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}
