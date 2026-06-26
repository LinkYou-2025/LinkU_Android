package com.linku.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.UserSession
import com.linku.core.repository.UserRepository
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
    private val authPreference: AuthPreference
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
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

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
                    _uiState.value = MyPageUiState(isLoading = false, userInfo = info)
                },
                onFailure = { e ->
                    _uiState.value =
                        MyPageUiState(isLoading = false, error = e.message ?: "마이페이지 조회 실패")
                }
            )
        }
    }

//    data class UiState(
//        val isLoading: Boolean = false,
//        val userInfo: UserInfo? = null,
//        val error: String? = null
//    )
//
//    private val _uiState = MutableStateFlow(UiState(isLoading = true))
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
//
//    // 마이페이지 조회
//    private val _userInfo = MutableStateFlow<UserInfo?>(null)
//    val userInfo: StateFlow<UserInfo?> = _userInfo
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    // 마이페이지 조회
//    fun loadUserInfo() {
//        val id = authPreference.userId
//        if (id == null || id <= 0L) {
//            _uiState.value = UiState(
//                isLoading = false,
//                userInfo = null,
//                error = "로그인이 필요합니다."
//            )
//            return
//        }
////        viewModelScope.launch {
////            _isLoading.value = true
////            _error.value = null
////            runCatching { userRepository.getUserInfo(userId) }
////                .onSuccess { _userInfo.value = it }
////                .onFailure { _error.value = it.message ?: "마이페이지 조회 실패" }
////            _isLoading.value = false
////        }
//        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
//        viewModelScope.launch {
//            runCatching { userRepository.getUserInfo(id) }
//                .onSuccess { info ->
//                    _uiState.value = UiState(isLoading = false, userInfo = info)
//                }
//                .onFailure { e ->
//                    _uiState.value = UiState(isLoading = false, error = e.message ?: "마이페이지 조회 실패")
//                }
//        }
//    }


    // 마이페이지 계정 정보 수정
    fun updateUserInfo(
        nickname: String,
        jobId: Long,
        jobName: String,
        purposes: List<String>,
        interests: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.updateUserInfo(
                nickname = nickname,
                jobId = jobId,
                purposes = purposes,
                interests = interests
            ).fold(
                onSuccess = {
                    onSuccess()
                    loadUserInfo()
                },
                onFailure = { e ->
                    onError("변경에 실패했습니다: ${e.message}")
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
            try {
                Log.d("MyPageViewModel", "🚀 회원 탈퇴 중...")
                userRepository.deleteUser(reason)
                Log.d("MyPageViewModel", "✅ 회원 탈퇴 성공")


                onSuccess()
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "❌ 회원 탈퇴 실패: ${e.message}")
                onError("회원 탈퇴에 실패했습니다.")
            }
        }
    }

    // 로그아웃 */
    // TODO: FCM토큰 삭제 API호출
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _uiState.value = MyPageUiState()
                onSuccess()
            } catch (e: Exception) {
                onError("로그아웃에 실패했습니다.")
            }
        }
    }

}