package com.sehmi.engine.screenplay.questions

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents a question asked by an [Actor] to query state from the application under test.
 */
interface Question<T> {
    /**
     * Answers the question as the given [actor].
     */
    fun answeredBy(actor: Actor): T
}
