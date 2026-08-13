package com.linku.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.UserSession
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.UserRepository
import com.linku.core.usecase.LogoutUseCase
import com.linku.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val alarmRepository: AlarmRepository,
    private val authPreference: AuthPreference,
    private val logoutUseCase: LogoutUseCase,
): ViewModel() {

    val sessionState: StateFlow<UserSession> = authPreference.sessionState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSession()
        )

    // 마이페이지 ui 상태
    data class MyPageUiState(
        val isLoading: Boolean = false,
        val userInfo: UserInfo? = null,
        val isUnreadAlarmExists: Boolean = false,
        val error: String? = null,
        // 서버 응답 오기 전까지 헤더에 즉시 보여줄 로컬 캐시 닉네임.
        val cachedNickname: String? = null
    )

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authPreference.getCachedNickname()?.takeIf { it.isNotBlank() }?.let { cached ->
                _uiState.value = _uiState.value.copy(cachedNickname = cached)
            }
        }
    }

    fun checkUnreadAlarm() {
        viewModelScope.launch {
            alarmRepository.getUnreadAlarmExists()
                .onSuccess { exists ->
                    _uiState.value = _uiState.value.copy(isUnreadAlarmExists = exists)
                }
        }
    }

    // 마이페이지 진입 시, api 로든
    fun loadUserInfo() {
        viewModelScope.launch {
            val id = authPreference.getUserId()
            if (id == null || id <= 0L) {
                _uiState.value = MyPageUiState(error = "로그인이 필요합니다.")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            userRepository.getUserInfo(id).fold(
                onSuccess = { info ->
                    _uiState.value = _uiState.value.copy(isLoading = false, userInfo = info, error = null)
                },
                onFailure = { e ->
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = e.message ?: "마이페이지 조회 실패")
                }
            )
        }
    }

    // 회원 탈퇴
    fun leaveUser(
        reason: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("MyPageViewModel", "🚀 회원 탈퇴 중...")
            userRepository.deleteUser(reason).fold(
                onSuccess = {
                    Log.d("MyPageViewModel", "✅ 회원 탈퇴 성공")
                    onSuccess()
                },
                onFailure = { e ->
                    Log.e("MyPageViewModel", "❌ 회원 탈퇴 실패")
                    onError("회원 탈퇴에 실패했습니다: ${e.message}")
                }
            )
        }
    }

    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
                .fold(
                    onSuccess = {
                        _uiState.value = MyPageUiState()
                        onSuccess()
                    },
                    onFailure = { onError("로그아웃에 실패했습니다.") }
                )
        }
    }

}
