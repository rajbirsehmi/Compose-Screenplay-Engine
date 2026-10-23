package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that enters text into a text field node identified by a test tag.
 *
 * @property text The text string to enter.
 * @property testTag The test tag identifying the target text field.
 */
class EnterText(
    private val text: String,
    private val testTag: String
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Enter text '$text' into node with test tag '$testTag'"

    /**
     * Performs the enter text action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browseUI.composeTestRule) {
            try {
                browseUI.composeTestRule.onNode(hasTestTag(testTag)).performScrollTo()
            } catch (_: Throwable) {}
            browseUI.composeTestRule.onNode(hasTestTag(testTag)).performTextReplacement(text)
        }
    }

    companion object {
        /**
         * Specifies the text value to enter.
         *
         * @param text The text string.
         * @return A [TextBuilder] to chain the target test tag.
         */
        fun value(text: String): TextBuilder {
            return TextBuilder(text)
        }

        /**
         * Builder helper for [EnterText].
         *
         * @property text The text string to enter.
         */
        class TextBuilder(private val text: String) {
            /**
             * Specifies the destination test tag.
             *
             * @param testTag The test tag of the target text field.
             * @return An [EnterText] action instance.
             */
            fun intoTag(testTag: String): EnterText {
                return EnterText(text, testTag)
            }
        }
    }
}
