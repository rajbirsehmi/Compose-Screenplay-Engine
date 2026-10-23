package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger

/**
 * Represents a composite sequence of [Performable] activities (actions or sub-tasks)
 * grouped together to model business-level user flows (e.g., LoginTask, CheckoutTask).
 */
abstract class Task : Performable {

    /**
     * Returns a description of this task.
     */
    abstract fun description(): String

    /**
     * Executes all constituent performables of this task sequentially.
     */
    protected abstract fun actionsToPerform(): List<Performable>

    override fun performAs(actor: Actor) {
        ScreenplayLogger.logStep("Starting Task: ${description()}")
        for (action in actionsToPerform()) {
            action.performAs(actor)
        }
        ScreenplayLogger.logStep("Completed Task: ${description()}")
    }

    companion object {
        /**
         * Creates an inline composite [Task] from a list of performables and a description.
         */
        fun where(description: String, vararg performables: Performable): Task {
            return object : Task() {
                override fun description(): String = description
                override fun actionsToPerform(): List<Performable> = performables.toList()
            }
        }
    }
}
