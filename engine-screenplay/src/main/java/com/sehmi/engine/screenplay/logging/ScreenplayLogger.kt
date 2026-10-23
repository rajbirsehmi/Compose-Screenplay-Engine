package com.sehmi.engine.screenplay.logging

import android.util.Log

/**
 * Structured logger for Compose Screenplay Engine.
 * Formats and outputs test execution steps, actor actions, tasks, and assertions.
 */
object ScreenplayLogger {
    private const val TAG = "ScreenplayEngine"
    var isEnabled: Boolean = true

    fun logStep(message: String) {
        if (isEnabled) {
            try {
                Log.d(TAG, "🎭 [STEP] $message")
            } catch (e: Exception) {
                println("🎭 [STEP] $message")
            }
        }
    }

    fun logActor(actorName: String, message: String) {
        if (isEnabled) {
            try {
                Log.d(TAG, "👤 [$actorName] $message")
            } catch (e: Exception) {
                println("👤 [$actorName] $message")
            }
        }
    }

    fun logAction(actionDescription: String) {
        if (isEnabled) {
            try {
                Log.v(TAG, "⚡ [ACTION] $actionDescription")
            } catch (e: Exception) {
                println("⚡ [ACTION] $actionDescription")
            }
        }
    }

    fun logQuestion(questionDescription: String, result: Any?) {
        if (isEnabled) {
            try {
                Log.v(TAG, "❓ [QUESTION] $questionDescription -> $result")
            } catch (e: Exception) {
                println("❓ [QUESTION] $questionDescription -> $result")
            }
        }
    }

    fun logConsequence(assertionDescription: String, passed: Boolean) {
        if (isEnabled) {
            val status = if (passed) "✅ PASSED" else "❌ FAILED"
            try {
                Log.i(TAG, "⚖️ [CONSEQUENCE] $status: $assertionDescription")
            } catch (e: Exception) {
                println("⚖️ [CONSEQUENCE] $status: $assertionDescription")
            }
        }
    }
}
