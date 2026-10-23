package com.sehmi.engine.screenplay.actions

import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] that controls the Compose test clock (e.g., advancing time).
 *
 * @property millis The duration in milliseconds to advance the test clock.
 */
class AdvanceTime(private val millis: Long) : Action {
    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = "Advance Compose test clock by ${millis}ms"

    /**
     * Performs the advance time action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(description())
        val browse = actor.recall<BrowseComposeUI>()
        ScreenplayFlakinessPipeline.runRobustly(browse.composeTestRule) {
            browse.composeTestRule.mainClock.advanceTimeBy(millis)
        }
    }

    companion object {
        /**
         * Creates an [AdvanceTime] action for the specified [millis].
         *
         * @param millis Milliseconds to advance.
         * @return An [AdvanceTime] instance.
         */
        fun byMillis(millis: Long) = AdvanceTime(millis)
    }
}
