<<<<<<<< HEAD:design/src/androidTest/java/com/linku/design/ExampleInstrumentedTest.kt
package com.linku.design
========
package com.linku.mypage
>>>>>>>> 6cfa3247fa9a751d3cefb7daf59fb3f6f6c8368c:test/mypage/src/androidTest/java/com/linku/mypage/ExampleInstrumentedTest.kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
<<<<<<<< HEAD:design/src/androidTest/java/com/linku/design/ExampleInstrumentedTest.kt
        assertEquals("com.linku.design.test", appContext.packageName)
========
        assertEquals("com.linku.mypage.test", appContext.packageName)
>>>>>>>> 6cfa3247fa9a751d3cefb7daf59fb3f6f6c8368c:test/mypage/src/androidTest/java/com/linku/mypage/ExampleInstrumentedTest.kt
    }
}