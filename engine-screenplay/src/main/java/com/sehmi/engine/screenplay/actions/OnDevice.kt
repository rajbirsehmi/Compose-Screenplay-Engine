package com.sehmi.engine.screenplay.actions

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action
import com.sehmi.engine.screenplay.logging.ScreenplayLogger
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline

/**
 * An [Action] wrapper that executes OS-level UIAutomator operations (`onDevice`).
 */
class OnDevice private constructor(
    private val descriptionText: String,
    private val deviceAction: (UiDevice) -> Unit
) : Action {

    /**
     * Returns a human-readable description of this action.
     */
    override fun description(): String = descriptionText

    /**
     * Performs the device-level action as the given [actor].
     *
     * @param actor The [Actor] executing the action.
     */
    override fun perform(actor: Actor) {
        ScreenplayLogger.logAction(descriptionText)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        deviceAction(device)
    }

    companion object {
        /** Press hardware Back button. */
        fun pressBack(): OnDevice = OnDevice("Press hardware Back button") { it.pressBack() }
        /** Press hardware Home button. */
        fun pressHome(): OnDevice = OnDevice("Press hardware Home button") { it.pressHome() }
        /** Open Recent Apps / Overview screen. */
        fun pressRecentApps(): OnDevice = OnDevice("Open Recent Apps") { it.pressRecentApps() }
        /** Rotate screen to portrait mode. */
        fun rotateScreenPortrait(): OnDevice = OnDevice("Rotate screen to portrait") { it.setOrientationPortrait() }
        /** Rotate screen to landscape mode. */
        fun rotateScreenLandscape(): OnDevice = OnDevice("Rotate screen to landscape") { it.setOrientationLandscape() }
        /** Wake up device screen. */
        fun wakeUp(): OnDevice = OnDevice("Wake up device screen") { it.wakeUp() }
        /** Put device screen to sleep. */
        fun sleep(): OnDevice = OnDevice("Put device to sleep") { it.sleep() }
        /** Executes a shell command on the device. */
        fun executeShell(command: String): OnDevice = OnDevice("Execute shell command: $command") { it.executeShellCommand(command) }
    }
}
