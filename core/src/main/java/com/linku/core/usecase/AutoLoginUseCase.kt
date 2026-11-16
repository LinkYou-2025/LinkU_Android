package com.linku.core.usecase

import android.util.Log
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.preference.AuthPreference
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * 저장된 로그인 상태로 자동 로그인이 가능한지 확인하는 UseCase입니다.
 *
 * 로컬 로그인 상태만 확인하며, 토큰 유효성은 첫 API 호출에서 TokenAuthenticator가 처리합니다.
 * 어떤 실패도 세션을 지우지 않습니다.
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
        } catch (e: CancellationException) {
            throw e // 코루틴 취소는 삼키지 않고 다시 던진다
        } catch (e: Exception) {
            // 나머지는 절대 logout 하지 않음
            Log.e(TAG, "자동 로그인 실패: ${e.message}")
            AutoLoginState.Failed
        }
    }

    private companion object {
        const val TAG = "AutoLoginUseCase"
    }
}
