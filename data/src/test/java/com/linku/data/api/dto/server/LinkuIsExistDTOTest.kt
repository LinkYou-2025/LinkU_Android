package com.linku.data.api.dto.server

import com.linku.data.serializer.OffsetDateTimeSerializer
import com.squareup.moshi.Moshi
import org.junit.Assert.assertFalse
import org.junit.Test

class LinkuIsExistDTOTest {

    @Test
    fun `isExist field maps without legacy link id`() {
        val adapter = Moshi.Builder()
            .add(OffsetDateTimeSerializer())
            .build()
            .adapter(LinkuIsExistDTO::class.java)

        val result = requireNotNull(adapter.fromJson("""{"isExist":false}"""))

        assertFalse(requireNotNull(result.isExist))
    }
}
