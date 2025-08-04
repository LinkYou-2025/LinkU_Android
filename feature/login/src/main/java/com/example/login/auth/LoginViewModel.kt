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

                //  Compose 변경 감지를 위해 새 객체로 복사
                _loginState.value = LoginResult(
                    userId = result.userId,
                    token = result.token,
                    status = result.status,
                    inactiveDate = result.inactiveDate
                )

                // 디버깅 로그 추가 (emit 직후)
                Log.d("LoginVM", "emit loginState userId=${result.userId} token=${result.token}")

                _isInactiveAccount.value = (result.status == "INACTIVE")
            } catch (e: Exception) {
                _loginState.value = null
                _isInactiveAccount.value = false
                Log.e("LoginVM", "login 실패", e) // 실패 로그도 추가
            }
        }
    }
}
