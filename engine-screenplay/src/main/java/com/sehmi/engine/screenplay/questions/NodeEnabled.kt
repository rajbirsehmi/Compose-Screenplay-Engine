package com.sehmi.engine.screenplay.questions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.assertIsEnabled
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * A [Question] that checks whether a node is enabled.
 */
class NodeEnabled(
    private val testTag: String
) : Question<Boolean> {

    override fun answeredBy(actor: Actor): Boolean {
        val browseUI = actor.recall<BrowseComposeUI>()
        val isEnabled = try {
            browseUI.composeTestRule.onNode(hasTestTag(testTag)).assertIsEnabled()
            true
        } catch (e: Exception) {
            false
        }
        ScreenplayLogger.logQuestion("Is node with tag '$testTag' enabled", isEnabled)
        return isEnabled
    }

    companion object {
        fun enabledTag(testTag: String): NodeEnabled {
            return NodeEnabled(testTag)
        }
    }
}
