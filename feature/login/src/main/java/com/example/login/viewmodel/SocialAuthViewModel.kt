package com.example.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.auth.Gender
import com.example.core.model.auth.Job
import com.example.core.model.auth.Purpose
import com.example.core.model.auth.Interest
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.core.model.auth.NicknameCheckState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter


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
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SocialAuthViewModel"
        private const val NICKNAME_DEBOUNCE_TIME = 500L
        private const val MAX_NICKNAME_LENGTH = 6
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

    private fun checkNicknameInternal(nickname: String) {
        viewModelScope.launch {
            try {
                _nicknameCheckState.value = NicknameCheckState.Checking

                // 실제 서버 API 호출
                val available = userRepository.checkNickname(nickname)

                _nicknameCheckState.value =
                    if (available) {
                        NicknameCheckState.Available
                    } else {
                        NicknameCheckState.Duplicated
                    }

            } catch (e: Exception) {
                Log.e(TAG, "닉네임 중복 체크 실패", e)
                _nicknameCheckState.value =
                    NicknameCheckState.Error(
                        e.message ?: "닉네임 확인 중 오류가 발생했습니다."
                    )
            }
        }
    }

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

                val success = userRepository.completeSocialProfile(
                    socialToken = socialToken,
                    nickname = _nickname.value,
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