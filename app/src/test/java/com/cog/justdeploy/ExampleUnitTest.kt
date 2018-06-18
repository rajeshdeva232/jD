package com.cog.justdeploy

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    /**
     *function
     */
    @Test
    fun additionisCorrect() {
        assertEquals(expected, actualDigit + actualDigit)
    }
    companion object {
        val expected=4
        val actualDigit=2
    }
}
