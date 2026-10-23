package com.sehmi.composescreenplayengine

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sehmi.engine.screenplay.ScreenplayEngine
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actions.*
import com.sehmi.engine.screenplay.consequences.See
import com.sehmi.engine.screenplay.dsl.givenThat
import com.sehmi.engine.screenplay.dsl.thenUser
import com.sehmi.engine.screenplay.dsl.whenUser
import com.sehmi.engine.screenplay.questions.NodeEnabled
import com.sehmi.engine.screenplay.questions.NodeExistence
import com.sehmi.engine.screenplay.questions.NodeText
import com.sehmi.engine.screenplay.robustness.FailureDiagnosticWatcher
import com.sehmi.engine.screenplay.robustness.ScreenplayFlakinessPipeline
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenplayEngineTest {

    @get:Rule
    val composeTestRule = ScreenplayEngine.createRule()

    @get:Rule
    val diagnosticWatcher = FailureDiagnosticWatcher(
        semanticsDumpProvider = { "Mock semantics dump" },
        screenshotProvider = { null }
    )

    @Test
    fun testGesturesAndTouchInteractions() {
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        givenThat("Alice") {
            can(BrowseComposeUI.using())
        }

        whenUser {
            attemptsTo(
                Click.onTag("btn_single_click"),
                Click.onText("Click Me By Text"),
                DoubleClick.onTag("btn_double_click"),
                LongClick.onTag("btn_long_click"),
                ClickFirstChild.ofParent("parent_container")
            )
        }

        thenUser(
            See.ifThe(NodeText.ofTag("status_text")) { it.contains("First Child Clicked") }
                .expect("Status banner should show First Child Clicked")
        )
    }

    @Test
    fun testTextEntryAndKeyboardOperations() {
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        givenThat("Alice") {
            can(BrowseComposeUI.using())
        }

        whenUser {
            attemptsTo(
                Click.onText("Text/Key"),
                RequestFocus.onTag("input_username"),
                EnterText.value("InitialUser").intoTag("input_username"),
                ReplaceText.value("ReplacedContent").inTag("input_replace"),
                ClearText.ofTag("input_clear"),
                RequestFocus.onTag("input_focus"),
                PressImeAction.onTag("input_ime")
            )
        }

        thenUser(
            See.ifThe(NodeText.ofTag("txt_ime_status")) { it.contains("Search Triggered") }
                .expect("IME status should show Search Triggered")
        )
    }

    @Test
    fun testListsAndDragDropInteractions() {
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        givenThat("Bob") {
            can(BrowseComposeUI())
        }

        whenUser {
            attemptsTo(
                Click.onText("Lists/Drag"),
                DragAndDrop.fromTagToTag("drag_source", "drop_target"),
                ScrollToIndex.listTag("lazy_list").toIndex(15)
            )
        }

        thenUser(
            See.ifThe(NodeExistence.existsTag("list_item_15")) { it }
                .expect("List item 15 should exist after scrolling")
        )
    }

    @Test
    fun testClockAndStateControls() {
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        givenThat("Bob") {
            can(BrowseComposeUI())
        }

        whenUser {
            attemptsTo(
                Click.onText("Clock/OS"),
                AdvanceTime.byMillis(10000L),
                Click.onTag("switch_toggle")
            )
        }

        thenUser(
            See.ifThe(NodeEnabled.enabledTag("target_toggle_button")) { !it }
                .expect("Target toggle button should be disabled after toggling switch off")
        )
    }

    @Test
    fun testFullBddWorkflowAndThemeToggle() {
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        givenThat("Alice") {
            can(BrowseComposeUI.using())
        }

        ScreenplayFlakinessPipeline.withRetry(maxRetries = 2, delayMillis = 50L) {
            whenUser {
                attemptsTo(
                    Click.onTag("btn_theme_toggle"),
                    Click.onText("BDD/Diag"),
                    EnterText.value("EnterpriseAdmin").intoTag("input_username"),
                    Click.onTag("btn_login")
                )
            }
        }

        thenUser(
            See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }
                .expect("Welcome screen should be displayed"),
            See.ifThe(NodeText.ofTag("txt_welcome")) { it.contains("EnterpriseAdmin") }
                .expect("Welcome screen should greet EnterpriseAdmin")
        )
    }
}
