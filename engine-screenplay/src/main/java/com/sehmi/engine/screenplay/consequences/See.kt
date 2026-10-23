package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.questions.Question

/**
 * A [Consequence] that asserts a [Question] satisfies an expected value or condition.
 */
class See<T>(
    private val question: Question<T>,
    private val matcher: (T) -> Boolean,
    private val expectationDescription: String
) : Consequence {

    override fun evaluateAs(actor: Actor) {
        val actual = question.answeredBy(actor)
        val passed = try {
            matcher(actual)
        } catch (e: Exception) {
            false
        }
        ScreenplayLogger.logConsequence("$expectationDescription (Actual: $actual)", passed)
        if (!passed) {
            throw AssertionError("Consequence failed: $expectationDescription. Expected condition to be true, but actual value was '$actual'.")
        }
    }

    companion object {
        /**
         * Asserts that a question matches the expected value.
         */
        fun <T> ifThe(question: Question<T>, matcher: (T) -> Boolean): SeeBuilder<T> {
            return SeeBuilder(question, matcher)
        }

        class SeeBuilder<T>(private val question: Question<T>, private val matcher: (T) -> Boolean) {
            fun expect(description: String): See<T> {
                return See(question, matcher, description)
            }
        }
    }
}
