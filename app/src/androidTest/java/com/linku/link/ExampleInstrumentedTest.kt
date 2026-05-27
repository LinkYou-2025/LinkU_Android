<<<<<<<< HEAD:app/src/androidTest/java/com/linku/link/ExampleInstrumentedTest.kt
package com.linku.link
========
package com.linku
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:app/src/androidTest/java/com/linku/ExampleInstrumentedTest.kt

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
<<<<<<<< HEAD:app/src/androidTest/java/com/linku/link/ExampleInstrumentedTest.kt
        assertEquals("com.linku.link", appContext.packageName)
========
        assertEquals("com.linku", appContext.packageName)
>>>>>>>> fd1304faab6b86e04c17e31a0786ce151290d292:app/src/androidTest/java/com/linku/ExampleInstrumentedTest.kt
    }
}