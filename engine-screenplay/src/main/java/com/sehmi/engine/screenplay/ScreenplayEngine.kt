package com.sehmi.engine.screenplay

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Global configuration data class for [ScreenplayEngine].
 *
 * @property defaultTimeoutMillis Default timeout in milliseconds for actions and polls.
 * @property pollIntervalMillis Polling interval in milliseconds for retry/wait operations.
 * @property verboseLogging Whether verbose logging is enabled.
 * @property autoCaptureScreenshots Whether to automatically capture screenshots on failure.
 * @property autoDumpSemantics Whether to automatically dump Compose semantics trees on failure.
 * @property autoCaptureLogcat Whether to automatically capture logcat tail on failure.
 * @property logcatTailLines Number of logcat lines to capture on failure.
 */
data class ScreenplayEngineConfiguration(
    val defaultTimeoutMillis: Long = 8000L,
    val pollIntervalMillis: Long = 150L,
    val verboseLogging: Boolean = true,
    val autoCaptureScreenshots: Boolean = true,
    val autoDumpSemantics: Boolean = true,
    val autoCaptureLogcat: Boolean = true,
    val logcatTailLines: Int = 150
)

/**
 * Centralized manager and rule factory for the Compose Screenplay Engine.
 * Automatically wraps and manages [ComposeTestRule] instances to provide zero-config robot execution,
 * automatic idle synchronization, flakiness retry integration, and failure diagnostics.
 */
object ScreenplayEngine {

    /** Type alias for ScreenplayEngineConfiguration. */
    typealias Configuration = ScreenplayEngineConfiguration

    /**
     * Active configuration of the Screenplay Engine.
     */
    var configuration: ScreenplayEngineConfiguration = ScreenplayEngineConfiguration()
        private set

    private val ruleHolder = ThreadLocal<ComposeTestRule>()

    /**
     * Configures global settings for the Screenplay Engine.
     *
     * @param config The [ScreenplayEngineConfiguration] to apply.
     */
    @JvmStatic
    fun configure(config: ScreenplayEngineConfiguration) {
        this.configuration = config
        ScreenplayLogger.isEnabled = config.verboseLogging
    }

    /**
     * Gets or sets the active [ComposeTestRule] for the current test thread.
     */
    var composeTestRule: ComposeTestRule
        get() = ruleHolder.get() ?: error(
            "ComposeTestRule not initialized in ScreenplayEngine. " +
                    "Use @get:Rule val rule = ScreenplayEngine.createRule() or ScreenplayEngine.setComposeRule(rule)."
        )
        set(value) {
            ruleHolder.set(value)
        }

    /**
     * Sets the active [ComposeTestRule].
     *
     * @param rule The [ComposeTestRule] to set.
     */
    fun setComposeRule(rule: ComposeTestRule) {
        ruleHolder.set(rule)
    }

    /**
     * Clears the active [ComposeTestRule].
     */
    fun clearComposeRule() {
        ruleHolder.remove()
    }

    /**
     * Sets the Compose content on the active test rule.
     *
     * @param composable The composable content lambda.
     */
    fun setContent(composable: @Composable () -> Unit) {
        val rule = composeTestRule
        if (rule is ComposeContentTestRule) {
            rule.setContent(composable)
        } else {
            error("Active ComposeTestRule is not a ComposeContentTestRule.")
        }
    }

    /**
     * Creates a standard Compose test rule wrapped with Screenplay Engine lifecycle management.
     *
     * @return A wrapped [ComposeContentTestRule].
     */
    fun createRule(): ComposeContentTestRule {
        return ScreenplayComposeTestRule()
    }

    /**
     * Creates an Android Compose test rule for the specified activity class.
     *
     * @param activityClass The activity class under test.
     * @return An [AndroidComposeTestRule].
     */
    fun <A : ComponentActivity> createAndroidComposeRule(activityClass: Class<A>): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
        return createAndroidComposeRule(activityClass)
    }

    /**
     * Creates a Hilt-compatible Compose test rule for the specified activity class.
     *
     * @param activityClass The activity class under test.
     * @return An [AndroidComposeTestRule].
     */
    fun <A : ComponentActivity> createHiltRule(activityClass: Class<A>): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
        return createAndroidComposeRule(activityClass)
    }
}

/**
 * A JUnit [TestRule] that registers an existing [ComposeTestRule] with [ScreenplayEngine]
 * for the duration of the test.
 *
 * @property composeTestRule The [ComposeTestRule] to manage.
 */
class ScreenplayEngineRule(
    private val composeTestRule: ComposeTestRule
) : TestRule {
    /**
     * Applies the rule statement, setting and clearing the compose test rule in [ScreenplayEngine].
     *
     * @param base The base statement.
     * @param description Test description.
     * @return Wrapped statement.
     */
    override fun apply(base: Statement, description: Description): Statement {
        val testRule = composeTestRule as TestRule
        return object : Statement() {
            override fun evaluate() {
                ScreenplayEngine.setComposeRule(composeTestRule)
                try {
                    testRule.apply(base, description).evaluate()
                } finally {
                    ScreenplayEngine.clearComposeRule()
                }
            }
        }
    }
}

/**
 * A JUnit [ComposeContentTestRule] wrapper that automatically integrates with [ScreenplayEngine].
 *
 * @property delegate The underlying [ComposeContentTestRule].
 */
class ScreenplayComposeTestRule(
    private val delegate: ComposeContentTestRule = createComposeRule()
) : ComposeContentTestRule by delegate, TestRule {
    /**
     * Applies the test rule statement.
     *
     * @param base The base statement.
     * @param description Test description.
     * @return Wrapped statement.
     */
    override fun apply(base: Statement, description: Description): Statement {
        val testRule = delegate as TestRule
        return object : Statement() {
            override fun evaluate() {
                ScreenplayEngine.setComposeRule(delegate as ComposeTestRule)
                try {
                    testRule.apply(base, description).evaluate()
                } finally {
                    ScreenplayEngine.clearComposeRule()
                }
            }
        }
    }
}
