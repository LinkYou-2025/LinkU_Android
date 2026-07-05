package com.linku.login.viewmodel.state

import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.NicknameCheckState
import com.linku.core.model.auth.Purpose
import com.linku.login.mvi.UiState

/**
 * 소셜 로그인 및 추가 정보 입력 온보딩 플로우의 통합 UI 상태를 관리하는 데이터 클래스입니다.
 *
 * @property isLoading 전체 화면의 로딩 상태 또는 API 요청 진행 여부를 나타냅니다.
 * @property socialLoginForm 온보딩 전 과정의 데이터가 누적되는 폼 주머니 객체입니다. (★에러 해결의 핵심)
 * @property nicknameCheckState 닉네임 실시간 중복 검증 상태(Idle, Checking, Available, Error)를 나타냅니다.
 * @property error 화면에 표시할 예외 상황 메시지 또는 서버 에러 문구입니다.
 */
internal data class SocialAuthUiState(
    val isLoading: Boolean = false,
    val socialLoginForm: SocialLoginForm = SocialLoginForm(),
    val nicknameCheckState: NicknameCheckState = NicknameCheckState.Idle,
    val error: String? = null
) : UiState

internal data class SocialLoginForm(
    val agreeTerms: Boolean = false,
    val agreePrivacy: Boolean = false,
    val agreeMarketing: Boolean = false,
    val nickname: String = "",
    val gender: Gender = Gender.NONE,
    val job: Job = Job.NONE,
    val purposes: List<Purpose> = emptyList(),
    val interests: List<Interest> = emptyList()
)