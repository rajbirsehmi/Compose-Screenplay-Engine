package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that scrolls to a node identified by a test tag and then clicks it.
 *
 * @property testTag The test tag of the target node.
 */
class ScrollAndClick(private val testTag: String) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Scroll to node '$testTag' and click"

    /**
     * Performs the scroll and click action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            val node = browse.composeTestRule.onNode(hasTestTag(testTag))
            node.performScrollTo()
            node.performClick()
        }
    }

    companion object {
        /**
         * Creates a [ScrollAndClick] action for the given test [tag].
         *
         * @param tag The test tag.
         * @return A [ScrollAndClick] instance.
         */
        fun toTag(tag: String) = ScrollAndClick(tag)
    }
}
