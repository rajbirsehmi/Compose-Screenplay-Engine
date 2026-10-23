package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that waits for a node with a given test tag to appear.
 *
 * @property testTag The test tag to wait for.
 * @property timeoutMillis Maximum timeout in milliseconds to wait.
 */
class Wait(
    private val testTag: String,
    private val timeoutMillis: Long = 5000L
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Wait for node with test tag '$testTag' (timeout: ${timeoutMillis}ms)"

    /**
     * Performs the wait action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browseUI.composeTestRule, maxRetries = (timeoutMillis / 200L).toInt().coerceAtLeast(1), delayMillis = 200L) {
            browseUI.composeTestRule.onNode(hasTestTag(testTag)).assertExists()
        }
    }

    companion object {
        /**
         * Creates a [Wait] action for the specified [testTag] and [timeoutMillis].
         *
         * @param testTag The test tag to wait for.
         * @param timeoutMillis Maximum timeout in milliseconds.
         * @return A [Wait] instance.
         */
        fun forTag(testTag: String, timeoutMillis: Long = 5000L): Wait {
            return Wait(testTag, timeoutMillis)
        }
    }
}
