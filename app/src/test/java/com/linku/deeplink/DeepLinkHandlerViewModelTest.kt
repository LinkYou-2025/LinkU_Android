package com.linku.deeplink

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkHandlerViewModelTest {

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
}
