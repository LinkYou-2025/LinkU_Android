package com.linku.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.error.AppError
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.NicknameCheckState
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.SignUpForm
import com.linku.core.model.auth.SignUpState
import com.linku.core.repository.AuthRepository
import com.linku.login.mvi.MviContainer
import com.linku.login.mvi.mviContainer
import com.linku.login.viewmodel.state.SignUpEffect
import com.linku.login.viewmodel.state.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), MviContainer<SignUpUiState, SignUpEffect> by mviContainer(SignUpUiState()) {

    // 상수 분리
    companion object {
        private const val NICKNAME_DEBOUNCE_TIME = 500L // 0.5초 동안 입력이 없는 경우.
        private const val MAX_NICKNAME_LENGTH = 6 // 닉네임은 6글자 이하
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState


    // 닉네임 중복 체크 파이프라인(과잉 호출되지 않도록)
    private val nicknameQuery = MutableStateFlow("")
    private val isSignUpInProgress = MutableStateFlow(false)



    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            nicknameQuery
                .debounce(NICKNAME_DEBOUNCE_TIME) //0.5초 동안 입력 없으면
                .distinctUntilChanged()
                .filter { isValidNickname(it) }
                .collect { query ->
                    checkNickname(query) //서버에 물어봄
                }
        }
    }

    // 공통 데이터
    fun updateForm(update: (SignUpForm) -> SignUpForm) {
        updateState {
            copy(signUpForm = update(signUpForm))
        }
    }

    // 약관 동의 관련 로직
    fun setAgreeAll(v: Boolean) {
        updateForm { it.copy(agreeTerms = v, agreePrivacy = v, agreeMarketing = v) }
    }

    fun setAgreeTerms(agree: Boolean) {
        updateForm { it.copy(agreeTerms = agree) }
    }

    fun setAgreePrivacy(agree: Boolean) {
        updateForm { it.copy(agreePrivacy = agree) }
    }

    fun setAgreeMarketing(agree: Boolean) {
        updateForm { it.copy(agreeMarketing = agree) }
    }

    // 비밀번호 확인 + 폼 전달
    fun onPasswordChanged(password: String) {
        updateState {
            val updatedForm = signUpForm.copy(password = password)

            val isLengthValid = password.length in 8..20
            val isComplex = password.any { it.isDigit() } &&
                    password.any { it.isLetter() } &&
                    password.any { !it.isLetterOrDigit() }
            val isPassValid = isLengthValid && isComplex
            val match = password == passwordStep.confirmPassword

            copy(
                signUpForm = updatedForm,
                passwordStep = passwordStep.copy(
                    isLengthValid = isLengthValid,
                    isComplex = isComplex,
                    doPasswordsMatch = match,
                    isPasswordValid = isPassValid,
                    canProceed = isPassValid && match
                )
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        updateState {
            val match = signUpForm.password == confirmPassword

            copy(
                passwordStep = passwordStep.copy(
                    confirmPassword = confirmPassword,
                    doPasswordsMatch = match,
                    canProceed = passwordStep.isPasswordValid && match
                )
            )
        }
    }

    fun onPasswordNextClicked() {
        postSideEffect(SignUpEffect.NavigateToNickname)
    }


    // 닉네임 유효성 검사
    private fun isValidNickname(input: String): Boolean {
        return input.isNotBlank() &&
                input.length in 1..MAX_NICKNAME_LENGTH &&
                input.matches(Regex("^[가-힣a-zA-Z]+$"))
    }


    // 닉네임 관련 로직
    fun onNicknameChanged(nickname: String) {
        if (nickname == state.value.signUpForm.nickname) return

        val isValid = isValidNickname(nickname)

        updateState {
            copy(
                signUpForm = signUpForm.copy(nickname = nickname),
                nicknameStep = nicknameStep.copy(
                    isNicknameValid = isValid,
                    nicknameCheckState = NicknameCheckState.Idle
                )
            )
        }

        if (isValid) {
            nicknameQuery.value = nickname
        }
    }

    // 닉네임 중복 체크
    private fun checkNickname(nickname: String) {
        viewModelScope.launch {
            updateState {
                copy(nicknameStep = nicknameStep.copy(nicknameCheckState = NicknameCheckState.Checking))
            }
            authRepository.checkNickname(nickname).foldApp(
                onSuccess = {
                    updateState {
                        copy(nicknameStep = nicknameStep.copy(nicknameCheckState = NicknameCheckState.Available))
                    }
                },
                onFailure = { error ->
                    val nextCheckState = when (error) {
                        is ApiError.User.DuplicateNickname -> NicknameCheckState.Duplicated
                        else -> NicknameCheckState.Error(error.displayMessage)
                    }
                    updateState {
                        copy(nicknameStep = nicknameStep.copy(nicknameCheckState = nextCheckState))
                    }
                    Log.e("SignUpViewModel", "닉네임 중복 체크 실패", error)
                }
            )
        }
    }

    fun onNicknameNextClicked() {
        postSideEffect(SignUpEffect.NavigateToGender)
    }

    // 성별 변화
    fun onGenderChanged(gender: Gender) {
        updateState { copy(signUpForm = signUpForm.copy(gender = gender)) }
    }

    fun onGenderNextClicked() {
        postSideEffect(SignUpEffect.NavigateToJob)
    }

    // 직업 선택 로직
    fun onJobSelected(jobId: Int) {
        updateForm { it.copy(jobId = jobId) }
    }

    fun onJobNextClicked() {
        postSideEffect(SignUpEffect.NavigateToPurpose)
    }

    // 목적 설정 단계
    fun togglePurpose(purpose: Purpose) {
        updateState {
            val currentList = signUpForm.purposeList
            val updatedList = if (currentList.contains(purpose)) {
                currentList - purpose
            } else {
                currentList + purpose
            }
            copy(signUpForm = signUpForm.copy(purposeList = updatedList))
        }
    }

    fun onPurposeNextClicked() {
        postSideEffect(SignUpEffect.NavigateToInterest)
    }

    //관심사 설정
    fun toggleInterest(interest: Interest) {
        updateState {
            val currentList = signUpForm.interestList
            val updatedList = if (currentList.contains(interest)) {
                currentList - interest
            } else {
                currentList + interest
            }
            copy(signUpForm = signUpForm.copy(interestList = updatedList))
        }
    }

    fun onInterestNextClicked() {
        postSideEffect(SignUpEffect.NavigateToWelcome)
    }

    // 회원가입 폼 유효성 검사
    private fun validateSignUpForm(): String? {
        val form = state.value.signUpForm
        val nicknameCheck = state.value.nicknameStep.nicknameCheckState

        return when {
            form.email.isBlank() -> "이메일을 입력해주세요."
            form.password.isBlank() -> "비밀번호를 입력해주세요."
            form.nickname.isBlank() -> "닉네임을 입력해주세요."
            nicknameCheck != NicknameCheckState.Available -> "닉네임 중복 확인이 필요합니다."
            form.gender == Gender.NONE -> "성별을 선택해주세요."
            form.jobId == 0 -> "직업을 선택해주세요."
            form.purposeList.isEmpty() -> "목적을 선택해주세요."
            form.interestList.isEmpty() -> "관심사를 선택해주세요."
            !form.agreeTerms || !form.agreePrivacy -> "필수 약관에 동의해주세요."
            else -> null
        }
    }

    // 최종 회원가입 로직
    fun signUp() {
        if (!isSignUpInProgress.compareAndSet(expect = false, update = true)) return

        validateSignUpForm()?.let { errorMessage ->
            _signUpState.value = SignUpState.Error(errorMessage)
            isSignUpInProgress.value = false
            return
        }

        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState.Loading
                val form = state.value.signUpForm
                Log.d("SignUpViewModel", "[회원가입 요청] $form")

                val termsMap = mapOf(
                    "TERMS_OF_USE" to form.agreeTerms,
                    "PRIVACY_POLICY" to form.agreePrivacy,
                    "MARKETING" to form.agreeMarketing
                )

                authRepository.signUpWithEmail(
                    nickname = form.nickname,
                    email = form.email,
                    password = form.password,
                    gender = form.gender.value,
                    jobId = form.jobId,
                    purposeList = form.purposeList,
                    interestList = form.interestList,
                    termsMap = termsMap
                ).foldApp(
                    onSuccess = { result ->
                        Log.d("SignUpViewModel", "[회원가입 성공] userId=${result.userId}")
                        _signUpState.value = SignUpState.Success
                    },
                    onFailure = { error ->
                        _signUpState.value = SignUpState.Error(error.displayMessage)
                        Log.e("SignUpViewModel", "회원가입 실패: ${error.displayMessage}")
                    }
                )
            } finally {
                isSignUpInProgress.value = false
            }
        }
    }
}

// 타입 캐스팅이 싫어서 만들었는데 마음에 안들면 원래대로 하겠습니당
inline fun <T> Result<T>.foldApp(
    onSuccess: (T) -> Unit,
    onFailure: (error: AppError) -> Unit
) {
    fold(
        onSuccess = { onSuccess(it) },
        onFailure = { exception ->
            val appError = when (exception) {
                is AppError -> exception
                else -> ApiError.Unknown(exception.message ?: "알 수 없는 오류가 발생했습니다.")
            }
            onFailure(appError)
        }
    )
}