package com.sehmi.engine.screenplay.dsl

import com.sehmi.engine.screenplay.actor.OnStage
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.actor.Actor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenplayDslTest {

    private class TestAction : Action {
        var performed = false
        override fun description(): String = "Test action"
        override fun perform(actor: Actor) {
            performed = true
        }
    }

    @After
    fun tearDown() {
        OnStage.drawTheCurtain()
    }

    @Test
    fun testDslFlow() {
        val action = TestAction()
        givenThat("Alice") {
            // setup
        }
        whenUser {
            attempts(action)
        }
        assertEquals(true, action.performed)
    }
}
