package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.down
import androidx.compose.ui.test.moveTo
import androidx.compose.ui.test.up
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that drags from one node and drops onto another node.
 *
 * @property sourceTag The test tag of the source node to drag from.
 * @property targetTag The test tag of the target node to drop onto.
 */
class DragAndDrop(
    private val sourceTag: String,
    private val targetTag: String
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Drag from node '$sourceTag' to node '$targetTag'"

    /**
     * Performs the drag and drop action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            val sourceNode = browse.composeTestRule.onNode(hasTestTag(sourceTag))
            val targetNode = browse.composeTestRule.onNode(hasTestTag(targetTag))
            
            sourceNode.performTouchInput {
                down(center)
                moveTo(targetNode.fetchSemanticsNode().positionInRoot)
                up()
            }
        }
    }

    companion object {
        /**
         * Creates a [DragAndDrop] action from [sourceTag] to [targetTag].
         *
         * @param sourceTag The source test tag.
         * @param targetTag The target test tag.
         * @return A [DragAndDrop] instance.
         */
        fun fromTagToTag(sourceTag: String, targetTag: String): DragAndDrop {
            return DragAndDrop(sourceTag, targetTag)
        }
    }
}
