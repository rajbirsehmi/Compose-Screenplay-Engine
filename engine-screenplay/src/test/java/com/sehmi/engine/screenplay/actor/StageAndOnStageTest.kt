package com.sehmi.engine.screenplay.actor

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class StageAndOnStageTest {

    @After
    fun tearDown() {
        OnStage.drawTheCurtain()
    }

    @Test
    fun testStageManagementAndSpotlight() {
        val stage = Stage()
        val actor1 = stage.actorNamed("Alice")
        assertEquals("Alice", actor1.name)

        assertEquals(actor1, stage.spotlight())

        val actor2 = stage.actorNamed("Bob")
        stage.shineSpotlightOn(actor2)
        assertEquals(actor2, stage.spotlight())

        stage.clear()
    }

    @Test
    fun testOnStageContext() {
        val alice = OnStage.theActorCalled("Alice")
        assertEquals("Alice", alice.name)
        assertEquals(alice, OnStage.theActorInTheSpotlight())
    }
}
