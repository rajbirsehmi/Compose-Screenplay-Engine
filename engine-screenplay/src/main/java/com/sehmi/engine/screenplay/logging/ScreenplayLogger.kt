package com.sehmi.engine.screenplay.logging

import android.util.Log
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Structured logger for Compose Screenplay Engine integrated with Log4j2 and Android Logcat.
 * Formats and outputs test execution steps, actor actions, tasks, and assertions.
 */
object ScreenplayLogger {
    private const val TAG = "ScreenplayEngine"
    private val logger: Logger = LogManager.getLogger(ScreenplayLogger::class.java)

    /** Whether logging is enabled. */
    var isEnabled: Boolean = true

    /**
     * Logs a test execution step.
     *
     * @param message Step message.
     */
    fun logStep(message: String) {
        if (isEnabled) {
            val formatted = "🎭 [STEP] $message"
            try {
                logger.info(formatted)
            } catch (_: Throwable) {}
            tryLogAndroid { Log.d(TAG, formatted) }
        }
    }

    /**
     * Logs an actor-specific message.
     *
     * @param actorName Name of the actor.
     * @param message Message to log.
     */
    fun logActor(actorName: String, message: String) {
        if (isEnabled) {
            val formatted = "👤 [$actorName] $message"
            try {
                logger.info(formatted)
            } catch (_: Throwable) {}
            tryLogAndroid { Log.d(TAG, formatted) }
        }
    }

    /**
     * Logs an action execution.
     *
     * @param actionDescription Description of the action.
     */
    fun logAction(actionDescription: String) {
        if (isEnabled) {
            val formatted = "⚡ [ACTION] $actionDescription"
            try {
                logger.info(formatted)
            } catch (_: Throwable) {}
            tryLogAndroid { Log.v(TAG, formatted) }
        }
    }

    /**
     * Logs a question query and its result.
     *
     * @param questionDescription Description of the question.
     * @param result Result value.
     */
    fun logQuestion(questionDescription: String, result: Any?) {
        if (isEnabled) {
            val formatted = "❓ [QUESTION] $questionDescription -> $result"
            try {
                logger.info(formatted)
            } catch (_: Throwable) {}
            tryLogAndroid { Log.v(TAG, formatted) }
        }
    }

    /**
     * Logs a consequence evaluation result.
     *
     * @param assertionDescription Description of the consequence.
     * @param passed Whether the consequence passed.
     */
    fun logConsequence(assertionDescription: String, passed: Boolean) {
        if (isEnabled) {
            val status = if (passed) "✅ PASSED" else "❌ FAILED"
            val formatted = "⚖️ [CONSEQUENCE] $status: $assertionDescription"
            try {
                if (passed) logger.info(formatted) else logger.error(formatted)
            } catch (_: Throwable) {}
            tryLogAndroid { Log.i(TAG, formatted) }
        }
    }

    private inline fun tryLogAndroid(block: () -> Unit) {
        try {
            block()
        } catch (_: Throwable) {
            // Android Log not available on pure JVM
        }
    }
}
