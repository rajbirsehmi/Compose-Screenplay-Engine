package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents any unit of work that an [Actor] can perform.
 * This is the base interface for both atomic [Action]s and composite [Task]s.
 */
interface Performable {
    /**
     * Executes this performable as the given [actor].
     *
     * @param actor The [Actor] executing the work.
     */
    fun performAs(actor: Actor)
}
