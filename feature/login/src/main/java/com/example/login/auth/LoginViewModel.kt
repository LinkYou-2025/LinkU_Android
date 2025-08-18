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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserRepository
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
                // UserRepositoryImpl.login()은 성공 시 LoginResult 리턴, 실패 시 예외 throw
                val res: LoginResult = repo.login(email, password)

                // 성공: 결과 반영 (Repo에서 토큰/유저ID 저장은 이미 수행됨)
                _loginState.value = LoginState(
                    loading = false,
                    result = res
                )
            } catch (e: HttpException) {
                // 서버에서 인증 실패 401/403 등 내려줄 때
                _loginState.value = LoginState(
                    loading = false,
                    errorTag = when (e.code()) {
                        401, 403 -> "INVALID_CREDENTIALS"
                        else -> "SERVER_ERROR"
                    }
                )
            } catch (_: IllegalStateException) {
                // Repo에서 "로그인 실패: ..." 로 throw 한 경우 (자격 증명 오류로 간주)
                _loginState.value = LoginState(
                    loading = false,
                    errorTag = "INVALID_CREDENTIALS"
                )
            } catch (_: Exception) {
                _loginState.value = LoginState(
                    loading = false,
                    errorTag = "SERVER_ERROR"
                )
            }
        }
    }
}
//
//@HiltViewModel
//class LoginViewModel @Inject constructor(
//    private val userRepository: UserRepository,
//    //private val authPreference: AuthPreference
//) : ViewModel() {
//
//    private val _loginState = MutableStateFlow<LoginResult?>(null)
//    val loginState: StateFlow<LoginResult?> = _loginState
//
//    //탈퇴 예정 계정 여부 상태
//    private val _isInactiveAccount = MutableStateFlow(false)
//    val isInactiveAccount: StateFlow<Boolean> = _isInactiveAccount
//
//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            try {
//                val result = userRepository.login(email, password)
//
////                // ✅ 로그인 성공 → userId를 영속 저장
////                val id = result.userId
////                if (id != null && id > 0) {
////                    authPreference.userId = id.toLong()
////                } else {
////                    authPreference.userId = null
////                }
//
//                //  Compose 변경 감지를 위해 새 객체로 복사
//                _loginState.value = LoginResult(
//                    userId = result.userId,
//                    token = result.token,
//                    status = result.status,
//                    inactiveDate = result.inactiveDate
//                )
//
//                Log.d("LoginVM", " emit loginState userId=${result.userId}, token=${result.token}")
//                _isInactiveAccount.value = (result.status == "INACTIVE")
//
//            } catch (e: retrofit2.HttpException) {
//                val errorBody = e.response()?.errorBody()?.string()
//                Log.e("LoginVM", " HTTP ${e.code()} 로그인 실패\nMessage: ${e.message()}\nErrorBody: $errorBody")
//
//
//                _loginState.value = null
//
//                _loginState.value = LoginResult(
//                    userId = -1,
//                    token = "",
//                    status = "ERROR_HTTP_${e.code()}",
//                    inactiveDate = null
//                )
//                _isInactiveAccount.value = false
//
//            } catch (e: Exception) {
//                Log.e("LoginVM", " 로그인 실패(기타 예외): ${e.localizedMessage}", e)
//
//                _loginState.value = null
//                _loginState.value = LoginResult(
//                    userId = -1,
//                    token = "",
//                    status = "ERROR",
//                    inactiveDate = null
//                )
//                _isInactiveAccount.value = false
//            }
//        }
//    }
//}
