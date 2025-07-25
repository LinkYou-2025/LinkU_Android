package com.example.login.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linku_android.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

// 이메일 인증과 관련된 로직을 담당하는 ViewModel
class EmailAuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "EmailAuthViewModel"
    }

    // 이메일 인증 코드 전송 결과 상태
    private val _sendCodeResult = MutableStateFlow<String?>(null)
    val sendCodeResult: StateFlow<String?> = _sendCodeResult

    // 인증 코드 전송 응답 코드 상태
    private val _sendCodeResultCode = MutableStateFlow<String?>(null)
    val sendCodeResultCode: StateFlow<String?> = _sendCodeResultCode

    // 이메일 인증 코드 검증 결과 상태
    private val _verifyCodeResult = MutableStateFlow<String?>(null)
    val verifyCodeResult: StateFlow<String?> = _verifyCodeResult


    // 인증 성공 여부를 Boolean으로 따로 분리
    val isVerifySuccess: StateFlow<Boolean> = _verifyCodeResult
        .map { it == "인증 성공" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    // 이메일 인증 코드를 요청하는 함수
    fun sendEmailCode(email: String) {
        viewModelScope.launch {
            //현재 백엔드 해당부분 배포가 되지 않아서, 임의로 안드 자체 수정함.
            //바로 밑 부분은 추후 "무조건 삭제"
            try {
                Log.d(TAG, "이메일 인증 코드 임시 전송 성공 처리: $email")

                // 임의로 성공 처리
                _sendCodeResult.value = "임시 코드 전송 성공"
                _sendCodeResultCode.value = "TEMP200"
            } catch (e: Exception) {
                Log.e(TAG, "임시 전송 중 예외", e)
                _sendCodeResult.value = "임시 네트워크 오류 발생"
            }
//            try {
//                Log.d(TAG, "이메일 인증 코드 전송 요청: $email")
//
//
//                // 서버에 이메일 인증 코드 요청 (헤더 없이)
//                val response = NetworkModule.authApi.sendEmailCode(email)
//
//                Log.d(TAG, "응답: $response")
//
//                if (response.isSuccess) {
//                    Log.d(TAG, "인증 코드 전송 성공: ${response.result}")
//                    _sendCodeResult.value = response.result
//                    _sendCodeResultCode.value = response.code // 응답 코드 저장
//                } else {
//                    Log.e(TAG, "인증 코드 전송 실패: ${response.message} ${response.code}")
//                    _sendCodeResult.value = response.message
//                    _sendCodeResultCode.value = response.code // 실패 코드도 저장
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "인증 코드 전송 중 예외 발생", e)
//                _sendCodeResult.value = "네트워크 오류 발생"
//                _sendCodeResultCode.value = null // 오류 발생 시 null 처리
//            }
        }
    }

    // 이메일 인증 코드를 검증하는 함수
    fun verifyEmailCode(context: Context,email: String, code: String) {
        viewModelScope.launch {
            //마찬가지로, 위의 이메일이 불가. 임의로 인증 코드 넣음
            //바로 밑의 코드는 백엔드 수정되는대로 "즉각 삭제"
            try {
                Log.d(TAG, "임시 인증 검증 요청: email=$email code=$code")

                if (code == "123456") {
                    _verifyCodeResult.value = "인증 성공"
                    Log.d(TAG, "임시 인증 성공 처리")

                } else {
                    _verifyCodeResult.value = "인증 실패: 코드 불일치"
                    Log.d(TAG, "임시 인증 실패 처리")
                }
            } catch (e: Exception) {
                Log.e(TAG, "임시 인증 중 예외 발생", e)
                _verifyCodeResult.value = "네트워크 오류 발생"
            }
//            try {
//                Log.d(TAG, "이메일 인증 코드 검증 요청: email=$email code=$code")
//
//                // 서버에 이메일 인증 코드 검증 요청 (헤더 없이)
//                val response = NetworkModule.authApi.verifyEmailCode(email, code)
//                Log.d(TAG, "응답: $response")
//                Log.d(TAG, "응답 전체 로그: isSuccess=${response.isSuccess}, code=${response.code}, message=${response.message}, result=${response.result}")
//
//
//                if (response.isSuccess) {
//                    Log.d(TAG, "이메일 인증 성공: ${response.result}")
//                    _verifyCodeResult.value = response.result
                        //markEmailVerified(context) // 예외처리,
                        //인증 중 앱 종료 시, 다시 진입해도 이메일 인증부터 시작
//                } else {
//                    Log.e(TAG, "이메일 인증 실패: ${response.message} ${response.code}")
//                    _verifyCodeResult.value = response.message
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "이메일 인증 중 예외 발생", e)
//                _verifyCodeResult.value = "네트워크 오류 발생"
//            }
        }
    }


}

// 서버 응답 구조를 담는 데이터 클래스
data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T?
)