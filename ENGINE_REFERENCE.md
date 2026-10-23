# 📚 Compose Screenplay Engine: Technical Reference

This document provides a comprehensive technical reference for all public classes, interfaces, actions, questions, consequences, robustness utilities, and lint guardrails provided by the **Compose Screenplay Engine** (`com.sehmi.engine.screenplay`).

---

## 📑 Table of Contents
1. [Global Configuration & Log4j2 Logging](#1-global-configuration--log4j2-logging)
2. [Core Architecture & Screenplay Semantics](#2-core-architecture--screenplay-semantics)
3. [Centralized Lifecycle & Rule Management (`ScreenplayEngine`)](#3-centralized-lifecycle--rule-management-screenplayengine)
4. [Action Library (`com.sehmi.engine.screenplay.actions`)](#4-action-library-comsehmienginescreenplayactions)
5. [Question Library (`com.sehmi.engine.screenplay.questions`)](#5-question-library-comsehmienginescreenplayquestions)
6. [Consequences & Assertions (`com.sehmi.engine.screenplay.consequences`)](#6-consequences--assertions-comsehmienginescreenplayconsequences)
7. [Industrial Robustness & The `runRobustly` Pipeline](#7-industrial-robustness--the-runrobustly-execution-pipeline)
8. [BDD Kotlin DSL (`com.sehmi.engine.screenplay.dsl`)](#8-bdd-kotlin-dsl-comsehmienginescreenplaydsl)
9. [Static Analysis & Lint Guardrails (`:engine-lint`)](#9-static-analysis--lint-guardrails-engine-lint)

---

## 1. Global Configuration & Log4j2 Logging

Configure engine-wide behavior—such as default timeouts, polling intervals, verbose Log4j2 logging, auto-screenshotting, semantics tree dumping, and logcat tail lines—globally using `ScreenplayEngine.configure`:

```kotlin
@BeforeClass
@JvmStatic
fun setupEngine() {
    ScreenplayEngine.configure(
        ScreenplayEngine.Configuration(
            defaultTimeoutMillis = 8000L,
            pollIntervalMillis = 150L,
            verboseLogging = true,
            autoCaptureScreenshots = true,
            autoDumpSemantics = true,
            autoCaptureLogcat = true,
            logcatTailLines = 150
        )
    )
}
```

### Configuration Options
- **`defaultTimeoutMillis`**: Maximum duration (ms) for robust action attempts (default: `8000L`).
- **`pollIntervalMillis`**: Delay (ms) between retry attempts (default: `150L`).
- **`verboseLogging`**: Enables/disables Screenplay console and Log4j2 step logs (default: `true`).
- **`autoCaptureScreenshots`**: Enables Base64 screenshot capture on failure (default: `true`).
- **`autoDumpSemantics`**: Enables Compose semantics tree dumping on failure (default: `true`).
- **`autoCaptureLogcat`**: Enables Logcat tail capture on failure (default: `true`).
- **`logcatTailLines`**: Number of logcat lines to capture on failure (default: `150`).

### Log4j2 Engine Logging
When `verboseLogging = true`, **`ScreenplayLogger`** uses `org.apache.logging.log4j.LogManager` to log every workflow event:
- **Steps**: `🎭 [STEP] ...`
- **Actor Operations**: `👤 [ActorName] ...`
- **Actions**: `⚡ [ACTION] ...`
- **Questions**: `❓ [QUESTION] ... -> Result`
- **Consequences**: `⚖️ [CONSEQUENCE] ✅ PASSED: ...` or `❌ FAILED: ...`

---

## 2. Core Architecture & Screenplay Semantics

The **Screenplay Pattern** structures tests around real-world user behavior, separating *who* is performing actions (`Actor`), *what* capabilities they have (`Ability`), *what* they do (`Action` / `Task`), and *what* they verify (`Question` / `Consequence`).

### `Actor` (`com.sehmi.engine.screenplay.actor.Actor`)
Models the user under test.
- **`can(vararg abilities: Ability)`**: Grants capabilities to the actor (e.g., `BrowseComposeUI`).
- **`recall<T>()`**: Retrieves a granted ability.
- **`attemptsTo(vararg performables: Performable)`**: Executes atomic actions or composite business tasks.
- **`should(vararg consequences: Consequence)`**: Evaluates expectations against the UI state.

### `Stage` & `OnStage` (`com.sehmi.engine.screenplay.actor.OnStage`)
Manages active actors during multi-user or scenario test sessions.
- **`theActorCalled(name: String)`**: Retrieves or creates an actor and brings them into focus.
- **`theActorInTheSpotlight()`**: Returns the current active actor.
- **`drawTheCurtain()`**: Resets stage state between tests.

### `Performable`, `Action`, & `Task` (`com.sehmi.engine.screenplay.core.*`)
- **`Action`**: Represents an atomic UI interaction.
- **`Task`**: Represents a composite business workflow (`Task.where("description", action1, action2)`).

---

## 3. Centralized Lifecycle & Rule Management (`ScreenplayEngine`)

`ScreenplayEngine` provides zero-config rule factory methods, thread-safe rule holders, and centralized content loading.

| API / Method | Signature | Description |
| :--- | :--- | :--- |
| **`createRule()`** | `fun createRule(): ComposeContentTestRule` | Creates a wrapped Compose test rule with automatic lifecycle registration. |
| **`setContent()`** | `fun setContent(composable: @Composable () -> Unit)` | Sets the Composable UI content on the active test rule. |
| **`setComposeRule()`** | `fun setComposeRule(rule: ComposeTestRule)` | Manually registers a custom test rule. |
| **`clearComposeRule()`** | `fun clearComposeRule()` | Clears the active rule binding. |
| **`createAndroidComposeRule()`**| `fun <A> createAndroidComposeRule(Class<A>): AndroidComposeTestRule` | Creates an Android activity Compose test rule. |
| **`createHiltRule()`** | `fun <A> createHiltRule(Class<A>): AndroidComposeTestRule` | Creates a Hilt-integrated Compose test rule. |

---

## 4. Action Library (`com.sehmi.engine.screenplay.actions`)

Every action in the engine is robustly executed through the `runRobustly` pipeline, ensuring pre/post idle synchronization, auto-scrolling, polling retries, and automatic diagnostics.

| Action Category | Action & Factory Method | Description |
| :--- | :--- | :--- |
| **Clicks & Touches** | `Click.onTag("tag")` / `Click.onText("text")` | Clicks a matching UI node with auto-scroll. |
| | `LongClick.onTag("tag")` / `LongClick.onText("text")` | Performs a long click gesture. |
| | `DoubleClick.onTag("tag")` / `DoubleClick.onText("text")` | Performs a double click gesture. |
| | `DragAndDrop.fromTagToTag("src", "tgt")` | Drags source node and drops onto target. |
| | `ClickFirstChild.ofParent("parent")` | Clicks the first child of a parent node. |
| **Text Entry & Focus** | `EnterText.value("txt").intoTag("tag")` | Types text into a text field. |
| | `ReplaceText.value("txt").inTag("tag")` | Replaces text in a text field. |
| | `ClearText.ofTag("tag")` | Clears text from a text field. |
| | `RequestFocus.onTag("tag")` | Requests keyboard focus for a node. |
| | `PressImeAction.onTag("tag")` | Triggers editor IME action (e.g. Search/Done). |
| **Scrolling** | `Scroll.toTag("tag")` | Scrolls container to locate a node. |
| | `ScrollToIndex.listTag("list").toIndex(i)` | Scrolls lazy list to specific index. |
| | `ScrollAndClick.toTag("tag")` | Scrolls node into view and clicks it. |
| **Synchronization** | `Wait.forTag("tag", timeoutMillis)` | Waits for node existence up to timeout. |
| **System & Clock** | `AdvanceTime.byMillis(1000L)` | Advances the Compose test clock. |
| | `OnDevice.pressBack()` / `pressHome()` / `wakeUp()` | UIAutomator OS-level orchestration. |

---

## 5. Question Library (`com.sehmi.engine.screenplay.questions`)

| Question | Factory Method | Return Type | Description |
| :--- | :--- | :--- | :--- |
| **`NodeText`** | `NodeText.ofTag("tag")` | `String` | Retrieves semantic text of a node. |
| **`NodeExistence`** | `NodeExistence.existsTag("tag")` | `Boolean` | Checks if node exists in UI tree. |
| **`NodeEnabled`** | `NodeEnabled.enabledTag("tag")` | `Boolean` | Checks if node is enabled/interactive. |

---

## 6. Consequences & Assertions (`com.sehmi.engine.screenplay.consequences`)

### `See`
Asserts that a question satisfies a predicate matcher with rich error messaging:
```kotlin
See.ifThe(NodeText.ofTag("title")) { it.contains("Dashboard") }
   .expect("Dashboard title should be displayed")
```

---

## 7. Industrial Robustness & The `runRobustly` Execution Pipeline

`ScreenplayFlakinessPipeline.runRobustly` guarantees deterministic test execution by wrapping every interaction in a 4-stage pipeline:
1. **Idle Synchronization**: `composeTestRule.waitForIdle()` before and after execution.
2. **Auto-Scrolling**: `performScrollTo()` attempt prior to interaction.
3. **Polling & Retry**: Transient failure recovery using configured `defaultTimeoutMillis` and `pollIntervalMillis`.
4. **Failure Capture**: Automatic generation of `_semantics.txt` dumps, Logcat tail logs, and self-contained `_FAILURE.html` reports upon failure.

---

## 8. BDD Kotlin DSL (`com.sehmi.engine.screenplay.dsl`)

- **`givenThat(actorName) { can(...) }`**: Initializes actor and abilities.
- **`whenUser { attemptsTo(...) }`**: Executes actions and tasks.
- **`thenUser(consequence1, consequence2)`**: Evaluates assertions.

---

## 9. Static Analysis & Lint Guardrails (`:engine-lint`)

The `:engine-lint` module provides **`DirectUiTestApiUsageDetector`**, which flags raw Compose testing API calls (`performClick`, `performTextReplacement`, etc.) in test code, enforcing that all interactions go through Screenplay actions and robustness pipelines.
