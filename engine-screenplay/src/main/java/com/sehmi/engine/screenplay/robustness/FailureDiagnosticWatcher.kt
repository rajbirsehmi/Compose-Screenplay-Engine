package com.sehmi.engine.screenplay.robustness

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

/**
 * JUnit TestWatcher that automatically captures diagnostic HTML reports and screenshots upon test failure.
 *
 * @property semanticsDumpProvider Provider lambda returning semantics tree dump string.
 * @property screenshotProvider Provider lambda returning screenshot File.
 */
class FailureDiagnosticWatcher(
    private val semanticsDumpProvider: () -> String = { "Semantics not provided" },
    private val screenshotProvider: () -> File? = { null }
) : TestWatcher() {

    /**
     * Called when a test fails, generating an HTML diagnostic failure report.
     *
     * @param e The exception/throwable that caused the failure.
     * @param description Test description.
     */
    override fun failed(e: Throwable, description: Description) {
        val testName = description.methodName ?: "UnknownTest"
        val semantics = semanticsDumpProvider()
        val screenshot = screenshotProvider()

        val htmlReport = ScreenplayHtmlReporter.generateHtmlReport(
            testName = testName,
            throwable = e,
            semanticsDump = semantics,
            screenshotFile = screenshot
        )
        println("📊 [DIAGNOSTICS] Generated HTML failure report at: ${htmlReport.absolutePath}")
    }
}
