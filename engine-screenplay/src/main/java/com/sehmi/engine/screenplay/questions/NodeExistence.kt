package com.sehmi.engine.screenplay.questions

import androidx.compose.ui.test.hasTestTag
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * A [Question] that checks whether a node exists in the Compose UI tree.
 */
class NodeExistence(
    private val testTag: String
) : Question<Boolean> {

    override fun answeredBy(actor: Actor): Boolean {
        val browseUI = actor.recall<BrowseComposeUI>()
        val exists = try {
            browseUI.composeTestRule.onNode(hasTestTag(testTag)).assertExists()
            true
        } catch (e: Exception) {
            false
        }
        ScreenplayLogger.logQuestion("Existence of node with tag '$testTag'", exists)
        return exists
    }

    companion object {
        fun existsTag(testTag: String): NodeExistence {
            return NodeExistence(testTag)
        }
    }
}
