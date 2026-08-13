package com.linku.core.model.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlarmTypeTest {

    @Test
    fun `summary completion notification maps to link alarm`() {
        assertEquals(AlarmType.LINK, AlarmType.from("LINK_SUMMARY_COMPLETE"))
    }

    @Test
    fun `unknown notification can be ignored by direct navigation`() {
        assertNull(AlarmType.fromOrNull("UNKNOWN"))
    }
}
