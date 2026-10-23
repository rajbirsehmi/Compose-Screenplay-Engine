package com.sehmi.engine.screenplay.actor

import com.sehmi.engine.screenplay.ability.Ability
import com.sehmi.engine.screenplay.core.Performable
import com.sehmi.engine.screenplay.consequences.Consequence
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import kotlin.reflect.KClass

/**
 * Represents a user or system entity interacting with the application under test in Screenplay scenarios.
 * An actor possesses [Ability]s and can perform [Performable]s (actions and tasks) or evaluate questions.
 */
class Actor(val name: String) {
    private val abilities: MutableMap<KClass<out Ability>, Ability> = mutableMapOf()

    /**
     * Grants one or more [Ability]s to this actor.
     */
    fun can(vararg newAbilities: Ability): Actor {
        for (ability in newAbilities) {
            abilities[ability::class] = ability
            ScreenplayLogger.logActor(name, "Gained ability: ${ability::class.simpleName}")
        }
        return this
    }

    /**
     * Retrieves an ability of the specified class possessed by this actor.
     * Throws an [IllegalStateException] if the actor does not possess the ability.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Ability> recallAbilityTo(abilityClass: KClass<T>): T {
        return abilities[abilityClass] as? T
            ?: throw IllegalStateException("Actor '$name' does not possess ability ${abilityClass.simpleName}. Make sure to equip the actor with .can(...) before use.")
    }

    /**
     * Convenience generic method to recall an ability.
     */
    inline fun <reified T : Ability> recall(): T {
        return recallAbilityTo(T::class)
    }

    /**
     * Instructs the actor to perform one or more [Performable] activities (actions or tasks).
     */
    fun attemptsTo(vararg performables: Performable): Actor {
        ScreenplayLogger.logActor(name, "Attempts to execute ${performables.size} activities.")
        for (performable in performables) {
            performable.performAs(this)
        }
        return this
    }

    /**
     * Evaluates one or more [Consequence] assertions.
     */
    fun should(vararg consequences: Consequence): Actor {
        for (consequence in consequences) {
            consequence.evaluateAs(this)
        }
        return this
    }

    /**
     * Clears all abilities and cleans up resources.
     */
    fun dismiss() {
        ScreenplayLogger.logActor(name, "Dismissing actor and tearing down abilities.")
        abilities.values.forEach { it.tearDown() }
        abilities.clear()
    }
}
