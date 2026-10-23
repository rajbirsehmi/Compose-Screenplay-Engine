package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.questions.Question
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsequenceAndSeeTest {

    private class DummyQuestion(val value: String) : Question<String> {
        override fun answeredBy(actor: Actor): String = value
    }

    @Test
    fun testSeeConsequenceSuccess() {
        val question = DummyQuestion("Hello World")
        val consequence = See.ifThe(question) { it.contains("Hello") }.expect("Text should contain Hello")
        val actor = Actor("Alice")
        consequence.evaluateAs(actor)
        assertTrue(true)
    }

    @Test(expected = AssertionError::class)
    fun testSeeConsequenceFailureThrowsAssertionError() {
        val question = DummyQuestion("Goodbye")
        val consequence = See.ifThe(question) { it.contains("Hello") }.expect("Text should contain Hello")
        val actor = Actor("Alice")
        consequence.evaluateAs(actor)
    }
}
