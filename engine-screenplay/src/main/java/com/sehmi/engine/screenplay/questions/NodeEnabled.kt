package com.sehmi.engine.screenplay.questions

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasTestTag
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * A [Question] that checks whether a node is enabled.
 *
 * @property testTag The test tag identifying the node.
 */
class NodeEnabled(
    private val testTag: String
) : Question<Boolean> {

    /**
     * Answers whether the node with the test tag is enabled.
     *
     * @param actor The [Actor] asking the question.
     * @return `true` if enabled, `false` otherwise.
     */
    override fun answeredBy(actor: Actor): Boolean {
        val browseUI = actor.recall<BrowseComposeUI>()
        val isEnabled = try {
            val semanticsNode = browseUI.composeTestRule.onNode(hasTestTag(testTag)).fetchSemanticsNode()
            val isDisabled = semanticsNode.config.getOrNull(SemanticsProperties.Disabled) != null
            !isDisabled
        } catch (e: Throwable) {
            false
        }
        ScreenplayLogger.logQuestion("Is node with tag '$testTag' enabled", isEnabled)
        return isEnabled
    }

    companion object {
        /**
         * Creates a [NodeEnabled] question for the specified [testTag].
         *
         * @param testTag The test tag.
         * @return A [NodeEnabled] instance.
         */
        fun enabledTag(testTag: String): NodeEnabled {
            return NodeEnabled(testTag)
        }
    }
}
