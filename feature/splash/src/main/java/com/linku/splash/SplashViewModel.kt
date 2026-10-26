package com.linku.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.auth.AutoLoginState
import com.linku.core.preference.AuthPreference
import com.linku.core.preference.NotificationPreference
import com.linku.core.usecase.AutoLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 스플래시 화면의 알림 권한 요청 여부와 자동 로그인 결과에 따른 첫 화면 결정을 담당하는 ViewModel입니다.
 *
 * @param notificationPreference 알림 설정 저장소
 * @param authPreference 로그인 세션 저장소
 * @param autoLoginUseCase 저장된 로그인 상태로 자동 로그인이 가능한지 확인하는 UseCase
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val notificationPreference: NotificationPreference,
    private val authPreference: AuthPreference,
    private val autoLoginUseCase: AutoLoginUseCase,
) : ViewModel() {

    private val _destination = Channel<SplashDestination>(Channel.BUFFERED)

    /** 스플래시가 끝난 뒤 이동할 첫 화면을 한 번만 전달하는 이벤트 스트림입니다. */
    val destination: Flow<SplashDestination> = _destination.receiveAsFlow()

    // 자동 로그인은 프로세스 내에서 한 번만 시도합니다. 화면 회전 등으로 스플래시가 다시 그려져도 유지됩니다.
    private var autoLoginTried = false

    /**
     * 시스템 알림 권한 팝업을 아직 띄운 적이 없다면 요청 완료로 기록하고 true를 반환합니다.
     *
     * @return 이번에 권한 팝업을 띄워야 하면 true, 이미 띄운 적이 있으면 false
     */
    suspend fun consumeSystemPermissionRequest(): Boolean {
        if (notificationPreference.isSystemPermissionRequested()) return false
        notificationPreference.setSystemPermissionRequested(true)
        return true
    }

    /**
     * 스플래시 애니메이션이 끝났을 때 호출하며, 자동 로그인 결과에 따라 [destination]으로 첫 화면을 알립니다.
     *
     * 이미 시도했거나 저장된 리프레시 토큰이 없으면 자동 로그인 없이 로그인 화면으로 보냅니다.
     */
    fun onSplashFinished() {
        viewModelScope.launch {
            val hasRefreshToken = !authPreference.getRefreshToken().isNullOrBlank()

            if (autoLoginTried || !hasRefreshToken) {
                _destination.send(SplashDestination.Login)
                return@launch
            }

            autoLoginTried = true
            val result = autoLoginUseCase()
            _destination.send(
                if (result is AutoLoginState.Success) SplashDestination.Home else SplashDestination.Login
            )
        }
    }
}

/** 스플래시 종료 후 이동할 첫 화면입니다. */
sealed interface SplashDestination {

    /** 자동 로그인에 성공해 홈으로 이동합니다. */
    data object Home : SplashDestination

    /** 자동 로그인이 불가능해 로그인 화면으로 이동합니다. */
    data object Login : SplashDestination
}
