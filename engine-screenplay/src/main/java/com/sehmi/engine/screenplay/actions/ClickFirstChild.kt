package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that clicks the first child of a parent node identified by a test tag.
 *
 * @property parentTag The test tag of the parent node.
 */
class ClickFirstChild(private val parentTag: String) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Click first child of parent node '$parentTag'"

    /**
     * Performs the click action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            browse.composeTestRule.onNode(hasTestTag(parentTag)).onChildren().onFirst().performClick()
        }
    }

    companion object {
        /**
         * Creates a [ClickFirstChild] action for the specified parent [tag].
         *
         * @param tag The test tag of the parent container.
         * @return A [ClickFirstChild] instance.
         */
        fun ofParent(tag: String) = ClickFirstChild(tag)
    }
}
