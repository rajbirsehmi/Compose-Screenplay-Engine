package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that performs an IME action (e.g. Done, Search, Next) on a text field node identified by a test tag.
 *
 * @property testTag The test tag identifying the text field node.
 */
class PressImeAction(private val testTag: String) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Perform IME action on node '$testTag'"

    /**
     * Performs the IME action as the given [actor].
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
            browse.composeTestRule.onNode(hasTestTag(testTag)).performImeAction()
        }
    }

    companion object {
        /**
         * Creates a [PressImeAction] for the specified test [tag].
         *
         * @param tag The test tag of the target text field.
         * @return A [PressImeAction] instance.
         */
        fun onTag(tag: String) = PressImeAction(tag)
    }
}
