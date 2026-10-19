package com.linku.login.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.toLoginErrorType
import com.linku.core.model.LoginResult
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.model.auth.LoginErrorType
import com.linku.core.model.auth.LoginState
import com.linku.core.usecase.AutoLoginUseCase
import com.linku.core.usecase.ClearAuthSessionUseCase
import com.linku.core.usecase.EmailLoginOutcome
import com.linku.core.usecase.LoginWithEmailUseCase
import com.linku.core.usecase.RecoverAccountOutcome
import com.linku.core.usecase.RecoverAccountUseCase
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.LoginUiEffect
import com.linku.login.viewmodel.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
open class LoginViewModel @Inject constructor(
    application: Application,
    private val loginWithEmail: LoginWithEmailUseCase,
    private val recoverAccount: RecoverAccountUseCase,
    private val autoLogin: AutoLoginUseCase,
    private val clearAuthSession: ClearAuthSessionUseCase,
) : AndroidViewModel(application),
    MviContainer<LoginUiState, LoginUiEffect> by mviContainer(LoginUiState()) {

    private val _autoLoginState = MutableStateFlow<AutoLoginState>(AutoLoginState.Idle)
    val autoLoginState: StateFlow<AutoLoginState> = _autoLoginState

    // INACTIVE 로그인 응답을 복구 모달이 뜬 동안 임시로 들고 있는 값. "부활" 성공 시에만
    // 정식 세션 저장에 사용하고, 그 전까지는 정식 로그인 상태(LOGGED_IN)를 저장하지 않음.
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

                when (val outcome = loginWithEmail(email, password)) {
                    is EmailLoginOutcome.RecoveryRequired -> {
                        // 탈퇴 유예기간 계정 -> 홈으로 보내지 않고 복구 여부를 묻는 모달을 띄움.
                        // 정식 로그인 상태(LOGGED_IN)를 저장하면 다음 실행 시 자동 로그인이 서버 상태를
                        // 재확인하지 않고 로컬 플래그만 보고 바로 홈으로 보내버리는 문제가 있어, 유스케이스가
                        // 복구 API 호출용 토큰만 임시 저장함.
                        Log.d(TAG, "탈퇴 유예기간 계정 감지 - 복구 모달 노출")
                        pendingRecoverLoginResult = outcome.loginResult
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
                    }

                    is EmailLoginOutcome.Authenticated -> {
                        val loginResult = outcome.loginResult
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
            val outcome = recoverAccount(
                consumePendingUserId = {
                    // 복구 응답을 기다리는 동안 모달이 닫혔는지 응답 이후에 확인하고, 보류 상태를 해제함.
                    val pendingResult = pendingRecoverLoginResult
                    pendingRecoverLoginResult = null
                    pendingResult?.userId
                }
            )

            when (outcome) {
                RecoverAccountOutcome.Recovered -> {
                    Log.d(TAG, "계정 복구 성공")
                    postSideEffect(LoginUiEffect.LoginSuccess)
                }

                RecoverAccountOutcome.Ignored -> Unit

                is RecoverAccountOutcome.Failed -> {
                    Log.e(TAG, "계정 복구 실패 (유예기간 만료 등)", outcome.error)
                    updateState { copy(loginState = LoginState.Error(LoginErrorType.INACTIVE_USER_ERROR)) }
                }
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
            clearAuthSession()
        }
    }

    /** 복구 모달을 아무 선택 없이 닫았을 때(외부 영역 클릭 등) 호출됨. 임시 저장된 복구 전용
     * 토큰이 계속 남아있으면 이후 다른 API 호출에 잘못 붙을 수 있어 함께 정리함. */
    fun dismissRecoverModal() {
        viewModelScope.launch {
            pendingRecoverLoginResult = null
            clearAuthSession()
        }
    }

    fun tryAutoLogin() {
        if (_autoLoginState.value == AutoLoginState.Checking) return

        viewModelScope.launch {
            _autoLoginState.value = autoLogin()
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}
