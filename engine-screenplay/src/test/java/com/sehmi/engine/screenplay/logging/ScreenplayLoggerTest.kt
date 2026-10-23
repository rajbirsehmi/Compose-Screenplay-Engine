package com.sehmi.engine.screenplay.logging

import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenplayLoggerTest {

    @Test
    fun testLoggerTogglesAndLoggingMethods() {
        ScreenplayLogger.isEnabled = false
        ScreenplayLogger.logStep("Test step")
        ScreenplayLogger.logActor("Alice", "Test action")
        ScreenplayLogger.logAction("Click button")
        ScreenplayLogger.logQuestion("Is visible", true)
        ScreenplayLogger.logConsequence("Check text", true)

        ScreenplayLogger.isEnabled = true
        ScreenplayLogger.logStep("Test step enabled")
        assertTrue(ScreenplayLogger.isEnabled)
    }
}
