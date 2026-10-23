# 🎭 Compose Screenplay Engine — Architectural Specification

## 📌 Executive Summary

**Compose Screenplay Engine** (`compose-screenplay-engine` or sub-module `:engine-screenplay`) is a user-centric test automation framework for **Jetpack Compose** built upon the **Screenplay Pattern**.

While the standard `UI Automation Engine` (`:engine`) leverages the **Robot Pattern**, this module brings **Actor/Task/Action/Question** semantics to Android UI testing while sharing the core industrial robustness pipeline (flakiness retry loops, Compose clock synchronization, and automatic diagnostic artifacts on failure).

---

## 🏷️ Proposed Project & Package Identity

* **Project Name**: `Compose Screenplay Engine`
* **Artifact ID**: `com.sehmi.engine:engine-screenplay`
* **Package Base**: `com.sehmi.engine.screenplay`
* **Target Platforms**: Android API 30+, Jetpack Compose, Java 17, Kotlin 2.x

---

## 📦 Project Type & Build Configuration

### 1. Project Classification
* **Type**: **Android Library (`com.android.library`)**
* **Output Format**: Android Archive (`.aar`) published to Maven/JitPack.
* **Scope**: UI Test Automation Dependency for Android projects (`androidTestImplementation`).

> [!NOTE]
> **Why an Android Library?**
> The Screenplay engine requires direct access to Android SDK APIs (`android.graphics.Bitmap`), Android instrumentation, and Android Jetpack Compose runtime dependencies (`androidx.compose.ui.test`). Therefore, it must be compiled using the Android Gradle Plugin (AGP) as an Android Library rather than a pure Kotlin JVM library.

### 2. Gradle Integration Options

#### Option A: Sub-Module in Multi-Module Repo (Recommended)
Defined as a Gradle sub-project module `:engine-screenplay` in `settings.gradle.kts`:
```kotlin
// settings.gradle.kts
include(":engine")            // Robot Pattern Engine
include(":engine-screenplay") // Screenplay Pattern Engine
include(":engine-lint")       // Custom Lint Rules
```

#### Option B: Standalone Library Repository
Created as an independent Git repository (`UI-Automation-Engine-Screenplay`) configured with `com.android.library` plugin and published to Maven Central/JitPack.

### 3. Key Dependencies (`build.gradle.kts`)
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.sehmi.engine.screenplay"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Shared Core Robustness Engine
    implementation(project(":engine"))

    // Jetpack Compose Testing APIs
    implementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.compose.ui.test.manifest)

    // Core AndroidX & Coroutines
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
```

---

## 🏗️ Architectural Core Concepts

```
                  ┌──────────────────────┐
                  │        Actor         │
                  │   ("User / Tester")  │
                  └──────────┬───────────┘
                             │
              ┌──────────────┴──────────────┐
              │ uses                        │ performs
              ▼                             ▼
   ┌────────────────────┐        ┌────────────────────┐
   │      Ability       │        │    Performable     │
   │ (BrowseComposeUI)  │        ├──────────┬─────────┤
   └────────────────────┘        │  Task    │ Action  │
                                 └──────────┴─────────┘
                                            │
                                            ▼
                                 ┌────────────────────┐
                                 │      Question      │
                                 │   ("Assertions")   │
                                 └────────────────────┘
```

1. **Actor**: The test subject performing actions (`Actor.named("Alice")`). Holds a list of **Abilities**.
2. **Ability**: Encapsulates external drivers and capabilities (`BrowseComposeUI` holding `ComposeTestRule`).
3. **Performable**: Base interface for anything an Actor can perform.
   - **Task**: High-level user workflow (composed of sub-tasks or actions).
   - **Action**: Low-level atomic UI interaction (`Click`, `EnterText`, `Scroll`).
4. **Question**: Queries system state (`NodeText`, `NodeVisibility`) to produce an answer for validation.
5. **Consequence / See**: Verifies Questions using matchers or conditions (`See.if(NodeText("title"), equalsTo("Dashboard"))`).

---

## 📁 Directory & File Structure

```
engine-screenplay/
├── build.gradle.kts
└── src/
    ├── main/
    │   └── java/com/sehmi/engine/screenplay/
    │       ├── actor/
    │       │   ├── Actor.kt
    │       │   ├── Stage.kt
    │       │   └── OnStage.kt
    │       ├── ability/
    │       │   ├── Ability.kt
    │       │   └── BrowseComposeUI.kt
    │       ├── core/
    │       │   ├── Performable.kt
    │       │   ├── Task.kt
    │       │   └── Action.kt
    │       ├── actions/
    │       │   ├── Click.kt
    │       │   ├── EnterText.kt
    │       │   ├── Scroll.kt
    │       │   ├── Swipe.kt
    │       │   └── Wait.kt
    │       ├── questions/
    │       │   ├── Question.kt
    │       │   ├── NodeText.kt
    │       │   ├── NodeExistence.kt
    │       │   ├── NodeEnabled.kt
    │       │   └── NodeBounds.kt
    │       ├── consequences/
    │       │   ├── Consequence.kt
    │       │   └── See.kt
    │       ├── robustness/
    │       │   ├── ScreenplayFlakinessPipeline.kt
    │       │   └── ScreenplayDiagnosticsListener.kt
    │       └── dsl/
    │           └── ScreenplayDsl.kt
    └── androidTest/
        └── java/com/sehmi/engine/screenplay/
            └── ScreenplayEngineTest.kt
```

---

## 📑 Complete Class & Interface Specifications

### 1. `ability/Ability.kt` & `ability/BrowseComposeUI.kt`
Wraps the `ComposeTestRule` and links into the engine's flakiness pipeline.

```kotlin
package com.sehmi.engine.screenplay.ability

import androidx.compose.ui.test.junit4.ComposeTestRule

interface Ability

class BrowseComposeUI(val composeRule: ComposeTestRule) : Ability {
    companion object {
        fun with(composeRule: ComposeTestRule): BrowseComposeUI = BrowseComposeUI(composeRule)
    }
}
```

---

### 2. `core/Performable.kt`, `core/Task.kt`, & `core/Action.kt`

```kotlin
package com.sehmi.engine.screenplay.core

import com.sehmi.engine.screenplay.actor.Actor

fun interface Performable {
    fun performAs(actor: Actor)
}

interface Action : Performable

abstract class Task : Performable {
    companion object {
        fun where(description: String, vararg steps: Performable): Task = object : Task() {
            override fun performAs(actor: Actor) {
                steps.forEach { step -> actor.attemptsTo(step) }
            }
        }
    }
}
```

---

### 3. `actor/Actor.kt` & `actor/OnStage.kt`
The central orchestrator for running tasks and checking consequences.

```kotlin
package com.sehmi.engine.screenplay.actor

import com.sehmi.engine.screenplay.ability.Ability
import com.sehmi.engine.screenplay.consequences.Consequence
import com.sehmi.engine.screenplay.core.Performable
import kotlin.reflect.KClass

class Actor(val name: String) {

    private val abilities = mutableMapOf<KClass<out Ability>, Ability>()

    fun <T : Ability> can(ability: T): Actor {
        abilities[ability::class] = ability
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Ability> using(abilityClass: KClass<T>): T {
        return abilities[abilityClass] as? T
            ?: throw IllegalStateException("Actor $name does not have the ability to ${abilityClass.simpleName}")
    }

    fun attemptsTo(vararg tasks: Performable) {
        tasks.forEach { task ->
            task.performAs(this)
        }
    }

    fun should(vararg consequences: Consequence) {
        consequences.forEach { consequence ->
            consequence.evaluateFor(this)
        }
    }

    companion object {
        fun named(name: String): Actor = Actor(name)
    }
}
```

---

### 4. `actions/Click.kt` & `actions/EnterText.kt`
Concrete atomic UI actions with automatic Compose idling and test tag resolution.

```kotlin
package com.sehmi.engine.screenplay.actions

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.core.Action

class ClickOnTag(private val tag: String) : Action {
    override fun performAs(actor: Actor) {
        val compose = actor.using(BrowseComposeUI::class).composeRule
        compose.onNodeWithTag(tag).performClick()
    }

    companion object {
        fun tag(tag: String): ClickOnTag = ClickOnTag(tag)
    }
}

class EnterText(private val tag: String, private val text: String) : Action {
    override fun performAs(actor: Actor) {
        val compose = actor.using(BrowseComposeUI::class).composeRule
        compose.onNodeWithTag(tag).performTextInput(text)
    }

    companion object {
        fun into(tag: String, text: String): EnterText = EnterText(tag, text)
    }
}
```

---

### 5. `questions/Question.kt` & `consequences/See.kt`
System state queries and expectations.

```kotlin
package com.sehmi.engine.screenplay.questions

import com.sehmi.engine.screenplay.actor.Actor

fun interface Question<T> {
    fun answeredBy(actor: Actor): T
}
```

```kotlin
package com.sehmi.engine.screenplay.consequences

import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.questions.Question
import org.junit.Assert.assertEquals

fun interface Consequence {
    fun evaluateFor(actor: Actor)
}

class See<T>(
    private val question: Question<T>,
    private val matcher: (T) -> Boolean,
    private val errorMessage: String = "Consequence check failed"
) : Consequence {

    override fun evaluateFor(actor: Actor) {
        val actual = question.answeredBy(actor)
        check(matcher(actual)) { "$errorMessage. Actual: $actual" }
    }

    companion object {
        fun <T> ifQuestion(question: Question<T>, expected: T): See<T> {
            return See(question, { it == expected }, "Expected $expected")
        }
    }
}
```

---

## 🧪 Sample End-to-End Test

Here is how a test looks using this proposed Screenplay architecture:

```kotlin
package com.sehmi.engine.screenplay

import androidx.compose.ui.test.junit4.createComposeRule
import com.sehmi.engine.screenplay.ability.BrowseComposeUI
import com.sehmi.engine.screenplay.actions.ClickOnTag
import com.sehmi.engine.screenplay.actions.EnterText
import com.sehmi.engine.screenplay.actor.Actor
import com.sehmi.engine.screenplay.consequences.See
import com.sehmi.engine.screenplay.core.Task
import com.sehmi.engine.screenplay.questions.NodeText
import org.junit.Rule
import org.junit.Test

class LoginScreenplayTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Task definition (Reusable)
    private fun logInWith(username: String, pass: String): Task = Task.where(
        "Log in as $username",
        EnterText.into("username_tag", username),
        EnterText.into("password_tag", pass),
        ClickOnTag.tag("submit_btn")
    )

    @Test
    fun userCanLogInSuccessfully() {
        val user = Actor.named("Test User")
            .can(BrowseComposeUI.with(composeRule))

        user.attemptsTo(
            logInWith("john@example.com", "secret123")
        )

        user.should(
            See.ifQuestion(NodeText.of("welcome_header"), "Welcome back, John!")
        )
    }
}
```

---

## 🛡️ Robustness Pipeline Integration

To guarantee the same zero-flakiness guarantees as `UI Automation Engine`, the `Actor` execution loop wraps calls in `FlakinessUtils.withRetry`:

1. **Automatic Idle Waits**: `BrowseComposeUI` invokes `composeRule.waitForIdle()` before and after every action.
2. **Failure Artifact Capture**: If an `Action` or `Consequence` fails, `ScreenplayDiagnosticsListener` catches the exception and immediately dumps the Compose Semantics tree and captures a full-resolution bitmap screenshot to disk.
