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
 * An [Action] that replaces text in a text field node identified by a test tag.
 *
 * @property text The new text replacement.
 * @property testTag The test tag of the target text field.
 */
class ReplaceText(private val text: String, private val testTag: String) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Replace text in '$testTag' with '$text'"

    /**
     * Performs the replace text action as the given [actor].
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
            browse.composeTestRule.onNode(hasTestTag(testTag)).performTextReplacement(text)
        }
    }

    companion object {
        /**
         * Specifies the text value for replacement.
         *
         * @param text The replacement text.
         * @return A [TextReplacer] builder.
         */
        fun value(text: String) = TextReplacer(text)

        /**
         * Builder for [ReplaceText].
         *
         * @property text The replacement text.
         */
        class TextReplacer(private val text: String) {
            /**
             * Specifies the target test tag.
             *
             * @param tag The test tag.
             * @return A [ReplaceText] instance.
             */
            fun inTag(tag: String) = ReplaceText(text, tag)
        }
    }
}
