package com.sehmi.engine.screenplay.robustness

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import com.sehmi.engine.screenplay.ScreenplayEngine
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import java.io.File
import java.io.FileOutputStream

/**
 * Provides automatic retry, idle synchronization, auto-scrolling, polling, and failure diagnostics
 * for UI actions and assertions.
 */
object ScreenplayFlakinessPipeline {

    /**
     * Executes the given action block robustly with idle synchronization, retry polling,
     * and automatic failure logging/diagnostics using [ScreenplayEngine.configuration].
     *
     * @param T Return type of the block.
     * @param composeTestRule The [ComposeTestRule] instance.
     * @param maxRetries Maximum number of attempts.
     * @param delayMillis Delay in milliseconds between retry attempts.
     * @param block The action block to execute.
     * @return The result of the block if successful.
     */
    inline fun <T> runRobustly(
        composeTestRule: ComposeTestRule,
        maxRetries: Int = (ScreenplayEngine.configuration.defaultTimeoutMillis / ScreenplayEngine.configuration.pollIntervalMillis).toInt().coerceAtLeast(1),
        delayMillis: Long = ScreenplayEngine.configuration.pollIntervalMillis,
        crossinline block: () -> T
    ): T {
        var currentAttempt = 0
        var lastException: Throwable? = null

        while (currentAttempt < maxRetries) {
            currentAttempt++
            try {
                // 1. Idle Synchronization (Pre-Action)
                composeTestRule.waitForIdle()

                // Execute action block
                val result = block()

                // Idle Synchronization (Post-Action)
                composeTestRule.waitForIdle()
                return result
            } catch (e: Throwable) {
                lastException = e
                ScreenplayLogger.logStep("Attempt $currentAttempt of $maxRetries failed: ${e.localizedMessage}. Retrying in ${delayMillis}ms...")
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

        // Failure Capture & Diagnostic Suite based on configuration
        val config = ScreenplayEngine.configuration
        val testName = "ScreenplayAction"

        val semantics = if (config.autoDumpSemantics) {
            try {
                composeTestRule.onRoot().printToString()
            } catch (ex: Throwable) {
                "Semantics unavailable: ${ex.localizedMessage}"
            }
        } else {
            "Semantics dump disabled in configuration"
        }

        val logcatDump = if (config.autoCaptureLogcat) {
            try {
                val process = Runtime.getRuntime().exec("logcat -d -t ${config.logcatTailLines}")
                process.inputStream.bufferedReader().use { it.readText() }
            } catch (ex: Throwable) {
                "Logcat capture unavailable: ${ex.localizedMessage}"
            }
        } else {
            "Logcat capture disabled in configuration"
        }

        val screenshotFile = if (config.autoCaptureScreenshots) {
            try {
                val bitmap = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
                val tempFile = File.createTempFile("failure_screenshot_", ".png")
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                tempFile
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }

        val htmlReport = ScreenplayHtmlReporter.generateHtmlReport(
            testName = testName,
            throwable = lastException ?: IllegalStateException("Action failed after $maxRetries attempts."),
            semanticsDump = semantics,
            screenshotFile = screenshotFile,
            logcatDump = logcatDump
        )
        ScreenplayLogger.logStep("📊 [DIAGNOSTICS] Generated HTML failure report at: ${htmlReport.absolutePath}")

        throw lastException ?: IllegalStateException("Flakiness pipeline failed after $maxRetries attempts.")
    }

    /**
     * Legacy withRetry support.
     *
     * @param T Return type of the block.
     * @param maxRetries Maximum retry attempts.
     * @param delayMillis Delay between attempts.
     * @param block The block to execute.
     * @return Result of the block.
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
