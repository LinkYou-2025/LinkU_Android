package com.example.login.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.auth.Gender
import com.example.core.model.auth.Interest
import com.example.core.model.auth.NicknameCheckState
import com.example.core.model.auth.Purpose
import com.example.core.model.auth.SignUpForm
import com.example.core.model.auth.SignUpState
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 상수 분리
    companion object {
        private const val NICKNAME_DEBOUNCE_TIME = 500L //0.5초 동안 입력이 없는 경우.
        private const val MAX_NICKNAME_LENGTH = 6 //닉네임은 6글자 이하
    }

    // 회원가입 전체 입력 폼
    var signUpForm by mutableStateOf(SignUpForm())
        private set

    // 닉네임 중복 체크 상태.
    private val _nicknameState = MutableStateFlow<NicknameCheckState>(NicknameCheckState.Idle)
    val nicknameState: StateFlow<NicknameCheckState> = _nicknameState

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState


    // 닉네임 중복 체크 파이프라인(과잉 호출되지 않도록)
    private val nicknameQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            nicknameQuery
                .debounce(NICKNAME_DEBOUNCE_TIME)
                .distinctUntilChanged()
                .filter { isValidNickname(it) }
                .collect { query ->
                    checkNickname(query)
                }
        }
    }

    // 공통 데이터
    fun updateForm(update: (SignUpForm) -> SignUpForm) {
        signUpForm = update(signUpForm)
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

    // 닉네임 유효성 검사
    private fun isValidNickname(input: String): Boolean {
        return input.isNotBlank() && input.length in 1..MAX_NICKNAME_LENGTH
    }

    // 닉네임 관련 로직
    fun onNicknameChanged(input: String) {
        if (input == signUpForm.nickname) return

        updateForm { it.copy(nickname = input) }

        if (isValidNickname(input)) {
            nicknameQuery.value = input
        } else {
            _nicknameState.value = NicknameCheckState.Idle
        }
    }

    // 닉네임 중복 체크
    private fun checkNickname(input: String) {
        viewModelScope.launch {
            _nicknameState.value = NicknameCheckState.Checking
            try {
                val available = userRepository.checkNickname(input)
                _nicknameState.value = if (available) {
                    NicknameCheckState.Available
                } else {
                    NicknameCheckState.Duplicated
                }
            } catch (e: Exception) {
                _nicknameState.value = NicknameCheckState.Error(
                    e.message ?: "닉네임 확인 중 오류가 발생했습니다."
                )
                Log.e("SignUpViewModel", "닉네임 중복 체크 실패", e)
            }
        }
    }

    // 직업 선택 로직
    fun onJobSelected(jobId: Int) {
        updateForm { it.copy(jobId = jobId) }
    }

    // Purpose 선택 로직 - List<Purpose> enum으로 변경
    fun onPurposeListChanged(purposeList: List<Purpose>) {
        updateForm { it.copy(purposeList = purposeList) }
    }

    // Interest 선택 로직 추가
    fun onInterestListChanged(interestList: List<Interest>) {
        updateForm { it.copy(interestList = interestList) }
    }

    // 회원가입 폼 유효성 검사
    private fun validateSignUpForm(): String? {
        return when {
            signUpForm.email.isBlank() -> "이메일을 입력해주세요."
            signUpForm.password.isBlank() -> "비밀번호를 입력해주세요."
            signUpForm.nickname.isBlank() -> "닉네임을 입력해주세요."
            _nicknameState.value != NicknameCheckState.Available -> "닉네임 중복 확인이 필요합니다."
            signUpForm.gender == Gender.NONE -> "성별을 선택해주세요."
            signUpForm.jobId == 0 -> "직업을 선택해주세요."
            signUpForm.purposeList.isEmpty() -> "목적을 선택해주세요."
            signUpForm.interestList.isEmpty() -> "관심사를 선택해주세요."
            !signUpForm.agreeTerms || !signUpForm.agreePrivacy -> "필수 약관에 동의해주세요."
            else -> null
        }
    }
    private var isSignUpRequested = false //중복 호출 방지.
    // 최종 회원가입 로직
    fun signUp() {
        if (isSignUpRequested) return  // 이미 요청했으면 아무것도 안 함.
        isSignUpRequested = true //처음 호출 시 true로 잠금.

        // 유효성 검사
        validateSignUpForm()?.let { errorMessage ->
            _signUpState.value = SignUpState.Error(errorMessage)
            isSignUpRequested = false  // 유효성 실패시 다시 시도 가능하게 잠금을 해제함.
            return
        }

        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState.Loading
                Log.d("SignUpViewModel", "[회원가입 요청] $signUpForm")

                // Purpose enum의 code 값을 List<String>으로 변환
                val purposeKeys = signUpForm.purposeList.map { it.serverKey }
                val interestKeys = signUpForm.interestList.map { it.serverKey }

                val success = userRepository.signUpWithEmail(
                    nickname = signUpForm.nickname,
                    email = signUpForm.email,
                    password = signUpForm.password,
                    gender = signUpForm.gender.value,
                    jobId = signUpForm.jobId,
                    purposeList = purposeKeys,
                    interestList = interestKeys
                )

                _signUpState.value = if (success) {
                    SignUpState.Success
                } else {
                    SignUpState.Error("회원가입에 실패했습니다.")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(
                    e.message ?: "알 수 없는 오류가 발생했습니다."
                )
                Log.e("SignUpViewModel", "회원가입 실패", e)
            }
        }
    }
}