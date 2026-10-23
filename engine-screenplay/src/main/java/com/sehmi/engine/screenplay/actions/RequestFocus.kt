package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.requestFocus
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that requests focus for a node identified by a test tag.
 *
 * @property testTag The test tag of the target node.
 */
class RequestFocus(private val testTag: String) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Request focus for node '$testTag'"

    /**
     * Performs the request focus action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            try {
                browse.composeTestRule.onNode(hasTestTag(testTag)).performScrollTo()
            } catch (_: Throwable) {}
            browse.composeTestRule.onNode(hasTestTag(testTag)).requestFocus()
        }
    }

    companion object {
        /**
         * Creates a [RequestFocus] action for the specified test [tag].
         *
         * @param tag The test tag.
         * @return A [RequestFocus] instance.
         */
        fun onTag(tag: String) = RequestFocus(tag)
    }
}
