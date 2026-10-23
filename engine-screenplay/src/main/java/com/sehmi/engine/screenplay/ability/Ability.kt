package com.sehmi.engine.screenplay.ability

/**
 * Represents a capability or resource that an [Actor] can use during a screenplay execution
 * (e.g., browsing a Compose UI, making HTTP calls, accessing local storage).
 */
interface Ability {
    /**
     * Cleans up resources associated with this ability when the actor is dismissed or reset.
     */
    fun tearDown() {}
}
