package com.example.login.auth



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.LoginResult
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState: StateFlow<LoginResult?> = _loginState

    //탈퇴 예정 계정 여부 상태
    private val _isInactiveAccount = MutableStateFlow(false)
    val isInactiveAccount: StateFlow<Boolean> = _isInactiveAccount

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.login(email, password)

                _loginState.value = LoginResult(
                    userId = result.userId,
                    token = result.token,
                    status = result.status,
                    inactiveDate = result.inactiveDate
                )

                Log.d("LoginVM", " emit loginState userId=${result.userId}, token=${result.token}")
                _isInactiveAccount.value = (result.status == "INACTIVE")

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("LoginVM", " HTTP ${e.code()} 로그인 실패\nMessage: ${e.message()}\nErrorBody: $errorBody")


                _loginState.value = null

                _loginState.value = LoginResult(
                    userId = -1,
                    token = "",
                    status = "ERROR_HTTP_${e.code()}",
                    inactiveDate = null
                )
                _isInactiveAccount.value = false

            } catch (e: Exception) {
                Log.e("LoginVM", " 로그인 실패(기타 예외): ${e.localizedMessage}", e)

                _loginState.value = null
                _loginState.value = LoginResult(
                    userId = -1,
                    token = "",
                    status = "ERROR",
                    inactiveDate = null
                )
                _isInactiveAccount.value = false
            }
        }
    }
}
