package com.example.login.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.core.repository.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import retrofit2.HttpException
import okhttp3.ResponseBody
import org.json.JSONObject

// 이메일 인증과 관련된 로직을 담당하는 ViewModel



@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    //  추가: 공용 에러 태그 세터
    private fun flagServerError() { _errorTag.value = "SERVER_ERROR" }

    //상태 초기화 (화면 재진입/재시도 시 호출)
    fun reset() {
        _sendCodeResult.value = null
        _verifyCodeResult.value = null
        _errorTag.value = null            // ← 추가: 에러 태그 초기화 (옵션)
        // isVerifySuccess는 앞서 1회성으로 바꿨다면 false가 기본이므로 그대로 두면 됨
    }

    // 인증 코드 전송 결과
    private val _sendCodeResult = MutableStateFlow<String?>(null)
    val sendCodeResult: StateFlow<String?> = _sendCodeResult

    // 인증 코드 검증 결과
    private val _verifyCodeResult = MutableStateFlow<String?>(null)
    val verifyCodeResult: StateFlow<String?> = _verifyCodeResult

    //  추가: 로그인 화면에서 그대로 매핑해 쓸 에러 태그
    private val _errorTag = MutableStateFlow<String?>(null)
    val errorTag: StateFlow<String?> = _errorTag

    // 변경: 1회성 신호로 쓰는 전용 플래그
    private val _isVerifySuccess = MutableStateFlow(false)
    val isVerifySuccess: StateFlow<Boolean> = _isVerifySuccess

    // 6자리 랜덤 코드 생성 함수
    private fun generateRandomSixDigitCode(): String {
        return Random.nextInt(0, 1_000_000)
            .toString()
            .padStart(6, '0')
    }

    // ResponseBody → 서버에서 내려주는 JSON 중 "message" 키값만 안전하게 추출
    private fun ResponseBody.safeStringMessage(): String? = try {
        val s = string() // 전체 response body 문자열
        val msg = JSONObject(s).optString("message", "")
        if (msg.isBlank()) null else msg  // 빈 문자열이면 null로 처리
    } catch (_: Throwable) {
        null
    }

    /** 이메일 인증 코드 전송 */
    fun sendEmailCode(email: String) {
        Log.d("EmailAuthVM", " sendEmailCode() called. email=$email")
        viewModelScope.launch {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Log.w("EmailAuthVM", "Invalid email format: $email")
                _sendCodeResult.value = "잘못된 이메일 형식"
                return@launch
            }
            val code = generateRandomSixDigitCode()
            Log.d("EmailAuthVM", "Generated code = $code")
            try {
                val ok = userRepository.sendEmailCode(email, code)
                Log.d("EmailAuthVM", " Server sendEmailCode result = $ok")
                _sendCodeResult.value = if (ok) "인증 코드 전송 성공" else "서버 오류"
            } catch (e: HttpException) {
                Log.e("EmailAuthVM", " HttpException in sendEmailCode", e)
                _sendCodeResult.value = when (e.code()) {
                    409 -> { // 중복 이메일
                        // (선택) 서버 메시지 파싱해서 UI로 보낼 수도 있음
                        val msg = e.response()?.errorBody()?.safeStringMessage()
                        Log.e("EmailAuthVM", " Duplicate email. server message=$msg")
                        if (msg?.contains("중복") == true) "이미 가입된 이메일입니다."
                        else "이미 가입된 이메일입니다."
                    }

                    else -> {
                        flagServerError()
                        "서버 오류"
                    }
                }
            } catch (e: Exception) {
                Log.e("EmailAuthVM", " Unknown error in sendEmailCode", e)
                _sendCodeResult.value = "서버 오류"
                flagServerError()                  //  서버 에러 태그 세팅
            }
        }
    }

    // 이메일 인증 코드 전송
    fun verifyEmailCode(email: String, code: String) {
        Log.d("EmailAuthVM", " verifyEmailCode() called. email=$email, code=$code")
        viewModelScope.launch {
            try {
                val ok = userRepository.verifyEmailCode(email, code)
                Log.d("EmailAuthVM", " Server verify result = $ok")
                _verifyCodeResult.value = if (ok) "인증 성공" else "인증 실패"

                // 여기서 1회성으로 true → 잠깐 후 false로 되돌림
                _isVerifySuccess.value = ok
                if (ok) {
                    // 네비게이션 트리거 후 바로 false로 reset (재진입 자동 네비 방지)
                    kotlinx.coroutines.delay(200)
                    _isVerifySuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("EmailAuthVM", "verifyEmailCode error", e)
                _verifyCodeResult.value = "네트워크 오류"
                _isVerifySuccess.value = false
                flagServerError()                      //  검증 중 서버 죽어도 태그 세팅
            }
        }
    }
}

