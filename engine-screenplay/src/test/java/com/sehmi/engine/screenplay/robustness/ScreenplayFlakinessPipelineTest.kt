package com.sehmi.engine.screenplay.robustness

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenplayFlakinessPipelineTest {

    @Test
    fun testWithRetrySuccessOnFirstAttempt() {
        var attempts = 0
        val result = ScreenplayFlakinessPipeline.withRetry(maxRetries = 3, delayMillis = 10L) {
            attempts++
            "Success"
        }
        assertEquals("Success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun testWithRetrySuccessAfterTransientFailure() {
        var attempts = 0
        val result = ScreenplayFlakinessPipeline.withRetry(maxRetries = 3, delayMillis = 10L) {
            attempts++
            if (attempts < 3) {
                throw RuntimeException("Transient failure")
            }
            "Recovered"
        }
        assertEquals("Recovered", result)
        assertEquals(3, attempts)
    }

    @Test(expected = RuntimeException::class)
    fun testWithRetryExhaustionThrowsException() {
        ScreenplayFlakinessPipeline.withRetry(maxRetries = 2, delayMillis = 10L) {
            throw RuntimeException("Permanent failure")
        }
    }
}
