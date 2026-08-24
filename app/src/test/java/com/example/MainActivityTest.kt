package com.example

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @Test
    fun testActivityLaunch() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        controller.create().start().resume()
        assert(controller.get() != null)
    }
}
