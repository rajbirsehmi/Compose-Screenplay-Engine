package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.performScrollTo
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that performs a double click on a UI node matching a test tag or text.
 */
class DoubleClick private constructor(
    private val descriptionText: String,
    private val performAction: (BrowseComposeUI) -> Unit
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = descriptionText

    /**
     * Performs the double click action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(descriptionText)
        val browseUI = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browseUI.composeTestRule) {
            performAction(browseUI)
        }
    }

    companion object {
        /**
         * Double clicks on a node identified by the given test tag.
         *
         * @param testTag The test tag of the target node.
         * @return A [DoubleClick] instance.
         */
        fun onTag(testTag: String): DoubleClick {
            return DoubleClick("Double click on node with test tag '$testTag'") { browse ->
                try {
                    browse.composeTestRule.onNode(hasTestTag(testTag)).performScrollTo()
                } catch (_: Throwable) {}
                browse.composeTestRule.onNode(hasTestTag(testTag)).performTouchInput { doubleClick() }
            }
        }

        /**
         * Double clicks on a node identified by the given text.
         *
         * @param text The text displayed on the target node.
         * @return A [DoubleClick] instance.
         */
        fun onText(text: String): DoubleClick {
            return DoubleClick("Double click on node with text '$text'") { browse ->
                try {
                    browse.composeTestRule.onNode(hasText(text)).performScrollTo()
                } catch (_: Throwable) {}
                browse.composeTestRule.onNode(hasText(text)).performTouchInput { doubleClick() }
            }
        }
    }
}
