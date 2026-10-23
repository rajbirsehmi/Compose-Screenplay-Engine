package com.sehmi.engine.screenplay.actor

/**
 * Static / contextual manager for actors on stage during test execution.
 * Facilitates BDD-style phrasing like `theActorCalled("Alice")` and `theActorInTheSpotlight()`.
 */
object OnStage {
    private val stage = Stage()

    /**
     * Retrieves or creates an actor with the given name and puts them in the spotlight.
     *
     * @param name The name of the actor.
     * @return The [Actor] instance.
     */
    fun theActorCalled(name: String): Actor {
        val actor = stage.actorNamed(name)
        stage.shineSpotlightOn(actor)
        return actor
    }

    /**
     * Returns the actor currently in the spotlight.
     *
     * @return The [Actor] in the spotlight.
     */
    fun theActorInTheSpotlight(): Actor {
        return stage.spotlight()
    }

    /**
     * Resets the stage, dismissing all actors.
     */
    fun drawTheCurtain() {
        stage.clear()
    }
}
