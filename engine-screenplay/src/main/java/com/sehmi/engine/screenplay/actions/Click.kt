package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * An [Action] that clicks on a UI node matching a test tag or text.
 */
class Click private constructor(
    private val descriptionText: String,
    private val performAction: (BrowseComposeUI) -> Unit
) : Action {

    override fun description(): String = descriptionText

    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(descriptionText)
        val browseUI = actor.recall<BrowseComposeUI>()
        performAction(browseUI)
    }

    companion object {
        /**
         * Clicks on a node identified by the given test tag.
         */
        fun onTag(testTag: String): Click {
            return Click("Click on node with test tag '$testTag'") { browse ->
                browse.composeTestRule.onNode(hasTestTag(testTag)).performClick()
            }
        }

        /**
         * Clicks on a node identified by the given text.
         */
        fun onText(text: String): Click {
            return Click("Click on node with text '$text'") { browse ->
                browse.composeTestRule.onNode(hasText(text)).performClick()
            }
        }
    }
}
