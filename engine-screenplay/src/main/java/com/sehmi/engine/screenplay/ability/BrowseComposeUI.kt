package com.sehmi.engine.screenplay.ability

import androidx.compose.ui.test.junit4.ComposeTestRule

/**
 * An [Ability] that equips an [Actor] with the ability to interact with and query a Jetpack Compose UI
 * via a [ComposeTestRule].
 *
 * Supports both standard [androidx.compose.ui.test.junit4.createComposeRule] and
 * Hilt [androidx.compose.ui.test.junit4.createAndroidComposeRule].
 */
class BrowseComposeUI(
    val composeTestRule: ComposeTestRule
) : Ability {

    companion object {
        /**
         * Convenience factory to create a [BrowseComposeUI] ability from a [ComposeTestRule].
         */
        fun using(composeTestRule: ComposeTestRule): BrowseComposeUI {
            return BrowseComposeUI(composeTestRule)
        }
    }
}
