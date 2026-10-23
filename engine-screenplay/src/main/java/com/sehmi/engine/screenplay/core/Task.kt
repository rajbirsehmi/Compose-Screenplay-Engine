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
     *
     * @return Task description string.
     */
    abstract fun description(): String

    /**
     * Returns the list of constituent performables that make up this task.
     *
     * @return List of [Performable] activities.
     */
    protected abstract fun actionsToPerform(): List<Performable>

    /**
     * Executes all constituent performables of this task sequentially.
     *
     * @param actor The [Actor] executing the task.
     */
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
         *
         * @param description Task description.
         * @param performables Vararg list of performables.
         * @return A composite [Task] instance.
         */
        fun where(description: String, vararg performables: Performable): Task {
            return object : Task() {
                override fun description(): String = description
                override fun actionsToPerform(): List<Performable> = performables.toList()
            }
        }
    }
}
