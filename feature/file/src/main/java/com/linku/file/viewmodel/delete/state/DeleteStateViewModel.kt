package com.linku.file.viewmodel.delete.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * 폴더 삭제 대상을 선택하는 UI 모드의 활성 여부를 관리합니다.
 *
 * 삭제할 폴더와 실제 삭제 동작은 보유하지 않으며, 화면에서 폴더를 선택했을 때
 * 해당 폴더의 기존 삭제 콜백으로 전달할 수 있도록 모드 상태만 제공합니다.
 */
class DeleteStateViewModel : ViewModel() {
    /** 폴더 삭제 대상 선택 모드가 활성화되어 있는지 나타냅니다. */
    var isDeleteMode by mutableStateOf(false)
        private set

    /** 폴더 삭제 대상 선택 모드의 활성 여부를 갱신합니다. */
    fun updateDeleteMode(value: Boolean) {
        isDeleteMode = value
    }
}
