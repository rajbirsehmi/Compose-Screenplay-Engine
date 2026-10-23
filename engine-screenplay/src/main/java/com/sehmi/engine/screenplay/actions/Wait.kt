package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * An [Action] that waits for a node with a given test tag to appear.
 */
class Wait(
    private val testTag: String,
    private val timeoutMillis: Long = 5000L
) : Action {

    override fun description(): String = "Wait for node with test tag '$testTag' (timeout: ${timeoutMillis}ms)"

    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        browseUI.composeTestRule.waitUntil(timeoutMillis) {
            try {
                browseUI.composeTestRule.onNode(hasTestTag(testTag)).assertExists()
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    companion object {
        fun forTag(testTag: String, timeoutMillis: Long = 5000L): Wait {
            return Wait(testTag, timeoutMillis)
        }
    }
}
