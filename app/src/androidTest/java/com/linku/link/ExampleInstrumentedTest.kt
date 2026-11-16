package com.linku.link

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // androidTest는 debug 변형에서 실행되므로 applicationIdSuffix(.qa)가 붙는다.
        assertEquals("com.linku.qa", appContext.packageName)
    }
}