package com.example.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.UserInfo
import com.example.core.repository.UserRepository
import com.example.core.session.SessionStore
import com.example.data.preference.AuthPreference
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
    private val sessionStore: SessionStore, //세션 스토어 추가.
    private val authPreference: AuthPreference
): ViewModel() {

    // 별도 로딩(api 호출 없이) 로컬에 저장된 데이터 바로 보여줌.
    val sessionState: StateFlow<SessionStore.SessionSnapshot> = sessionStore.session
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionStore.SessionSnapshot(false, null,
                null, null, null, null, null, null, null, null,
                    emptyList(), emptyList() )
        )

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

    // 마이페이지 진입 시 최신 정보 갱신 용도로 사용함.
    fun refreshUserInfo() {
        val id = authPreference.userId ?: return
        viewModelScope.launch {
            runCatching {
                userRepository.getUserInfo(id)
            }.onFailure { e ->
                Log.e("MyPageViewModel", "데이터 동기화 실패: ${e.message}")
            }
            // 따로 상태를 업데이트할 필요가X.
            // UserRepositoryImpl 내부의 .also 블록이 세션을 업데이트하면
            // 위 1번의 sessionState가 자동으로 UI를 갱신합니다.
        }
    }

    // 마이페이지 계정 정보 수정
    fun updateUserInfo(
        nickname: String,
        jobId: Long,
        jobName: String,  //UI 즉시 반영을 위해 추가
        purposes: List<String>,
        interests: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1) DB 변경 : UserRepository를 통해 PATCH /api/users/profile API 호출
                val success = userRepository.updateUserInfo(nickname, jobId, purposes, interests)
                if (success) {
                    // 다시 fetch 해서 최신 데이터 반영
                    //loadUserInfo()

                    // 서버 성공 시(DB 변경 성공시) 세션만 즉시 업데이트(ui 자동 갱신)
                    sessionStore.updateProfile(nickname, jobId, jobName, purposes, interests)
                    onSuccess()
                } else {
                    onError("변경에 실패했습니다.")
                }
            } catch (e: Exception) {
                onError("API 호출 실패: ${e.message}")
            }
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

                // 토큰/세션 정리
                authPreference.clear()
                sessionStore.clear()

                onSuccess()
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "❌ 회원 탈퇴 실패: ${e.message}")
                onError("회원 탈퇴에 실패했습니다.")
            }
        }
    }

    // 로그아웃 */
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.logout()
                onSuccess()
            } catch (e: Exception) {
                onError("로그아웃에 실패했습니다.")
            }
        }
    }
//        fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
//            viewModelScope.launch {
//                try {
//                    userRepository.logout() // 서버 로그아웃 + 토큰/유저ID 정리까지 Repository에서 처리
//                    _uiState.value = UiState() // 마이페이지 상태 초기화
//                    onSuccess()
//                } catch (e: Exception) {
//                    onError("로그아웃에 실패했습니다.")
//                }
//            }
//        }
}