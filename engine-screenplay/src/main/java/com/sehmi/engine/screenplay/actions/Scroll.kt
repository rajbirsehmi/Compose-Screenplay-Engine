package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that scrolls to a node identified by a test tag.
 *
 * @property testTag The test tag of the target node.
 */
class Scroll(
    private val testTag: String
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Scroll to node with test tag '$testTag'"

    /**
     * Performs the scroll action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browseUI.composeTestRule) {
            browseUI.composeTestRule.onNode(hasTestTag(testTag)).performScrollTo()
        }
    }

    companion object {
        /**
         * Creates a [Scroll] action to the specified [testTag].
         *
         * @param testTag The test tag.
         * @return A [Scroll] instance.
         */
        fun toTag(testTag: String): Scroll {
            return Scroll(testTag)
        }
    }
}
