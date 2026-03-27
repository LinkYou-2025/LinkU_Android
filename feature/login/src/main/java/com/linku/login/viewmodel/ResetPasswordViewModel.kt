package com.linku.login.viewmodel

//유저 비밀번호 재설정 기능 수정으로 리펙X

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


//여기 api 전면 수정 예정. 실제 api 연동은 1월 말~ 2월 초
data class ResetPwUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ResetPwUiState())
    val ui: StateFlow<ResetPwUiState> = _ui

//    fun request(email: String) {
//        viewModelScope.launch {
//            _ui.value = ResetPwUiState(loading = true)
//            val ok = repo.requestTempPassword(email)
//            _ui.value = if (ok) {
//                ResetPwUiState(loading = false, success = true)
//            } else {
//                ResetPwUiState(loading = false, success = false, error = "전송에 실패했어요. 이메일을 확인하고 다시 시도해 주세요.")
//            }
//        }
//    }

    fun consumeSuccess() {
        _ui.value = _ui.value.copy(success = false)
    }

    fun consumeError() {
        _ui.value = _ui.value.copy(error = null)
    }
}