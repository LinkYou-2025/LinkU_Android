package com.linku.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.model.LoginResult
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.NicknameCheckState
import com.linku.core.model.auth.Purpose
import com.linku.core.repository.AuthRepository
import com.linku.core.repository.UserRepository
import com.linku.data.preference.AuthPreference
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.SocialAuthUiEffect
import com.linku.login.viewmodel.state.SocialAuthUiState
import com.linku.login.viewmodel.state.SocialLoginForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 소셜 로그인(카카오, 구글) 통합 세션을 제어하고, TEMP 회원의 온보딩 필수 프로필 설정 폼을 제어하는
 * LinkU 표준 MVI 아키텍처 기반의 비즈니스 뷰모델입니다.
 *
 * @property authRepository 세션 인가 코드 검증 및 성향 데이터 저장을 대행하는 도메인 인증 저장소 계층입니다.
 * @property authPreference 로컬 디바이스 보안 프리퍼런스 세션 저장소 제어 장치입니다.
 */
@HiltViewModel
class SocialAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val authPreference: AuthPreference
) : ViewModel(),
    MviContainer<SocialAuthUiState, SocialAuthUiEffect> by mviContainer(SocialAuthUiState()) {

    companion object {
        private const val TAG = "SocialAuthViewModel"
        private const val NICKNAME_DEBOUNCE_TIME = 500L
        private const val MAX_NICKNAME_LENGTH = 6
    }

    /** 닉네임 실시간 중복 체크의 과도한 트래픽 분산을 제어하는 내부 관찰 스트림 백킹 필드입니다. */
    private val nicknameQuery = MutableStateFlow("")

    /** 가입 최종 트랜잭션 확정 시 어떤 채널로 연동했는지 동기화하기 위한 소셜 공급자 유형 플래그입니다. */
    private var currentSocialProvider: LoginType = LoginType.NONE

    init {
        observeNicknameQuery()
        loadRecentLoginType()
    }

    /**
     * 로그인 화면에 "최근 로그인" 말풍선을 표시하기 위해 마지막으로 로그인했던 수단을 불러옵니다.
     */
    private fun loadRecentLoginType() {
        viewModelScope.launch {
            val recentLoginType = authPreference.getLoginType()
            updateState { copy(recentLoginType = recentLoginType) }
        }
    }

    private fun updateForm(update: (SocialLoginForm) -> SocialLoginForm) {
        updateState {
            copy(socialLoginForm = update(socialLoginForm))
        }
    }

    fun setAgreeAll(v: Boolean) =
        updateForm { it.copy(agreeTerms = v, agreePrivacy = v, agreeMarketing = v) }

    fun setAgreeTerms(agree: Boolean) = updateForm { it.copy(agreeTerms = agree) }
    fun setAgreePrivacy(agree: Boolean) = updateForm { it.copy(agreePrivacy = agree) }
    fun setAgreeMarketing(agree: Boolean) = updateForm { it.copy(agreeMarketing = agree) }

    /**
     * 카카오 인증 플랫폼 토큰 세션을 기반으로 원격 엔드포인트에 소셜 로그인을 요청합니다.
     */
    fun loginWithKakao(token: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            Log.d(TAG, "[카카오 로그인 트랜잭션 수립]")

            authRepository.loginWithKakao(token).foldApp(
                onSuccess = { result ->
                    currentSocialProvider = LoginType.KAKAO
                    updateState { copy(isLoading = false) }
                    handleLoginRoute(result)
                },
                onFailure = { error ->
                    Log.e(TAG, "카카오 로그인 실패: ${error.displayMessage}", error)
                    updateState { copy(isLoading = false, error = error.displayMessage) }
                    postSideEffect(SocialAuthUiEffect.ShowToast(error.displayMessage))
                }
            )
        }
    }

    /**
     * 구글 고유 인가 자격 명세를 기반으로 원격 엔드포인트에 소셜 로그인을 요청합니다.
     */
    fun loginWithGoogle(token: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            Log.d(TAG, "[구글 로그인 트랜잭션 수립]")

            authRepository.loginWithGoogle(token).foldApp(
                onSuccess = { result ->
                    currentSocialProvider = LoginType.GOOGLE
                    updateState { copy(isLoading = false) }
                    handleLoginRoute(result)
                },
                onFailure = { error ->
                    Log.e(
                        TAG,
                        "구글 로그인 실패: ${error.displayMessage}",
                        error
                    )
                    updateState { copy(isLoading = false, error = error.displayMessage) }
                    postSideEffect(SocialAuthUiEffect.ShowToast(error.displayMessage))
                }
            )
        }
    }

    /**
     * 인증 성공 후 전달받은 회원의 가입 영속 상태(TEMP / ACTIVE / INACTIVE)를 판별하여 화면 흐름(Side Effect)을 제어합니다.
     */
    private suspend fun handleLoginRoute(result: LoginResult) {
        when (result.status) {
            "TEMP" -> {
                Log.d(TAG, "신규 TEMP 회원 감지 -> 로컬 세션 초기화 및 온보딩 입력 플로우 지시")
                authPreference.clear()
                postSideEffect(SocialAuthUiEffect.NavigateToAdditionalInfo(result))
            }

            "INACTIVE" -> {
                // 탈퇴 유예기간 계정 -> 홈으로 보내지 않고 복구 여부를 묻는 모달을 띄움.
                Log.d(TAG, "탈퇴 유예기간 계정 감지 -> 복구 모달 노출")
                updateState { copy(showRecoverModal = true, pendingLoginResult = result) }
            }

            else -> {
                Log.d(TAG, "기존 ACTIVE 회원 감지 -> 로컬 토큰 자동 동기화 완료 상태로 홈 진입 지시")
                postSideEffect(SocialAuthUiEffect.NavigateToHome(result))
            }
        }
    }

    /**
     * 복구 모달에서 "계정 복구"를 선택했을 때 호출됨. 로그인 응답으로 이미 저장된
     * 복구 전용 accessToken을 이용해 `users/recover`를 호출하고, 성공하면 정상 로그인과
     * 동일하게 홈으로 이동시킴.
     */
    fun recoverAccount() {
        viewModelScope.launch {
            val pendingResult = state.value.pendingLoginResult
            val recovered = userRepository.recoverUser()
            updateState { copy(showRecoverModal = false, pendingLoginResult = null) }

            if (recovered && pendingResult != null) {
                Log.d(TAG, "계정 복구 성공")
                // 복구가 확정된 시점에만 정식 세션(LOGGED_IN=true)으로 저장함.
                authPreference.saveTokens(
                    accessToken = pendingResult.accessToken,
                    refreshToken = pendingResult.refreshToken,
                    userId = pendingResult.userId,
                    loginType = currentSocialProvider
                )
                postSideEffect(SocialAuthUiEffect.NavigateToHome(pendingResult))
            } else {
                Log.e(TAG, "계정 복구 실패 (유예기간 만료 등)")
                // 복구 실패 시 계정은 여전히 비활성 상태이므로 임시 저장된 세션을 정리함.
                authPreference.clear()
                postSideEffect(SocialAuthUiEffect.ShowToast("계정 복구에 실패했습니다. 다시 시도해주세요."))
            }
        }
    }

    /**
     * 복구 모달에서 "탈퇴 유지"를 선택했을 때 호출됨. 로그인 응답으로 임시 저장해둔
     * 세션(복구 전용 토큰)을 지우고 로그인 화면에 그대로 남김.
     */
    fun keepWithdrawn() {
        viewModelScope.launch {
            authPreference.clear()
            updateState { copy(showRecoverModal = false, pendingLoginResult = null) }
        }
    }

    /** 복구 모달을 순수 UI적으로만 닫음(외부 영역 클릭 등). 세션은 건드리지 않음. */
    fun dismissRecoverModal() {
        updateState { copy(showRecoverModal = false) }
    }

    /**
     * 닉네임의 문법 및 길이 유효성을 판단합니다. (SignUpViewModel과 동일 규격 규정)
     */
    private fun isValidNickname(input: String): Boolean {
        return input.isNotBlank() &&
                input.length in 1..MAX_NICKNAME_LENGTH &&
                input.matches(Regex("^[가-힣a-zA-Z]+$"))
    }

    /**
     * 사용자가 온보딩 입력 폼에서 닉네임 문자열을 수정할 때 호출되는 상태 변경 메서드입니다.
     */
    fun onNicknameChanged(nickname: String) {
        if (nickname == state.value.socialLoginForm.nickname) return

        val isValid = isValidNickname(nickname)

        updateForm { it.copy(nickname = nickname) }
        updateState { copy(nicknameCheckState = NicknameCheckState.Idle) }

        if (isValid) {
            nicknameQuery.value = nickname
        }
    }

    /**
     * 코루틴 디바운싱 연산 규칙에 입각하여 닉네임 입력 버퍼를 독립 관찰하는 수집 스트림 엔진입니다.
     */
    @OptIn(FlowPreview::class)
    private fun observeNicknameQuery() {
        viewModelScope.launch {
            nicknameQuery
                .debounce(NICKNAME_DEBOUNCE_TIME)
                .distinctUntilChanged()
                .filter { isValidNickname(it) }
                .collectLatest { query ->
                    checkNicknameInternal(query)
                }
        }
    }

    /**
     * 이메일 회원가입 양식과 완전히 동일하게 세부 에러 타입별 분기 처리를 진행하는 중복 검사 엔진입니다.
     */
    private suspend fun checkNicknameInternal(nickname: String) {
        updateState { copy(nicknameCheckState = NicknameCheckState.Checking) }

        authRepository.checkNickname(nickname).foldApp(
            onSuccess = {
                updateState { copy(nicknameCheckState = NicknameCheckState.Available) }
            },
            onFailure = { error ->
                val nextCheckState = when (error) {
                    is ApiError.User.DuplicateNickname -> NicknameCheckState.Duplicated
                    else -> NicknameCheckState.Error(error.displayMessage)
                }
                updateState { copy(nicknameCheckState = nextCheckState) }
                Log.e(TAG, "닉네임 중복 체크 실패", error)
            }
        )

    }

    /**
     * 성별 선택 컴포넌트의 상태가 변경되거나 확정될 때 단일 UiState를 동기화하는 메서드입니다.
     */
    fun onGenderChanged(gender: Gender) {
        updateForm { it.copy(gender = gender) }
        Log.d(TAG, "성별 선택 상태 업데이트 완료: $gender")
    }

    fun onJobChanged(value: Job) = updateForm { it.copy(job = value) }

    fun togglePurpose(purpose: Purpose) {
        updateForm { form ->
            val currentList = form.purposes
            val updatedList =
                if (currentList.contains(purpose)) currentList - purpose else currentList + purpose
            form.copy(purposes = updatedList)
        }
    }

    fun toggleInterest(interest: Interest) {
        updateForm { form ->
            val currentList = form.interests
            val updatedList =
                if (currentList.contains(interest)) currentList - interest else currentList + interest
            form.copy(interests = updatedList)
        }
    }

    fun clearError() = updateState { copy(error = null) }

    /**
     * 입력 수집된 온보딩 성향 프로필 폼 패키지를 취합하여 서버에 소셜 프로필 설정 완성 처리를 최종 확정 요청합니다.
     */
    fun completeSocialProfile(socialToken: String) {
        val form = state.value.socialLoginForm
        if (form.nickname.isBlank() || state.value.nicknameCheckState != NicknameCheckState.Available) {
            Log.w(TAG, "필수 가입 온보딩 명세 누락 우회 차단 예외 발생")
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            Log.d(TAG, "[소셜 온보딩 프로필 폼 주머니 기반 서버 동기화 트랜잭션 개시]")

            val termsMap = mapOf(
                "TERMS_OF_USE" to form.agreeTerms,
                "PRIVACY_POLICY" to form.agreePrivacy,
                "MARKETING" to form.agreeMarketing
            )

            authRepository.completeSocialProfile(
                socialToken = socialToken,
                nickName = form.nickname,
                gender = form.gender,
                job = form.job,
                purposes = form.purposes,
                interests = form.interests,
                termsMap = termsMap,
                loginType = currentSocialProvider
            ).foldApp(
                onSuccess = { isSuccess ->
                    updateState { copy(isLoading = false) }
                    if (isSuccess) {
                        // 응답 토큰으로 로그인 세션 저장(자동 로그인)까지 리포지토리에서 이미 끝난 상태.
                        Log.d(TAG, "소셜 프로필 동기화 전면 성공 - 자동 로그인 완료")
                        postSideEffect(SocialAuthUiEffect.CompleteProfileSuccess)
                    } else {
                        updateState { copy(error = "프로필 저장 처리에 실패하였습니다.") }
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "온보딩 프로필 전송 중 원격 예외 발생", error)
                    updateState { copy(isLoading = false, error = error.displayMessage) }
                }
            )
        }
    }

}

//    /**
//     * 카카오 인증 토큰 세션을 기반으로 서버 엔드포인트에 로그인을 시도합니다.
//     *
//     * @param token 카카오 인플랫폼 SDK 인증 완료 후 전달받은 액세스 토큰 문자열입니다.
//     */
//    fun loginWithKakao(token: String) {
//        Log.d("SocialAuthViewModel", "loadKakaoLogin")
//
//        viewModelScope.launch {
//            Log.d("SocialAuthViewModel", "loadWithKakao launch")
//
//            _kakaoLoginState.value = SocialLoginState.Loading
//
//            try {
//                val result = authRepository.loginWithKakao(token).getOrThrow()
//                currentSocialProvider = LoginType.KAKAO
//
//                if (result.refreshToken == null) {
//                    // [TEMP 유저] 가입이 안 된 신규 유저이므로, 토큰 저장 없이 프로필 입력 화면으로 진입 유도
//                    authPreference.clear()
////                    authPreference.socialToken = result.accessToken
//                    _kakaoLoginState.value = SocialLoginState.Success(result)
//                } else {
//                    authPreference.saveTokens(
//                        accessToken = result.accessToken,
//                        refreshToken = result.refreshToken,
//                        userId = result.userId,
//                        loginType = LoginType.KAKAO
//                    )
//                    _kakaoLoginState.value = SocialLoginState.Success(result)
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, "loadWithKakao catch: ${e.message}")
//                _kakaoLoginState.value = SocialLoginState.Error(e.message ?: "카카오 로그인 실패")
//            }
//
//            Log.d("SocialAuthViewModel", "loadWithKakao end")
//        }
//        Log.d("SocialAuthViewModel", "loadWithKakao return")
//    }
//
//    sealed class SocialLoginState {
//        object Idle : SocialLoginState() // 아무것도 하지 않는 초기상태.
//        object Loading : SocialLoginState() // 로딩 중
//        data class Success(val result: LoginResult) :
//            SocialLoginState() //loginResult에서 성공 + 데이터 가져옴.
//
//        data class Error(val message: String) : SocialLoginState()
//    }
//
//    // kakao 로그인 stateflow
//    private val _kakaoLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Idle)
//    val kakaoLoginState: StateFlow<SocialLoginState> = _kakaoLoginState.asStateFlow()
//
//
//    // 구글 로그인 stateflow
//    private val _googleLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Idle)
//    val googleLoginState: StateFlow<SocialLoginState> = _googleLoginState.asStateFlow()
//
//
//    //reset 함수 추가
//    fun resetKakaoLoginState() {
//        _kakaoLoginState.value = SocialLoginState.Idle
//    }
//
//    fun resetGoogleLoginState() {
//        _googleLoginState.value = SocialLoginState.Idle
//    }
//
//
//    // 입력 상태
//    private val _nickname = MutableStateFlow("")
//    val nickname: StateFlow<String> = _nickname
//
//    private val _nicknameCheckState =
//        MutableStateFlow<NicknameCheckState>(NicknameCheckState.Idle)
//    val nicknameCheckState: StateFlow<NicknameCheckState> = _nicknameCheckState
//
//
//    init {
//        viewModelScope.launch {
//            nicknameQuery
//                .debounce(NICKNAME_DEBOUNCE_TIME)
//                .distinctUntilChanged()
//                .filter { isValidNickname(it) }
//                .collect { query ->
//                    checkNicknameInternal(query)
//                }
//        }
//    }
//
//    private val _gender = MutableStateFlow(Gender.NONE)
//    val gender: StateFlow<Gender> = _gender
//
//    private val _job = MutableStateFlow(Job.NONE)
//    val job: StateFlow<Job> = _job
//
//    private val _purposes = MutableStateFlow<List<Purpose>>(emptyList())
//    val purposes: StateFlow<List<Purpose>> = _purposes
//
//    private val _interests = MutableStateFlow<List<Interest>>(emptyList())
//    val interests: StateFlow<List<Interest>> = _interests
//
//
//    // 로딩, 성공, 에러
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _error = MutableStateFlow<Throwable?>(null)
//    val error: StateFlow<Throwable?> = _error
//
//
//    private fun isValidNickname(input: String): Boolean =
//        input.isNotBlank() && input.length in 1..MAX_NICKNAME_LENGTH
//
//    //닉네임 중복 체크
//    private fun checkNicknameInternal(nickname: String) {
//        viewModelScope.launch {
//            try {
//                _nicknameCheckState.value = NicknameCheckState.Checking
//                authRepository.checkNickname(nickname)
//                // 여기까지 왔다 = 성공
//                _nicknameCheckState.value = NicknameCheckState.Available
//            } catch (e: Exception) {
//                Log.e(TAG, "닉네임 중복 체크 실패", e)
//                _nicknameCheckState.value = NicknameCheckState.Error(
//                    e.message ?: "닉네임 확인 중 오류가 발생했습니다."
//                )
//            }
//        }
//    }
//
//    // 카카오로 로그인하기
//
//
//    // TODO : 이 양식에 맞춰서 로그인, 회원가입 api 변경된 부분 에러처리 수정해야함.
//    sealed interface GoogleLoginStatus {
//        fun work(
//            authPreference: AuthPreference,
//            updateState: (SocialLoginState) -> Unit,
//            setProvider: (LoginType) -> Unit
//        )
//
//        data object Idle : GoogleLoginStatus {
//            override fun work(
//                authPreference: AuthPreference,
//                updateState: (SocialLoginState) -> Unit,
//                setProvider: (LoginType) -> Unit
//            ) = Unit
//        }
//
//        data class Success(val result: LoginResult) : GoogleLoginStatus {
//            override fun work(
//                authPreference: AuthPreference,
//                updateState: (SocialLoginState) -> Unit,
//                setProvider: (LoginType) -> Unit
//            ) {
//                setProvider(LoginType.GOOGLE)
//                if (result.refreshToken == null) {
//                    runBlocking { authPreference.clear() }
//                } else {
//                    runBlocking {
//                        authPreference.saveTokens(
//                            accessToken = result.accessToken,
//                            refreshToken = result.refreshToken,
//                            userId = result.userId,
//                            loginType = LoginType.GOOGLE
//                        )
//                    }
//                }
//                updateState(SocialLoginState.Success(result))
//            }
//        }
//
//        data class Error(val message: String) : GoogleLoginStatus {
//            override fun work(
//                authPreference: AuthPreference,
//                updateState: (SocialLoginState) -> Unit,
//                setProvider: (LoginType) -> Unit
//            ) {
//                updateState(SocialLoginState.Error(message))
//            }
//        }
//    }
//
//    val googleLoginStatus = MutableStateFlow<GoogleLoginStatus>(GoogleLoginStatus.Idle)
//
//    // 구글로 로그인하기
//    suspend fun loginWithGoogle(token: String) {
//        _googleLoginState.value = SocialLoginState.Loading
//
//        try {
//            authRepository.loginWithGoogle(token).fold(
//                onSuccess = {
//                    GoogleLoginStatus.Success(it).work(
//                        authPreference = authPreference,
//                        updateState = { state -> _googleLoginState.value = state },
//                        setProvider = { provider -> currentSocialProvider = provider }
//                    )
//                },
//                onFailure = {
//                    GoogleLoginStatus.Error(it.message ?: "구글 로그인 실패")
//                        .work(authPreference, { state -> _googleLoginState.value = state }, {})
//                }
//            )
//        } catch (e: kotlinx.coroutines.CancellationException) {
//            throw e
//        } catch (e: Exception) {
//            Log.e(TAG, "loginWithGoogle 예외 발생", e)
//            _googleLoginState.value = SocialLoginState.Error(e.message ?: "구글 로그인 실패")
//        }
//    }
//    /*fun loginWithGoogle(token: String) : GoogleLoginStatus{
//        Log.d("SocialAuthViewModel", "loadGoogleLogin")
//
//        googleLoginStatus.value = GoogleLoginStatus.Idle
//
//        viewModelScope.launch {
//            Log.d("SocialAuthViewModel", "loadWithGoogle launch")
//
//            _googleLoginState.value = SocialLoginState.Loading
//
//            val result = authRepository.loginWithGoogle(token).fold(
//                onSuccess = { googleLoginStatus.value = GoogleLoginStatus.Success },
//                onFailure = { e->
//                    Log.e(TAG, "loadWithGoogle failure", e)
//                    when(e){
//                        is ~~ ->
//                    }
//                }
//            )
//
//            /*try {
//                Log.d("SocialAuthViewModel", "loadWithGoogle try")
//                val result = authRepository.loginWithGoogle(token).fold(
//                    onSuccess = { googleLoginStatus.value = GoogleLoginStatus.Success },
//                    onFailure = { googleLoginStatus.value = GoogleLoginStatus.Error }
//                )
//                authPreference.saveTokens(
//                    accessToken = result.accessToken,
//                    refreshToken = result.refreshToken,
//                    userId = result.userId
//                )
//
//                _googleLoginState.value = SocialLoginState.Success(result)
//            } catch (e: Exception) {
//                Log.d(TAG, "loadWithGoogle catch: ${e.message}")
//                _googleLoginState.value = SocialLoginState.Error(e.message ?: "구글 로그인 실패")
//            }*/
//
//            Log.d("SocialAuthViewModel", "loadWithGoogle end")
//        }
//        Log.d("SocialAuthViewModel", "loadWithGoogle return")
//
//        return googleLoginStatus.value
//    }*/
//
//
//    fun updateNickname(input: String) {
//        if (_nickname.value == input) return
//        _nickname.value = input
//
//        if (isValidNickname(input)) {
//            nicknameQuery.value = input
//        } else {
//            _nicknameCheckState.value = NicknameCheckState.Idle
//        }
//    }
//
//    fun updateGender(value: Gender) {
//        _gender.value = value
//    }
//
//    fun updateJob(value: Job) {
//        _job.value = value
//    }
//
//    fun updatePurposes(values: List<Purpose>) {
//        _purposes.value = values
//    }
//
//    fun updateInterests(values: List<Interest>) {
//        _interests.value = values
//    }
//
//    fun clearError() {
//        _error.value = null
//    }
//
//
//    //소셜 프로필 완료 API
//    fun completeSocialProfile(
//        socialToken: String,
//        onSuccess: () -> Unit
//    ) {
//        // 기본 검증 (UI에서도 하지만 여기서 한 번 더)
//        if (_nickname.value.isBlank()) {
//            Log.w(TAG, "닉네임이 비어 있음")
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                _isLoading.value = true
//                _error.value = null
//
//                Log.d(TAG, "소셜 프로필 완료 API 호출 시작")
//
//                val success = authRepository.completeSocialProfile(
//                    socialToken = socialToken,
//                    nickName = _nickname.value,
//                    gender = _gender.value,
//                    job = _job.value,
//                    purposes = _purposes.value,
//                    interests = _interests.value
//                ).getOrThrow()
//
//                if (success) {
//                    Log.d(TAG, "소셜 프로필 완료 성공")
//                    authPreference.updateLoginType(currentSocialProvider)
//                    onSuccess()
//                } else {
//                    Log.e(TAG, "소셜 프로필 완료 실패 (서버 반환 false)")
//                    _error.value = IllegalStateException("프로필 저장에 실패했습니다.")
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, "소셜 프로필 완료 실패: ${e.message}")
//                _error.value = e
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//}


/**  MVI 구조에는 적합하지 않다고 판단함...  status렁 상태 관리가 엉킬 수 있는 구조임
 *  runBlocking은 메인 스레드를 사용합니다. ANR의 위험이 있는 코드이기에 반영하지 않았습니다... 미안...
 * sealed interface GoogleLoginStatus {
 *         fun work(
 *             authPreference: AuthPreference,
 *             updateState: (SocialLoginState) -> Unit,
 *             setProvider: (LoginType) -> Unit
 *         )
 *
 *         data object Idle : GoogleLoginStatus {
 *             override fun work(
 *                 authPreference: AuthPreference,
 *                 updateState: (SocialLoginState) -> Unit,
 *                 setProvider: (LoginType) -> Unit
 *             ) = Unit
 *         }
 *
 *         data class Success(val result: LoginResult) : GoogleLoginStatus {
 *             override fun work(
 *                 authPreference: AuthPreference,
 *                 updateState: (SocialLoginState) -> Unit,
 *                 setProvider: (LoginType) -> Unit
 *             ) {
 *                 setProvider(LoginType.GOOGLE)
 *                 if (result.refreshToken == null) {
 *                     runBlocking { authPreference.clear() }
 *                 } else {
 *                     runBlocking {
 *                         authPreference.saveTokens(
 *                             accessToken = result.accessToken,
 *                             refreshToken = result.refreshToken,
 *                             userId = result.userId,
 *                             loginType = LoginType.GOOGLE
 *                         )
 *                     }
 *                 }
 *                 updateState(SocialLoginState.Success(result))
 *             }
 *         }
 *
 *         data class Error(val message: String) : GoogleLoginStatus {
 *             override fun work(
 *                 authPreference: AuthPreference,
 *                 updateState: (SocialLoginState) -> Unit,
 *                 setProvider: (LoginType) -> Unit
 *             ) {
 *                 updateState(SocialLoginState.Error(message))
 *             }
 *         }
 *     }
 *
 * */