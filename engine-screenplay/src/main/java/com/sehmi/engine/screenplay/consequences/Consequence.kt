package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents an assertion or expectation evaluated by an [Actor] during a test scenario.
 */
interface Consequence {
    /**
     * Evaluates the consequence as the given [actor].
     *
     * @param actor The [Actor] evaluating the consequence.
     */
    fun evaluateAs(actor: Actor)
}
