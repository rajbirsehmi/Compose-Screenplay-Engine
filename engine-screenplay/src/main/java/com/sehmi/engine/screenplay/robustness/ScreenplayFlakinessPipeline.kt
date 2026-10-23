package com.sehmi.engine.screenplay.robustness

import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * Provides automatic retry and flakiness handling for flaky UI interactions or assertions.
 */
object ScreenplayFlakinessPipeline {

    /**
     * Executes the given block, retrying up to [maxRetries] times with a delay if an exception is thrown.
     */
    inline fun <T> withRetry(
        maxRetries: Int = 3,
        delayMillis: Long = 500L,
        block: () -> T
    ): T {
        var currentAttempt = 0
        var lastException: Throwable? = null

        while (currentAttempt < maxRetries) {
            currentAttempt++
            try {
                ScreenplayLogger.logStep("Executing action (Attempt $currentAttempt of $maxRetries)")
                return block()
            } catch (e: Throwable) {
                lastException = e
                ScreenplayLogger.logStep("Attempt $currentAttempt failed: ${e.localizedMessage}. Retrying in ${delayMillis}ms...")
                if (currentAttempt < maxRetries) {
                    try {
                        Thread.sleep(delayMillis)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw ie
                    }
                }
            }
        }
        throw lastException ?: IllegalStateException("Flakiness pipeline failed after $maxRetries attempts.")
    }
}
