package com.sehmi.engine.screenplay.questions

import androidx.compose.ui.test.hasTestTag
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * A [Question] that checks whether a node exists in the Compose UI tree.
 *
 * @property testTag The test tag of the node.
 */
class NodeExistence(
    private val testTag: String
) : Question<Boolean> {

    /**
     * Answers whether the node exists in the UI tree.
     *
     * @param actor The [Actor] asking the question.
     * @return `true` if the node exists, `false` otherwise.
     */
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
        /**
         * Creates a [NodeExistence] question for the specified [testTag].
         *
         * @param testTag The test tag.
         * @return A [NodeExistence] instance.
         */
        fun existsTag(testTag: String): NodeExistence {
            return NodeExistence(testTag)
        }
    }
}
