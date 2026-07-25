package com.linku.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkHandlerViewModelTest {

    private val viewModel = DeepLinkHandlerViewModel()

    @Test
    fun `pending invitation token defaults to an empty string`() {
        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `blank invitation token is not stored`() {
        viewModel.setPendingInvitation("   ")

        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `pending invitation token is consumed only once`() {
        viewModel.setPendingInvitation("invitation-token")

        assertEquals("invitation-token", viewModel.consumePendingInvitation())
        assertEquals("", viewModel.consumePendingInvitation())
    }

    @Test
    fun `clearing pending deep links removes every pending value`() {
        viewModel.setPendingShare(1L)
        viewModel.setPendingInvitation("invitation-token")

        viewModel.clearPendingDeepLinks()

        assertNull(viewModel.consumePendingShare())
        assertEquals("", viewModel.consumePendingInvitation())
    }
}
