package com.sehmi.engine.screenplay.actions

import com.sehmi.engine.screenplay.questions.NodeExistence
import com.sehmi.engine.screenplay.questions.NodeEnabled
import com.sehmi.engine.screenplay.questions.NodeText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ActionsAndQuestionsUnitTest {

    @Test
    fun testActionFactories() {
        val clickTag = Click.onTag("btn_submit")
        assertEquals("Click on node with test tag 'btn_submit'", clickTag.description())

        val clickText = Click.onText("Submit")
        assertEquals("Click on node with text 'Submit'", clickText.description())

        val enter = EnterText.value("hello").intoTag("input_field")
        assertEquals("Enter text 'hello' into node with test tag 'input_field'", enter.description())

        val scroll = Scroll.toTag("scroll_view")
        assertEquals("Scroll to node with test tag 'scroll_view'", scroll.description())

        val wait = Wait.forTag("dialog", 1000L)
        assertEquals("Wait for node with test tag 'dialog' (timeout: 1000ms)", wait.description())
    }

    @Test
    fun testQuestionFactories() {
        assertNotNull(NodeText.ofTag("title"))
        assertNotNull(NodeExistence.existsTag("card"))
        assertNotNull(NodeEnabled.enabledTag("button"))
    }
}
