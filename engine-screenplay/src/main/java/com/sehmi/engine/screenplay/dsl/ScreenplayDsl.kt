package com.sehmi.engine.screenplay.dsl

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.actor.OnStage
import com.sehmi.engine.screenplay.core.Performable
import com.sehmi.engine.screenplay.consequences.Consequence

/**
 * BDD Given clause for setting up actor and pre-conditions.
 */
inline fun givenThat(actorName: String, block: Actor.() -> Unit): Actor {
    val actor = OnStage.theActorCalled(actorName)
    actor.block()
    return actor
}

/**
 * BDD When clause for executing actions or tasks.
 */
inline fun whenUser(block: Actor.() -> Unit): Actor {
    val actor = OnStage.theActorInTheSpotlight()
    actor.block()
    return actor
}

/**
 * BDD Then clause for evaluating consequences.
 */
inline fun thenUser(vararg consequences: Consequence): Actor {
    val actor = OnStage.theActorInTheSpotlight()
    actor.should(*consequences)
    return actor
}

/**
 * Extension helper to allow actor to attempt performables inside DSL blocks.
 */
fun Actor.attempts(vararg performables: Performable): Actor {
    return attemptsTo(*performables)
}
