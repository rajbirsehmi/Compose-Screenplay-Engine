package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * An [Action] that scrolls to a node identified by a test tag.
 */
class Scroll(
    private val testTag: String
) : Action {

    override fun description(): String = "Scroll to node with test tag '$testTag'"

    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browseUI = actor.recall<BrowseComposeUI>()
        browseUI.composeTestRule.onNode(hasTestTag(testTag)).performScrollTo()
    }

    companion object {
        fun toTag(testTag: String): Scroll {
            return Scroll(testTag)
        }
    }
}
