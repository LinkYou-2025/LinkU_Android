package com.linku.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.LoginResult
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.NicknameCheckState
import com.linku.core.model.auth.Purpose
import com.linku.core.repository.AuthRepository
import com.linku.data.preference.AuthPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * SocialAuthViewModel
 *
 * 소셜 로그인 이후 TEMP 유저가
 * 닉네임 / 성별 / 직업 / 목적 / 관심사를 입력하는 전용 ViewModel
 *
 *  이메일 / 비밀번호 없음
 *  SessionStore 직접 접근 안 함
 *  마지막 단계에서 completeSocialProfile API 호출
 */


@HiltViewModel
class SocialAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference
) : ViewModel() {

    companion object {
        private const val TAG = "SocialAuthViewModel"
        private const val NICKNAME_DEBOUNCE_TIME = 500L
        private const val MAX_NICKNAME_LENGTH = 6
    }

    sealed class SocialLoginState {
        object Idle : SocialLoginState() // 아무것도 하지 않는 초기상태.
        object Loading : SocialLoginState() // 로딩 중
        data class Success(val result: LoginResult) :
            SocialLoginState() //loginResult에서 성공 + 데이터 가져옴.

        data class Error(val message: String) : SocialLoginState()
    }

    // kakao 로그인 stateflow
    private val _kakaoLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Idle)
    val kakaoLoginState: StateFlow<SocialLoginState> = _kakaoLoginState.asStateFlow()


    // 구글 로그인 stateflow
    private val _googleLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Idle)
    val googleLoginState: StateFlow<SocialLoginState> = _googleLoginState.asStateFlow()

    //reset 함수 추가
    fun resetKakaoLoginState() {
        _kakaoLoginState.value = SocialLoginState.Idle
    }

    fun resetGoogleLoginState() {
        _googleLoginState.value = SocialLoginState.Idle
    }


    // 입력 상태
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _nicknameCheckState =
        MutableStateFlow<NicknameCheckState>(NicknameCheckState.Idle)
    val nicknameCheckState: StateFlow<NicknameCheckState> = _nicknameCheckState

    private val nicknameQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            nicknameQuery
                .debounce(NICKNAME_DEBOUNCE_TIME)
                .distinctUntilChanged()
                .filter { isValidNickname(it) }
                .collect { query ->
                    checkNicknameInternal(query)
                }
        }
    }

    private val _gender = MutableStateFlow(Gender.NONE)
    val gender: StateFlow<Gender> = _gender

    private val _job = MutableStateFlow(Job.NONE)
    val job: StateFlow<Job> = _job

    private val _purposes = MutableStateFlow<List<Purpose>>(emptyList())
    val purposes: StateFlow<List<Purpose>> = _purposes

    private val _interests = MutableStateFlow<List<Interest>>(emptyList())
    val interests: StateFlow<List<Interest>> = _interests


    // 로딩, 성공, 에러
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error


    private fun isValidNickname(input: String): Boolean =
        input.isNotBlank() && input.length in 1..MAX_NICKNAME_LENGTH

    //닉네임 중복 체크
    private fun checkNicknameInternal(nickname: String) {
        viewModelScope.launch {
            try {
                _nicknameCheckState.value = NicknameCheckState.Checking
                authRepository.checkNickname(nickname)
                // 여기까지 왔다 = 성공
                _nicknameCheckState.value = NicknameCheckState.Available
            } catch (e: Exception) {
                Log.e(TAG, "닉네임 중복 체크 실패", e)
                _nicknameCheckState.value = NicknameCheckState.Error(
                    e.message ?: "닉네임 확인 중 오류가 발생했습니다."
                )
            }
        }
    }

    // 카카오로 로그인하기
    fun loginWithKakao(token: String) {
        Log.d("SocialAuthViewModel", "loadKakaoLogin")

        viewModelScope.launch {
            Log.d("SocialAuthViewModel", "loadWithKakao launch")

            _kakaoLoginState.value = SocialLoginState.Loading

            try {
                val result = authRepository.loginWithKakao(token)

                if (result.refreshToken == null) {
                    // TEMP 유저 → 토큰 저장 없이 소셜 토큰만 임시 보관
                    authPreference.socialToken = result.accessToken
                    _kakaoLoginState.value = SocialLoginState.Success(result)
                } else {
                    // 정상 로그인 → 토큰 저장
                    authPreference.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken,
                        userId = result.userId
                    )
                    _kakaoLoginState.value = SocialLoginState.Success(result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "loadWithKakao catch: ${e.message}")
                _kakaoLoginState.value = SocialLoginState.Error(e.message ?: "카카오 로그인 실패")
            }

            Log.d("SocialAuthViewModel", "loadWithKakao end")
        }
        Log.d("SocialAuthViewModel", "loadWithKakao return")
    }

    // TODO : 이 양식에 맞춰서 로그인, 회원가입 api 변경된 부분 에러처리 수정해야함.
    sealed interface GoogleLoginStatus {
        fun work(
            authPreference: AuthPreference,
            updateState: (SocialLoginState) -> Unit
        )

        data object Idle : GoogleLoginStatus {
            override fun work(
                authPreference: AuthPreference,
                updateState: (SocialLoginState) -> Unit
            ) = Unit
        }

        data class Success(val result: LoginResult) : GoogleLoginStatus {
            override fun work(
                authPreference: AuthPreference,
                updateState: (SocialLoginState) -> Unit
            ) {
                if (result.refreshToken == null) {
                    authPreference.socialToken = result.accessToken
                } else {
                    authPreference.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken,
                        userId = result.userId
                    )
                }
                updateState(SocialLoginState.Success(result))
            }
        }

        data class Error(val message: String) : GoogleLoginStatus {
            override fun work(
                authPreference: AuthPreference,
                updateState: (SocialLoginState) -> Unit
            ) {
                updateState(SocialLoginState.Error(message))
            }
        }
    }

    val googleLoginStatus = MutableStateFlow<GoogleLoginStatus>(GoogleLoginStatus.Idle)

    // 구글로 로그인하기
    suspend fun loginWithGoogle(token: String) {
        _googleLoginState.value = SocialLoginState.Loading

        authRepository.loginWithGoogle(token).fold(
            onSuccess = {
                GoogleLoginStatus.Success(it).work(authPreference) { _googleLoginState.value = it }
            },
            onFailure = {
                GoogleLoginStatus.Error(it.message ?: "구글 로그인 실패")
                    .work(authPreference) { _googleLoginState.value = it }
            }
        )
    }
    /*fun loginWithGoogle(token: String) : GoogleLoginStatus{
        Log.d("SocialAuthViewModel", "loadGoogleLogin")

        googleLoginStatus.value = GoogleLoginStatus.Idle

        viewModelScope.launch {
            Log.d("SocialAuthViewModel", "loadWithGoogle launch")

            _googleLoginState.value = SocialLoginState.Loading

            val result = authRepository.loginWithGoogle(token).fold(
                onSuccess = { googleLoginStatus.value = GoogleLoginStatus.Success },
                onFailure = { googleLoginStatus.value = GoogleLoginStatus.Error }
            )

            /*try {
                Log.d("SocialAuthViewModel", "loadWithGoogle try")
                val result = authRepository.loginWithGoogle(token).fold(
                    onSuccess = { googleLoginStatus.value = GoogleLoginStatus.Success },
                    onFailure = { googleLoginStatus.value = GoogleLoginStatus.Error }
                )
                authPreference.saveTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    userId = result.userId
                )

                _googleLoginState.value = SocialLoginState.Success(result)
            } catch (e: Exception) {
                Log.d(TAG, "loadWithGoogle catch: ${e.message}")
                _googleLoginState.value = SocialLoginState.Error(e.message ?: "구글 로그인 실패")
            }*/

            Log.d("SocialAuthViewModel", "loadWithGoogle end")
        }
        Log.d("SocialAuthViewModel", "loadWithGoogle return")

        return googleLoginStatus.value
    }*/


    fun updateNickname(input: String) {
        if (_nickname.value == input) return
        _nickname.value = input

        if (isValidNickname(input)) {
            nicknameQuery.value = input
        } else {
            _nicknameCheckState.value = NicknameCheckState.Idle
        }
    }

    fun updateGender(value: Gender) {
        _gender.value = value
    }

    fun updateJob(value: Job) {
        _job.value = value
    }

    fun updatePurposes(values: List<Purpose>) {
        _purposes.value = values
    }

    fun updateInterests(values: List<Interest>) {
        _interests.value = values
    }

    fun clearError() {
        _error.value = null
    }


    //소셜 프로필 완료 API
    fun completeSocialProfile(
        socialToken: String,
        onSuccess: () -> Unit
    ) {
        // 기본 검증 (UI에서도 하지만 여기서 한 번 더)
        if (_nickname.value.isBlank()) {
            Log.w(TAG, "닉네임이 비어 있음")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d(TAG, "소셜 프로필 완료 API 호출 시작")

                val success = authRepository.completeSocialProfile(
                    socialToken = socialToken,
                    nickName = _nickname.value,
                    gender = _gender.value,
                    job = _job.value,
                    purposes = _purposes.value,
                    interests = _interests.value
                )

                if (success) {
                    Log.d(TAG, "소셜 프로필 완료 성공")
                    onSuccess()
                } else {
                    Log.e(TAG, "소셜 프로필 완료 실패 (서버 반환 false)")
                    _error.value = IllegalStateException("프로필 저장에 실패했습니다.")
                }

            } catch (e: Exception) {
                Log.e(TAG, "소셜 프로필 완료 실패: ${e.message}")
                _error.value = e
            } finally {
                _isLoading.value = false
            }
        }
    }
}