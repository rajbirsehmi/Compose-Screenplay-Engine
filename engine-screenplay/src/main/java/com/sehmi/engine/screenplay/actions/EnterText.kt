package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performTextReplacement
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * An [Action] that enters text into a text field node identified by a test tag.
 */
class EnterText(
    private val text: String,
    private val testTag: String
) : Action {

    override fun description(): String = "Enter text '$text' into node with test tag '$testTag'"

    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        browseUI.composeTestRule.onNode(hasTestTag(testTag)).performTextReplacement(text)
    }

    companion object {
        /**
         * Enters [text] into the node with testTag.
         */
        fun value(text: String): TextBuilder {
            return TextBuilder(text)
        }

        class TextBuilder(private val text: String) {
            fun intoTag(testTag: String): EnterText {
                return EnterText(text, testTag)
            }
        }
    }
}
