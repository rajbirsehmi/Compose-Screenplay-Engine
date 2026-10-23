package com.sehmi.engine.screenplay.ability

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.sehmi.engine.screenplay.ScreenplayEngine

/**
 * An [Ability] that equips an [Actor] with the ability to interact with and query a Jetpack Compose UI
 * via a [ComposeTestRule].
 *
 * Supports both standard [androidx.compose.ui.test.junit4.createComposeRule] and
 * Hilt [androidx.compose.ui.test.junit4.createAndroidComposeRule].
 *
 * @property composeTestRule The [ComposeTestRule] instance used to interact with Compose nodes.
 */
class BrowseComposeUI(
    val composeTestRule: ComposeTestRule
) : Ability {

    /**
     * Secondary constructor using the active [ScreenplayEngine.composeTestRule].
     */
    constructor() : this(ScreenplayEngine.composeTestRule)

    companion object {
        /**
         * Convenience factory to create a [BrowseComposeUI] ability from a [ComposeTestRule].
         *
         * @param composeTestRule The [ComposeTestRule] to use.
         * @return A [BrowseComposeUI] instance.
         */
        fun using(composeTestRule: ComposeTestRule): BrowseComposeUI {
            return BrowseComposeUI(composeTestRule)
        }

        /**
         * Convenience factory to create a [BrowseComposeUI] ability using the active [ScreenplayEngine.composeTestRule].
         *
         * @return A [BrowseComposeUI] instance.
         */
        fun using(): BrowseComposeUI {
            return BrowseComposeUI(ScreenplayEngine.composeTestRule)
        }
    }
}
