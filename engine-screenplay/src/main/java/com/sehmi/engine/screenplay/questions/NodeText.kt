package com.sehmi.engine.screenplay.questions

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

    override fun answeredBy(actor: Actor): String {
        val browseUI = actor.recall<BrowseComposeUI>()
        val interaction = queryNode(browseUI)
        val textValue = try {
            interaction.printToString()
        } catch (e: Exception) {
            ""
        }
        ScreenplayLogger.logQuestion(description, textValue)
        return textValue
    }

    companion object {
        fun ofTag(testTag: String): NodeText {
            return NodeText("Text of node with tag '$testTag'") { browse ->
                browse.composeTestRule.onNode(hasTestTag(testTag))
            }
        }
    }
}
