package com.example.core.model.auth

sealed class SignUpState {
    object Idle : SignUpState()           // 초기 상태
    object Loading : SignUpState()        // 진행 중
    object Success : SignUpState()        // 성공
    data class Error(val message: String) : SignUpState()  // 실패
}

sealed class NicknameCheckState {
    object Idle : NicknameCheckState()
    object Checking : NicknameCheckState()  // Loading 역할
    object Available : NicknameCheckState()
    object Duplicated : NicknameCheckState()
    data class Error(val message: String) : NicknameCheckState()
}