package com.example.login.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.api.LoginResponse
import com.example.core.domain.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResponse?>(null)
    val loginState: StateFlow<LoginResponse?> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = loginRepository.login(email, password)
                _loginState.value = response   // UI에서 감지
            } catch (e: Exception) {
                _loginState.value = null
            }
        }
    }
}
