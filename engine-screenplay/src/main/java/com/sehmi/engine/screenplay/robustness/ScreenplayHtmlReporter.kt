package com.sehmi.engine.screenplay.robustness

import java.io.File
import java.util.Base64

/**
 * Generates self-contained HTML failure reports with embedded Base64 screenshots, semantics dumps, and logcat tails.
 */
object ScreenplayHtmlReporter {

    /**
     * Generates a self-contained HTML failure report file.
     *
     * @param testName Name of the test.
     * @param throwable The exception/error causing the failure.
     * @param semanticsDump String dump of the Compose semantics tree.
     * @param screenshotFile Optional screenshot image file.
     * @param logcatDump Optional logcat output tail.
     * @param outputDir Output directory for the report file.
     * @return The generated HTML [File].
     */
    fun generateHtmlReport(
        testName: String,
        throwable: Throwable,
        semanticsDump: String,
        screenshotFile: File?,
        logcatDump: String = "",
        outputDir: File = File(System.getProperty("java.io.tmpdir") ?: ".")
    ): File {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val base64Image = if (screenshotFile != null && screenshotFile.exists()) {
            val bytes = screenshotFile.readBytes()
            Base64.getEncoder().encodeToString(bytes)
        } else {
            ""
        }

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Test Failure Report: $testName</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background: #f4f4f9; color: #333; }
                    h1 { color: #d9534f; }
                    .card { background: white; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    pre { background: #272822; color: #f8f8f2; padding: 15px; border-radius: 5px; overflow-x: auto; max-height: 400px; }
                    img { max-width: 100%; border: 1px solid #ddd; border-radius: 4px; }
                </style>
            </head>
            <body>
                <h1>❌ Test Failed: $testName</h1>
                <div class="card">
                    <h2>Error / Exception</h2>
                    <pre>${throwable.stackTraceToString()}</pre>
                </div>
                <div class="card">
                    <h2>Compose Semantics Tree</h2>
                    <pre>$semanticsDump</pre>
                </div>
                ${if (logcatDump.isNotEmpty()) """
                <div class="card">
                    <h2>Device Logcat Tail</h2>
                    <pre>$logcatDump</pre>
                </div>
                """ else ""}
                ${if (base64Image.isNotEmpty()) """
                <div class="card">
                    <h2>Failure Screenshot</h2>
                    <img src="data:image/png;base64,$base64Image" alt="Failure Screenshot"/>
                </div>
                """ else ""}
            </body>
            </html>
        """.trimIndent()

        val htmlFile = File(outputDir, "${testName}_FAILURE.html")
        htmlFile.writeText(htmlContent)
        return htmlFile
    }
}
