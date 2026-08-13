package com.linku.file.viewmodel.leave.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * 공유폴더 목록에서 long press 대상을 고르는 UI 모드만 관리합니다.
 *
 * 나가기 대상과 네트워크 요청은 탐색 상태 및 FileViewModel이 소유하며, 이 ViewModel에는
 * 안정적인 폴더 ID나 비동기 작업을 저장하지 않습니다.
 */
class LeaveStateViewModel : ViewModel() {
    var isLeaveMode by mutableStateOf(false)
        private set

    fun updateLeaveMode(value: Boolean) {
        isLeaveMode = value
    }
}
