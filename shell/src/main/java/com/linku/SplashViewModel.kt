package com.linku

import androidx.lifecycle.ViewModel
import com.linku.core.preference.NotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 스플래시 화면의 알림 권한 요청 여부를 관리하는 ViewModel입니다.
 *
 * @param notificationPreference 알림 설정 저장소
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val notificationPreference: NotificationPreference,
) : ViewModel() {

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
}
