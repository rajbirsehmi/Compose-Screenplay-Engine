package com.sehmi.composescreenplayengine

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actions.Click
import com.sehmi.engine.screenplay.actions.EnterText
import com.sehmi.engine.screenplay.consequences.See
import com.sehmi.engine.screenplay.dsl.givenThat
import com.sehmi.engine.screenplay.dsl.thenUser
import com.sehmi.engine.screenplay.dsl.whenUser
import com.sehmi.engine.screenplay.questions.NodeExistence
import com.sehmi.engine.screenplay.questions.NodeText
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenplayEngineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginWorkflowUsingScreenplayEngine() {
        composeTestRule.setContent {
            SampleLoginScreen()
        }

        givenThat("Alice") {
            can(BrowseComposeUI.using(composeTestRule))
        }

        whenUser {
            attemptsTo(
                EnterText.value("Rajbir").intoTag("input_username"),
                Click.onTag("btn_login")
            )
        }

        thenUser(
            See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }.expect("Welcome message should exist"),
            See.ifThe(NodeText.ofTag("txt_welcome")) { it.contains("Rajbir") }.expect("Welcome text should contain Rajbir")
        )
    }

    @Test
    fun testFlakinessPipelineRecovery() {
        composeTestRule.setContent {
            SampleLoginScreen()
        }

        givenThat("Bob") {
            can(BrowseComposeUI.using(composeTestRule))
        }

        ScreenplayFlakinessPipeline.withRetry(maxRetries = 2, delayMillis = 100L) {
            whenUser {
                attemptsTo(
                    EnterText.value("RobustUser").intoTag("input_username"),
                    Click.onTag("btn_login")
                )
            }
        }

        thenUser(
            See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }.expect("Welcome banner should be visible")
        )
    }
}
