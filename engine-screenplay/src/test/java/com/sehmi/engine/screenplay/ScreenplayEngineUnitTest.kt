package com.sehmi.engine.screenplay

import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import androidx.compose.ui.test.junit4.ComposeTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class ScreenplayEngineUnitTest {

    @After
    fun tearDown() {
        ScreenplayEngine.clearComposeRule()
    }

    @Test
    fun testGlobalEngineConfiguration() {
        ScreenplayEngine.configure(
            ScreenplayEngine.Configuration(
                defaultTimeoutMillis = 8000L,
                pollIntervalMillis = 150L,
                verboseLogging = false,
                autoCaptureScreenshots = true,
                autoDumpSemantics = true,
                autoCaptureLogcat = true,
                logcatTailLines = 150
            )
        )

        val config = ScreenplayEngine.configuration
        assertEquals(8000L, config.defaultTimeoutMillis)
        assertEquals(150L, config.pollIntervalMillis)
        assertEquals(false, config.verboseLogging)
        assertEquals(true, config.autoCaptureScreenshots)
        assertEquals(true, config.autoDumpSemantics)
        assertEquals(true, config.autoCaptureLogcat)
        assertEquals(150, config.logcatTailLines)
    }

    @Test(expected = IllegalStateException::class)
    fun testUninitializedComposeTestRuleThrowsException() {
        ScreenplayEngine.composeTestRule
    }

    @Test
    fun testSetAndClearComposeRule() {
        val mockRule = mock<ComposeTestRule>()
        ScreenplayEngine.setComposeRule(mockRule)
        assertEquals(mockRule, ScreenplayEngine.composeTestRule)

        val ability = BrowseComposeUI.using()
        assertEquals(mockRule, ability.composeTestRule)

        ScreenplayEngine.clearComposeRule()
    }
}
