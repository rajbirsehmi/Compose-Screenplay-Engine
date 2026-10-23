package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents an atomic action performed by an [Actor] against the UI or system under test.
 */
interface Action : Performable {
    /**
     * Returns a human-readable description of this action for logging and reporting.
     */
    fun description(): String

    override fun performAs(actor: Actor) {
        perform(actor)
    }

    /**
     * Performs the atomic action.
     */
    fun perform(actor: Actor)
}
