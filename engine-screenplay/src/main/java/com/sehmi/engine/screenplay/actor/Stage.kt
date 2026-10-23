package com.sehmi.engine.screenplay.actor

/**
 * Manages the collection of active [Actor]s during a test session.
 */
class Stage {
    private val actors: MutableMap<String, Actor> = mutableMapOf()
    private var spotlightActor: Actor? = null

    /**
     * Finds or creates an actor with the given name.
     */
    fun actorNamed(name: String): Actor {
        return actors.getOrPut(name) {
            Actor(name).also {
                if (spotlightActor == null) {
                    spotlightActor = it
                }
            }
        }
    }

    /**
     * Sets the actor currently in the spotlight.
     */
    fun shineSpotlightOn(actor: Actor) {
        spotlightActor = actor
        actors[actor.name] = actor
    }

    /**
     * Gets the actor currently in the spotlight, throwing an exception if none is set.
     */
    fun spotlight(): Actor {
        return spotlightActor
            ?: throw IllegalStateException("No actor is currently in the spotlight. Call OnStage.theActorCalled(...) first.")
    }

    /**
     * Dismisses all actors on stage and resets stage state.
     */
    fun clear() {
        actors.values.forEach { it.dismiss() }
        actors.clear()
        spotlightActor = null
    }
}
