package com.sehmi.engine.screenplay.questions

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.printToString
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * A [Question] that retrieves the text of a Compose UI node.
 */
class NodeText private constructor(
    private val description: String,
    private val queryNode: (BrowseComposeUI) -> SemanticsNodeInteraction
) : Question<String> {

    /**
     * Answers the text value of the node.
     *
     * @param actor The [Actor] asking the question.
     * @return The extracted text string or print representation.
     */
    override fun answeredBy(actor: Actor): String {
        val browseUI = actor.recall<BrowseComposeUI>()
        val interaction = queryNode(browseUI)
        val textValue = try {
            val semanticsNode = interaction.fetchSemanticsNode()
            val textList = semanticsNode.config.getOrNull(SemanticsProperties.Text)
            if (!textList.isNullOrEmpty()) {
                textList.joinToString { it.text }
            } else {
                interaction.printToString()
            }
        } catch (e: Exception) {
            ""
        }
        ScreenplayLogger.logQuestion(description, textValue)
        return textValue
    }

    companion object {
        /**
         * Creates a [NodeText] question for the specified [testTag].
         *
         * @param testTag The test tag of the target node.
         * @return A [NodeText] instance.
         */
        fun ofTag(testTag: String): NodeText {
            return NodeText("Text of node with tag '$testTag'") { browse ->
                browse.composeTestRule.onNode(hasTestTag(testTag))
            }
        }
    }
}
