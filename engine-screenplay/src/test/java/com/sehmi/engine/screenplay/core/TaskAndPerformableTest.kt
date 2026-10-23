package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskAndPerformableTest {

    private class TestAction(val name: String) : Action {
        var performedCount = 0
        override fun description(): String = "Action $name"
        override fun perform(actor: Actor) {
            performedCount++
        }
    }

    @Test
    fun testTaskCompositionAndExecution() {
        val action1 = TestAction("One")
        val action2 = TestAction("Two")

        val task = Task.where("Execute two actions", action1, action2)
        assertEquals("Execute two actions", task.description())

        val actor = Actor("Bob")
        task.performAs(actor)

        assertEquals(1, action1.performedCount)
        assertEquals(1, action2.performedCount)
    }
}
