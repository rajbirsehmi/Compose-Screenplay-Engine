package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents an assertion or expectation evaluated by an [Actor] during a test scenario.
 */
interface Consequence {
    /**
     * Evaluates the consequence as the given [actor].
     */
    fun evaluateAs(actor: Actor)
}
