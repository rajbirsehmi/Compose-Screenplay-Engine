package com.sehmi.engine.screenplay.questions

import com.sehmi.engine.screenplay.actor.Actor

/**
 * Represents a question asked by an [Actor] to query state from the application under test.
 *
 * @param T The return type of the question answer.
 */
interface Question<T> {
    /**
     * Answers the question as the given [actor].
     *
     * @param actor The [Actor] asking the question.
     * @return The answer value of type [T].
     */
    fun answeredBy(actor: Actor): T
}
