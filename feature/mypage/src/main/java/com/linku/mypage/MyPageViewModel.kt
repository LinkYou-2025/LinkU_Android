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

    // Eagerly: 로그인 직후(마이페이지 탭 진입 전) MainApp 생성 시점부터 미리 구독해 둠.
    // WhileSubscribed였을 때는 마이페이지 화면에 실제로 진입해야 구독이 시작돼,
    // 첫 프레임엔 기본값(loginType = NONE)이 그려졌다가 실제 값으로 바뀌며 아이콘이 지직거리는 문제가 있었음.
    // 여기서 설계에서 고민한 점이 값이 자주 안 바뀌니까 굳이 관찰해야하나 고민했으나, 뷰모델이 로그인, 재로그인 사이에도 살아 남을 수 있음(메인 액티비티 1개 구조)
    // 그래서 반응형으로 구현함. 어차피 값이 바뀔 때만 사용하기에 리스크 비용이 크기 않아 보입니다:)
    val sessionState: StateFlow<UserSession> = authPreference.sessionState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserSession()
        )

    // 마이페이지 ui 상태
    data class MyPageUiState(
        val isLoading: Boolean = false,
        val userInfo: UserInfo? = null,
        val error: String? = null,
        // 서버 응답 오기 전까지 헤더에 즉시 보여줄 로컬 캐시 닉네임.
        // 이메일은 소셜 로그인 응답에 아예 안 내려오는 값이라 캐싱 대상에서 제외.
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

    // 마이페이지 진입 시, api 로든
    fun loadUserInfo() {
        viewModelScope.launch {
            val id = authPreference.getUserId()
            if (id == null || id <= 0L) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "로그인이 필요합니다.")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            userRepository.getUserInfo(id).fold(
                onSuccess = { info ->
                    _uiState.value = _uiState.value.copy(isLoading = false, userInfo = info)
                },
                onFailure = { e ->
                    // .copy()로 실패 시에도 마지막으로 성공했던 userInfo를 유지 - 새 MyPageUiState(...)로
                    // 통째로 교체하면 재조회 실패 한 번에 이미 잘 보이던 닉네임/이메일이 null로 사라짐.
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "마이페이지 조회 실패"
                    )
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

    // 로그아웃 */
    // TODO: FCM토큰 삭제 API호출
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.logout()
            if (success) {
                _uiState.value = MyPageUiState()
                onSuccess()
            } else {
                onError("로그아웃에 실패했습니다.")
            }
        }
    }

}