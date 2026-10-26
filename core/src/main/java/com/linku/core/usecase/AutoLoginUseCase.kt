package com.linku.core.usecase

import android.util.Log
import com.linku.core.error.ApiError
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.preference.AuthPreference
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 저장된 로그인 상태로 자동 로그인이 가능한지 확인하는 UseCase입니다.
 *
 * 이미 로그인된 상태이면 [AutoLoginState.Success], 아니면 [AutoLoginState.Failed]를 돌려줍니다. 토큰 만료만
 * 로그아웃(세션 정리)으로 처리하고, 나머지 실패는 절대 로그아웃하지 않습니다.
 *
 * @property authPreference 로그인 상태를 저장하고 정리하는 저장소
 */
class AutoLoginUseCase @Inject constructor(
    private val authPreference: AuthPreference,
) {

    /**
     * 자동 로그인 가능 여부를 확인합니다.
     *
     * @return [AutoLoginState.Success] 또는 [AutoLoginState.Failed]
     */
    suspend operator fun invoke(): AutoLoginState {
        return try {
            // dataStore 기반의 isLoggedIn Flow 최신 값을 받아옴
            val isAlreadyLoggedIn = authPreference.isLoggedIn.first()

            if (!isAlreadyLoggedIn) {
                AutoLoginState.Failed
            } else {
                Log.d(TAG, "자동 로그인 성공")
                AutoLoginState.Success
            }
        } catch (e: ApiError.Auth.TokenExpired) {
            // 이 경우만 logout
            Log.e(TAG, "자동 로그인 실패: 토큰 만료")
            authPreference.clear()
            AutoLoginState.Failed
        } catch (e: Exception) {
            // 나머지는 절대 logout 하지 않음
            Log.e(TAG, "자동 로그인 실패: ${e.message}")
            AutoLoginState.Failed
        }
    }

    private companion object {
        const val TAG = "LoginViewModel"
    }
}
