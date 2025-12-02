package com.example.login.auth



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LoginResult
import com.example.core.repository.UserRepository
//import com.example.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import retrofit2.HttpException

//로그인 뷰모델 : 로그인 로직 담당. 레포지토리를 통해 로그인 api 수학.
//로그인 성공시 사용자 세션 및 userId 전달.
//예외 발생 시 UI 에러태그 전달.

@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val repo: UserRepository,
    private val sessionStore: com.example.core.session.SessionStore,
    private val authPreference: com.example.data.preference.AuthPreference,
) : ViewModel() {

    // UI가 사용할 단일 상태
    data class LoginState(
        val loading: Boolean = false,
        val result: LoginResult? = null,   // userId, token, status, inactiveDate
        val errorTag: String? = null       // "INVALID_CREDENTIALS", "SERVER_ERROR"
    )

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun clearError() {
        _loginState.update { it.copy(errorTag = null) }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState(loading = true)
            try {
                val res: LoginResult = repo.login(email, password)
                Log.d("LoginViewModel", "로그인 성공: userId=${res.userId}")

                // 토큰 저장은 Repo에서 이미 처리됨. 여기서는 userId/세션만.
                authPreference.userId = res.userId?.toLong()

                sessionStore.saveLogin(
                    userId   = res.userId?.toLong() ?: -1L,
                    nickname = "",
                    email    = "",
                    gender   = "",
                    jobId    = -1L,
                    jobName  = "",
                    myLinku  = -1L,
                    myFolder = -1L,
                    myAiLinku= -1L
                )

                _loginState.value = LoginState(loading = false, result = res)
                Log.d("LoginViewModel", "login() 완료")

            } catch (e: HttpException) {
                Log.e("LoginViewModel", "HttpException: code=${e.code()}, msg=${e.message}")
                _loginState.value = LoginState(
                    loading = false,
                    errorTag = when (e.code()) {
                        401, 403 -> "INVALID_CREDENTIALS"
                        else -> "SERVER_ERROR"
                    }
                )
            } catch (_: IllegalStateException) {
                _loginState.value = LoginState(loading = false, errorTag = "INVALID_CREDENTIALS")
            } catch (_: Exception) {
                _loginState.value = LoginState(loading = false, errorTag = "SERVER_ERROR")
            }
        }
    }

    fun tryAutoLogin(
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val refresh = authPreference.refreshToken
                    ?: throw Exception("No refresh token stored")

                Log.d("LoginViewModel", "자동 로그인 시도: refreshToken=$refresh")

                // 서버에 토큰 재발급 요청
                val newTokens = repo.reissue(refresh)

                // 새 토큰 저장
                authPreference.accessToken = newTokens.accessToken
                authPreference.refreshToken = newTokens.refreshToken

                Log.d("LoginViewModel", "자동 로그인 성공 → 새로운 토큰 저장 완료")

                onSuccess()

            } catch (e: Exception) {
                Log.e("LoginViewModel", "자동 로그인 실패: ${e.message}", e)

                // 자동 로그인 실패 → 토큰 삭제
                authPreference.accessToken = null
                authPreference.refreshToken = null
                authPreference.userId = null

                onFail()
            }
        }
    }
}

