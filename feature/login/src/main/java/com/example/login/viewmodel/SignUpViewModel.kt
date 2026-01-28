package com.example.login.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// 모든 회원가입 입력 데이터를 담는 데이터 클래스
data class SignUpForm(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val gender: Gender = Gender.NONE,
    val jobId: Int = 0,
    val purposeList: List<Purpose> = emptyList(),
    val interestList: List<Interest> = emptyList(),
    val agreeTerms: Boolean = false,    // 필수
    val agreePrivacy: Boolean = false,  // 필수
    val agreeMarketing: Boolean = false // 선택
)

// signUpSuccess는 null 허용 안 할 순 없어? -> 이렇게 아예 state로 분리하는 것은 어떤지.
sealed class SignUpState {
    object Idle : SignUpState()           // 초기 상태
    object Loading : SignUpState()        // 진행 중
    object Success : SignUpState()        // 성공
    data class Error(val message: String) : SignUpState()  // 실패
}

// 닉네임 중복 상태 체크
sealed class NicknameCheckState {
    object Idle : NicknameCheckState()
    object Checking : NicknameCheckState()  // Loading 역할
    object Available : NicknameCheckState()
    object Duplicated : NicknameCheckState()
    data class Error(val message: String) : NicknameCheckState()
}

// 성별
enum class Gender(val value: Int){
    NONE(0), // 미선택 판단
    MALE(1), // 남성인 경우 api에 gender 값으로 1이 전달됩니다.
    FEMALE(2) // 여성인 경우 api에 gender 값으로 2이 전달됩니다.
}

// 직업
enum class Job(val id: Int, val displayName: String) {
    NONE(0, "미선택"),
    HIGH_SCHOOL(1, "고등학생"),
    COLLEGE(2, "대학생"),
    WORKER(3, "직장인"),
    SELF_EMPLOYED(4, "자영업자"),
    FREELANCER(5, "프리랜서"),
    JOB_SEEKER(6, "취준생");

    companion object {
        // NONE을 제외한 선택 가능한 직업 리스트
        fun getAllJobs(): List<Job> = values().filter { it != NONE }

        // ID로 Job 찾기
        fun fromId(id: Int): Job = values().find { it.id == id } ?: NONE
    }
}

// 목적
enum class Purpose(val code: String, val displayName: String) {
    SELF_DEVELOPMENT("SELF_DEVELOPMENT", "자기개발\n/정보수집"),
    SIDE_PROJECT("SIDE_PROJECT", "사이드 프로젝트\n/창업준비"),
    OTHERS("OTHERS", "기타"),
    LATER_READING("LATER_READING", "그냥 나중에\n읽고 싶은 글 저장"),
    CAREER("CAREER", "취업 커리어 준비"),
    CREATION_REFERENCE("CREATION_REFERENCE", "블로그/콘텐츠 작성 참고용"),
    INSIGHTS("INSIGHTS", "인사이트 모으기"),
    WORK("WORK", "업무자료 아카이빙"),
    STUDY("STUDY", "학업/리포트 정리");

    companion object {
        // 모든 Purpose 리스트 반환
        fun getAllPurposes(): List<Purpose> = values().toList()

        // code로 Purpose 찾기
        fun fromCode(code: String): Purpose? = values().find { it.code == code }

        // displayName으로 Purpose 찾기
        fun fromDisplayName(displayName: String): Purpose? =
            values().find { it.displayName == displayName }
    }
}

// 관심사
enum class Interest(val code: String, val displayName: String) {
    BUSINESS("BUSINESS", "비즈니스/마케팅"),
    DESIGN("DESIGN", "디자인/\n크리에이티브"),
    IT("IT", "IT/개발"),
    STARTUP("STARTUP", "스타트업/창업"),
    SOCIETY("SOCIETY", "사회/문화/환경"),
    STUDY("STUDY", "학업/\n리포트 참고"),
    WRITING("WRITING", "글쓰기/콘텐츠\n작성"),
    INSIGHTS("INSIGHTS", "책/인사이트\n요약"),
    PSYCHOLOGY("PSYCHOLOGY", "심리/자기계발"),
    CURRENT_EVENTS("CURRENT_EVENTS", "시사/트렌드"),
    COLLECT("COLLECT", "그냥 모아두고\n싶은 글들"),
    CAREER("CAREER", "커리어/채용");

    companion object {
        // 모든 Interest 리스트 반환
        fun getAllInterests(): List<Interest> = values().toList()

        // code로 Interest 찾기
        fun fromCode(code: String): Interest? = values().find { it.code == code }

        // displayName으로 Interest 찾기
        fun fromDisplayName(displayName: String): Interest? =
            values().find { it.displayName == displayName }
    }
}

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
            !signUpForm.agreeTerms || !signUpForm.agreePrivacy -> "필수 약관에 동의해주세요."
            else -> null
        }
    }

    // 최종 회원가입 로직
    fun signUp() {
        // 유효성 검사
        validateSignUpForm()?.let { errorMessage ->
            _signUpState.value = SignUpState.Error(errorMessage)
            return
        }

        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState.Loading
                Log.d("SignUpViewModel", "[회원가입 요청] $signUpForm")

                // Purpose enum의 code 값을 List<String>으로 변환
                val purposeCodes = signUpForm.purposeList.map { it.code }
                val interestCodes = signUpForm.interestList.map { it.code }

                val success = userRepository.signUp(
                    nickname = signUpForm.nickname,
                    email = signUpForm.email,
                    password = signUpForm.password,
                    gender = signUpForm.gender.value,
                    jobId = signUpForm.jobId,
                    purposeList = purposeCodes,
                    interestList = interestCodes
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