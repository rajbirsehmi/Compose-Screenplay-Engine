package com.sehmi.engine.screenplay.robustness

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import java.io.File
import java.io.FileOutputStream

/**
 * Captures diagnostic artifacts (Compose semantics tree dump and screenshot bitmap) when test actions fail.
 */
class ScreenplayDiagnosticsListener(
    private val composeTestRule: ComposeTestRule,
    private val outputDirectory: File = File(System.getProperty("java.io.tmpdir") ?: ".")
) {

    /**
     * Executes a block and captures diagnostics if an exception occurs.
     */
    inline fun <T> captureOnFailure(testName: String, block: () -> T): T {
        return try {
            block()
        } catch (e: Throwable) {
            ScreenplayLogger.logStep("❌ Test '$testName' failed. Capturing diagnostic artifacts...")
            dumpDiagnostics(testName, e)
            throw e
        }
    }

    fun dumpDiagnostics(testName: String, throwable: Throwable) {
        try {
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs()
            }

            // 1. Dump Compose Semantics Tree
            val semanticsDump = try {
                composeTestRule.onRoot().printToString()
            } catch (e: Exception) {
                "Failed to capture semantics: ${e.message}"
            }

            val semanticsFile = File(outputDirectory, "${testName}_semantics.txt")
            semanticsFile.writeText(semanticsDump)
            ScreenplayLogger.logStep("Saved semantics dump to: ${semanticsFile.absolutePath}")

            // 2. Capture Screenshot Bitmap (if hardware capture supported)
            try {
                val bitmap = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
                val bitmapFile = File(outputDirectory, "${testName}_screenshot.png")
                FileOutputStream(bitmapFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                ScreenplayLogger.logStep("Saved screenshot bitmap to: ${bitmapFile.absolutePath}")
            } catch (e: Exception) {
                ScreenplayLogger.logStep("Screenshot capture skipped or unsupported in current test environment: ${e.message}")
            }
        } catch (diagError: Exception) {
            ScreenplayLogger.logStep("Failed to dump diagnostics: ${diagError.message}")
        }
    }
}
