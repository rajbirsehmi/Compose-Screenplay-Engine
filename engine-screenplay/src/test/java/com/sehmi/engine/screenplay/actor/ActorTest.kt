package com.sehmi.engine.screenplay.actor

import com.sehmi.engine.screenplay.ability.Ability
import com.sehmi.engine.screenplay.core.Action
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ActorTest {

    private class DummyAbility : Ability {
        var tornDown = false
        override fun tearDown() {
            tornDown = true
        }
    }

    private class DummyAction : Action {
        var performed = false
        override fun description(): String = "Dummy action"
        override fun perform(actor: Actor) {
            performed = true
        }
    }

    @Test
    fun testActorAbilitiesAndExecution() {
        val ability = DummyAbility()
        val actor = Actor("Alice").can(ability)

        assertEquals("Alice", actor.name)
        val recalled = actor.recall<DummyAbility>()
        assertNotNull(recalled)

        val action = DummyAction()
        actor.attemptsTo(action)
        assertEquals(true, action.performed)

        actor.dismiss()
        assertEquals(true, ability.tornDown)
    }

    @Test(expected = IllegalStateException::class)
    fun testMissingAbilityThrowsException() {
        val actor = Actor("Charlie")
        actor.recall<DummyAbility>()
    }
}
