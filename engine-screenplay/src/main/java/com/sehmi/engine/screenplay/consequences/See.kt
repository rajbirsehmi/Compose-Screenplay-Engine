package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.questions.Question

/**
 * A [Consequence] that asserts a [Question] satisfies an expected value or condition.
 *
 * @param T The type of value returned by the question.
 * @property question The [Question] being asked.
 * @property matcher The predicate validating the answer.
 * @property expectationDescription Description of the expectation.
 */
class See<T>(
    private val question: Question<T>,
    private val matcher: (T) -> Boolean,
    private val expectationDescription: String
) : Consequence {

    /**
     * Evaluates the consequence as the given [actor], checking the question result against the matcher.
     *
     * @param actor The [Actor] evaluating the consequence.
     */
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
         *
         * @param T The question return type.
         * @param question The [Question] to ask.
         * @param matcher The matching predicate.
         * @return A [SeeBuilder] instance.
         */
        fun <T> ifThe(question: Question<T>, matcher: (T) -> Boolean): SeeBuilder<T> {
            return SeeBuilder(question, matcher)
        }

        /**
         * Builder for [See].
         *
         * @param T The question return type.
         * @property question The [Question].
         * @property matcher The matching predicate.
         */
        class SeeBuilder<T>(private val question: Question<T>, private val matcher: (T) -> Boolean) {
            /**
             * Sets the expectation description for the consequence.
             *
             * @param description Human-readable description.
             * @return A [See] consequence instance.
             */
            fun expect(description: String): See<T> {
                return See(question, matcher, description)
            }
        }
    }
}
