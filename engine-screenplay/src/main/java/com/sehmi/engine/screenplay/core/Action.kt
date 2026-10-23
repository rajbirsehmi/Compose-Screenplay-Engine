package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents an atomic action performed by an [Actor] against the UI or system under test.
 */
interface Action : Performable {
    /**
     * Returns a human-readable description of this action for logging and reporting.
     *
     * @return Action description string.
     */
    fun description(): String

    /**
     * Executes this action as the given [actor].
     *
     * @param actor The [Actor] performing the action.
     */
    override fun performAs(actor: Actor) {
        perform(actor)
    }

    /**
     * Performs the atomic action.
     *
     * @param actor The [Actor] performing the action.
     */
    fun perform(actor: Actor)
}
