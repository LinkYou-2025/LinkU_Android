package com.linku.deeplink

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 로그인 후 이어서 처리할 딥링크 상태의 저장, 복원 및 일회성 소비 계약을 검증합니다.
 */
class DeepLinkHandlerViewModelTest {

    /**
     * 지정한 [SavedStateHandle]을 사용하는 딥링크 상태 ViewModel을 생성합니다.
     *
     * @param savedStateHandle 테스트할 보류 딥링크 상태 저장소
     * @return 전달한 저장소를 사용하는 [DeepLinkHandlerViewModel]
     */
    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): DeepLinkHandlerViewModel = DeepLinkHandlerViewModel(savedStateHandle)

    @Test
    fun `pending invitation token defaults to an empty string`() {
        val viewModel = createViewModel()

        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `blank invitation token is not stored`() {
        val viewModel = createViewModel()

        viewModel.setPendingInvitation("   ")

        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `pending invitation token is consumed only once`() {
        val viewModel = createViewModel()

        viewModel.setPendingInvitation("invitation-token")

        assertEquals("invitation-token", viewModel.consumePendingInvitation())
        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `clearing pending deep links removes every pending value`() {
        val viewModel = createViewModel()

        viewModel.setPendingShare(1L)
        viewModel.setPendingInvitation("invitation-token")

        viewModel.clearPendingDeepLinks()

        assertNull(viewModel.consumePendingShare())
        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `pending invitation token is restored from saved state`() {
        val savedStateHandle = SavedStateHandle()

        createViewModel(savedStateHandle).setPendingInvitation("invitation-token")

        assertEquals(
            "invitation-token",
            createViewModel(savedStateHandle).consumePendingInvitation()
        )
    }

    /** 저장된 공유 폴더 ID가 동일한 상태 저장소를 사용하는 새 ViewModel에서 복원되는지 검증합니다. */
    @Test
    fun `pending share folder id is restored from saved state`() {
        val savedStateHandle = SavedStateHandle()

        createViewModel(savedStateHandle).setPendingShare(42L)

        assertEquals(
            42L,
            createViewModel(savedStateHandle).consumePendingShare(),
        )
    }

    /** 공유 폴더 ID를 소비하면 저장소에서도 제거되어 다시 복원되지 않는지 검증합니다. */
    @Test
    fun `pending share folder id is consumed only once`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = createViewModel(savedStateHandle)

        viewModel.setPendingShare(42L)

        assertEquals(42L, viewModel.consumePendingShare())
        assertNull(createViewModel(savedStateHandle).consumePendingShare())
    }
}
