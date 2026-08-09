package com.linku.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.error.AppError
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.NicknameCheckState
import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.mypage.intent.EditUserInfoIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 회원 정보 수정(내 정보 수정 / 맞춤정보 설정) 화면 전용 뷰모델.
// 마이페이지 메인 화면 상태(MyPageViewModel)와 분리해 수정 로직만 담당함.
@HiltViewModel
class MyPageEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference
) : ViewModel() {

    companion object {
        private const val NICKNAME_DEBOUNCE_TIME = 500L
    }

    data class EditUserInfoUiState(
        val isLoading: Boolean = false,
        val userInfo: UserInfo? = null,
        val error: String? = null,
        val nicknameCheckState: NicknameCheckState = NicknameCheckState.Idle
    )

    private val _uiState = MutableStateFlow(EditUserInfoUiState())
    val uiState: StateFlow<EditUserInfoUiState> = _uiState.asStateFlow()

    // 회원 정보 수정 화면 전용 draft 상태 (EditUserInfoIntent로만 갱신됨 - MVI)
    data class EditUserInfoState(
        val nickname: String = "",
        val jobId: Long = 0L,
        val purposes: List<String> = emptyList(),
        val interests: List<String> = emptyList()
    )

    private val _editUserInfoState = MutableStateFlow(EditUserInfoState())
    val editUserInfoState: StateFlow<EditUserInfoState> = _editUserInfoState.asStateFlow()

    // 닉네임 중복 체크 파이프라인(과잉 호출되지 않도록) - SignUpViewModel과 동일한 패턴
    private val nicknameQuery = MutableStateFlow("")

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            nicknameQuery
                .debounce(NICKNAME_DEBOUNCE_TIME)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query -> checkNickname(query) }
        }
    }

    // 회원 정보 수정 화면 진입 시, 최신 값 조회 + draft 초기화
    fun loadUserInfo() {
        viewModelScope.launch {
            val id = authPreference.getUserId()
            if (id == null || id <= 0L) {
                _uiState.value = EditUserInfoUiState(error = "로그인이 필요합니다.")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            userRepository.getUserInfo(id).fold(
                onSuccess = { info ->
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, userInfo = info, error = null)
                    resetEditUserInfo(info)
                },
                onFailure = { e ->
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = e.message ?: "사용자 정보 조회 실패")
                }
            )
        }
    }

    // 닉네임 입력 변경 - 원래 닉네임과 같으면(변경 없음/원복) 중복 체크를 건너뜀
    fun onNicknameChanged(nickname: String, originalNickname: String) {
        if (nickname == originalNickname) {
            _uiState.value = _uiState.value.copy(nicknameCheckState = NicknameCheckState.Idle)
            return
        }

        _uiState.value = _uiState.value.copy(nicknameCheckState = NicknameCheckState.Idle)

        if (nickname.isNotBlank()) {
            nicknameQuery.value = nickname
        }
    }

    private fun checkNickname(nickname: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(nicknameCheckState = NicknameCheckState.Checking)

            authRepository.checkNickname(nickname).fold(
                onSuccess = {
                    _uiState.value =
                        _uiState.value.copy(nicknameCheckState = NicknameCheckState.Available)
                },
                onFailure = { exception ->
                    val appError = exception as? AppError
                        ?: ApiError.Unknown(exception.message ?: "알 수 없는 오류가 발생했습니다.")
                    val nextState = when (appError) {
                        is ApiError.User.DuplicateNickname -> NicknameCheckState.Duplicated
                        else -> NicknameCheckState.Error(appError.displayMessage)
                    }
                    _uiState.value = _uiState.value.copy(nicknameCheckState = nextState)
                    Log.e("MyPageEditViewModel", "닉네임 중복 체크 실패", exception)
                }
            )
        }
    }

    private fun resetEditUserInfo(info: UserInfo) {
        _editUserInfoState.value = EditUserInfoState(
            nickname = info.nickname,
            jobId = info.jobId,
            purposes = info.purposes,
            interests = info.interests
        )
    }

    // 회원 정보 수정 draft 갱신 (MVI reducer)
    fun onEditUserInfoIntent(intent: EditUserInfoIntent) {
        _editUserInfoState.update { current ->
            when (intent) {
                is EditUserInfoIntent.UpdateNickname -> current.copy(nickname = intent.nickname)
                is EditUserInfoIntent.UpdateJobId -> current.copy(jobId = intent.jobId)
                is EditUserInfoIntent.UpdatePurpose -> current.copy(
                    purposes = current.purposes.toggle(intent.purpose.displayName)
                )

                is EditUserInfoIntent.UpdateInterest -> current.copy(
                    interests = current.interests.toggle(intent.interest.displayName)
                )
            }
        }
    }

    private fun List<String>.toggle(value: String): List<String> =
        if (contains(value)) this - value else this + value

    // 회원 정보 수정 - PATCH이므로 draft가 원본과 하나라도 다를 때만 서버에 요청함
    fun editUserInfo(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val original = _uiState.value.userInfo ?: return
        val draft = _editUserInfoState.value

        val isChanged = draft.nickname != original.nickname ||
                draft.jobId != original.jobId ||
                draft.purposes.toSet() != original.purposes.toSet() ||
                draft.interests.toSet() != original.interests.toSet()

        if (!isChanged) {
            onSuccess()
            return
        }

        viewModelScope.launch {
            userRepository.updateUserInfo(
                nickname = draft.nickname,
                jobId = draft.jobId,
                purposes = draft.purposes,
                interests = draft.interests
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
}
