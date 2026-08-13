package com.linku.file.viewmodel.leave.state

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaveStateViewModelTest {

    @Test
    fun `나가기 모드는 기본 비활성 상태에서 명시적으로 진입하고 종료한다`() {
        val viewModel = LeaveStateViewModel()

        assertFalse(viewModel.isLeaveMode)

        viewModel.updateLeaveMode(true)
        assertTrue(viewModel.isLeaveMode)

        viewModel.updateLeaveMode(false)
        assertFalse(viewModel.isLeaveMode)
    }
}
