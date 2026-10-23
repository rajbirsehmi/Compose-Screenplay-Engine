# 📚 Compose Screenplay Engine: Technical Reference

This document provides a comprehensive technical reference for all public classes, interfaces, actions, questions, consequences, and robustness utilities provided by the **Compose Screenplay Engine** (`com.sehmi.engine.screenplay`).

---

## 1. Core Architecture

### `Actor` (`com.sehmi.engine.screenplay.actor.Actor`)
Represents a user or system entity interacting with the application under test.
- **`can(vararg abilities: Ability)`**: Equips the actor with capabilities (e.g., `BrowseComposeUI`).
- **`recall<T>()`**: Retrieves a granted ability.
- **`attemptsTo(vararg performables: Performable)`**: Executes actions or composite tasks.
- **`should(vararg consequences: Consequence)`**: Evaluates expectations/assertions.

### `Stage` & `OnStage` (`com.sehmi.engine.screenplay.actor.OnStage`)
Manages active actors during a test scenario session.
- **`theActorCalled(name: String)`**: Retrieves or creates an actor and puts them in the spotlight.
- **`theActorInTheSpotlight()`**: Returns the current actor in focus.
- **`drawTheCurtain()`**: Clears stage state and dismisses all actors.

### `Performable`, `Action`, & `Task` (`com.sehmi.engine.screenplay.core.*`)
- **`Action`**: Represents an atomic UI interaction (click, type, scroll, wait).
- **`Task`**: Represents a composite business workflow (`Task.where("description", action1, action2)`).

---

## 2. Action Library (`com.sehmi.engine.screenplay.actions`)

| Action | Factory Method | Description |
| :--- | :--- | :--- |
| **`Click`** | `Click.onTag("tag")` / `Click.onText("text")` | Clicks on a matching UI node. |
| **`EnterText`** | `EnterText.value("txt").intoTag("tag")` | Types text into a text field. |
| **`Scroll`** | `Scroll.toTag("tag")` | Scrolls view to locate a node. |
| **`Wait`** | `Wait.forTag("tag", timeoutMillis)` | Waits for node existence with timeout. |

---

## 3. Question Library (`com.sehmi.engine.screenplay.questions`)

| Question | Factory Method | Return Type | Description |
| :--- | :--- | :--- | :--- |
| **`NodeText`** | `NodeText.ofTag("tag")` | `String` | Retrieves text/semantics of a node. |
| **`NodeExistence`** | `NodeExistence.existsTag("tag")` | `Boolean` | Checks if node exists in UI tree. |
| **`NodeEnabled`** | `NodeEnabled.enabledTag("tag")` | `Boolean` | Checks if node is enabled. |

---

## 4. Consequences & Assertions (`com.sehmi.engine.screenplay.consequences`)

### `See`
Asserts that a question satisfies a predicate matcher with rich failure error messaging:
```kotlin
See.ifThe(NodeText.ofTag("title")) { it.contains("Dashboard") }
   .expect("Dashboard title should be displayed")
```

---

## 5. Industrial Robustness & Diagnostics (`com.sehmi.engine.screenplay.robustness`)

### `ScreenplayFlakinessPipeline`
Automatically retries flaky UI actions or assertions with delay and backoff:
```kotlin
ScreenplayFlakinessPipeline.withRetry(maxRetries = 3, delayMillis = 200L) {
    // flaky interaction
}
```

### `ScreenplayDiagnosticsListener`
Captures Compose semantics text tree dumps (`_semantics.txt`) and hardware screenshot bitmaps (`_screenshot.png`) upon test failure.

---

## 6. BDD Kotlin DSL (`com.sehmi.engine.screenplay.dsl`)

- **`givenThat(actorName) { ... }`**: Setup actor abilities.
- **`whenUser { attempts(...) }`**: Execute actions and tasks.
- **`thenUser(consequence1, consequence2)`**: Verify assertions.
