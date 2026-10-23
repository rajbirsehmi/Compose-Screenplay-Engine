package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollToIndex
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that scrolls a lazy list container to a specific index.
 *
 * @property listTag The test tag of the list container.
 * @property index The target item index.
 */
class ScrollToIndex(private val listTag: String, private val index: Int) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Scroll list '$listTag' to index $index"

    /**
     * Performs the scroll to index action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            browse.composeTestRule.onNode(hasTestTag(listTag)).performScrollToIndex(index)
        }
    }

    companion object {
        /**
         * Specifies the list test tag.
         *
         * @param tag The test tag of the list.
         * @return A [ListScrollBuilder].
         */
        fun listTag(tag: String) = ListScrollBuilder(tag)

        /**
         * Builder for [ScrollToIndex].
         *
         * @property tag The list test tag.
         */
        class ListScrollBuilder(private val tag: String) {
            /**
             * Specifies the target item index.
             *
             * @param index The item index.
             * @return A [ScrollToIndex] instance.
             */
            fun toIndex(index: Int) = ScrollToIndex(tag, index)
        }
    }
}
