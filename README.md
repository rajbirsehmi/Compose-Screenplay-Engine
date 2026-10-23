# 🎭 Compose Screenplay Engine

**Compose Screenplay Engine** (`com.sehmi.engine.screenplay`) is a modern, enterprise-grade test automation framework implementing the **Screenplay Pattern** (Serenity-style) tailored specifically for Jetpack Compose UI testing on Android. It bridges the gap between readable BDD specifications and robust, deterministic execution on Jetpack Compose.

---

## 📑 Table of Contents
1. [Core Philosophy](#-core-philosophy--guiding-principles)
2. [System Requirements](#-system-requirements--compatibility)
3. [Global Configuration & Log4j2 Logging](#-global-engine-configuration--log4j2-logging)
4. [Architecture & Design Patterns](#-architecture--design-patterns)
5. [The `runRobustly` Execution Pipeline](#-the-runrobustly-execution-pipeline)
6. [Key Features & Action Matrix](#-key-features--action-matrix)
7. [Advanced Capabilities & Diagnostics](#-advanced-capabilities--diagnostics)
8. [Centralized Lifecycle Management (`ScreenplayEngine`)](#-centralized-lifecycle-management-screenplayengine)
9. [Custom Lint Guardrails](#-custom-lint-guardrails)
10. [Installation & JitPack Integration Guide](#-installation--jitpack-integration-guide)
11. [Technical References](#-technical-references)

---

## 🎯 Core Philosophy & Guiding Principles

UI testing in Jetpack Compose often suffers from flakiness due to animations, asynchronous recompositions, and opaque test logs. **Compose Screenplay Engine** solves these challenges through:
- **Actor-Centric Behavior Modeling**: Tests model real users ("Actors") who possess capabilities ("Abilities") to interact with the system and achieve business goals.
- **Readable BDD Kotlin DSL**: Expressive syntax (`givenThat`, `whenUser`, `thenUser`, `attemptsTo`) that reads like plain English specifications.
- **Industrial Determinism (`runRobustly`)**: Every interaction is automatically guarded by idle synchronization, auto-scrolling, polling retries, and diagnostic capture.
- **Full Workflow Log4j2 Logging**: Every action, query, retry, and assertion is logged structured via Log4j2 and Android Logcat.

---

## 📋 System Requirements & Compatibility

- **Minimum SDK**: API 30+ (Android 11+)
- **Compile SDK**: API 37+
- **Java Compatibility**: Java 17+ (Source & Target)
- **Kotlin Compatibility**: Kotlin 2.4+ with Compose Compiler plugin
- **Testing Libraries**: Jetpack Compose UI Test (`androidx.compose.ui.test.junit4`), JUnit 4, Espresso, Log4j2 (`2.23.1`)

---

## ⚙️ Global Engine Configuration & Log4j2 Logging

Configure engine-wide behavior—such as timeouts, polling intervals, verbose Log4j2 logging, and failure artifact capture—globally via `ScreenplayEngine.configure`:

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

When `verboseLogging = true`, **ScreenplayLogger** automatically outputs structured workflow logs to both **Log4j2** (`org.apache.logging.log4j.LogManager`) and Android **Logcat**:
- `🎭 [STEP] ...` — High-level workflow tasks
- `👤 [Alice] ...` — Actor abilities and actions
- `⚡ [ACTION] ...` — UI clicks, text input, and gestures
- `❓ [QUESTION] ...` — Semantics and state queries
- `⚖️ [CONSEQUENCE] ...` — Assertions and test results

---

## 🏗 Architecture & Design Patterns

The Screenplay Pattern organizes tests around 5 foundational pillars:

### 1. Actor (`Actor`)
Represents the user interacting with the app.
```kotlin
val alice = Actor.named("Alice")
```

### 2. Ability (`Ability`)
Equips the actor with tools to interact with external drivers (e.g., Jetpack Compose UI).
```kotlin
alice.can(BrowseComposeUI.using())
```

### 3. Action (`Action`)
Atomic UI interactions (clicking, typing, scrolling, advancing time).
```kotlin
attemptsTo(
    EnterText.value("EnterpriseUser").intoTag("input_username"),
    Click.onTag("btn_login")
)
```

### 4. Question (`Question`)
Queries the state of the UI (`NodeText`, `NodeExistence`, `NodeEnabled`).
```kotlin
NodeExistence.existsTag("txt_welcome")
```

### 5. Consequence (`Consequence`)
Asserts that a question satisfies expectations with rich diagnostic messaging (`See.ifThe(...)`).
```kotlin
thenUser(
    See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }.expect("Welcome banner should exist")
)
```

---

## 🛡 The `runRobustly` Execution Pipeline

Every action and interaction in the engine is wrapped in the `ScreenplayFlakinessPipeline.runRobustly` execution block, guaranteeing:

1. **Idle Synchronization**: Automatically waits for the Compose UI to be stable (`waitForIdle()`) before and after interaction.
2. **Auto-Scrolling**: Attempts to bring off-screen target nodes into the viewport (`performScrollTo()`) prior to interaction.
3. **Polling & Retry**: Automatically handles transient animations, asynchronous recompositions, and network delays with configurable retries and delay backoff.
4. **Failure Capture**: Automatically generates semantics tree dumps (`_semantics.txt`), Logcat tail lines, and self-contained HTML failure reports (`_FAILURE.html`) upon retry exhaustion.

---

## ✨ Key Features & Action Matrix

| Category | Actions / Features | Description |
| :--- | :--- | :--- |
| **Clicks & Touches** | `Click.onTag`, `Click.onText`, `DoubleClick`, `LongClick`, `DragAndDrop`, `ScrollAndClick`, `ClickFirstChild` | Interacts with Compose nodes with auto-scroll and idle sync. |
| **Text Entry** | `EnterText.value().intoTag()`, `ReplaceText`, `ClearText`, `RequestFocus`, `PressImeAction` | Manages text input fields and focus management. |
| **Scrolling** | `Scroll.toTag()`, `ScrollToIndex` | Navigates lazy lists and scrollable containers. |
| **Time Control** | `AdvanceTime.byMillis()` | Controls the Compose test clock for time-based testing. |
| **System Control** | `OnDevice.pressBack()`, `rotateScreenPortrait()`, `wakeUp()`, `executeShell()` | OS-level orchestration via UIAutomator. |
| **Questions** | `NodeText`, `NodeExistence`, `NodeEnabled` | Inspects UI state declaratively. |

---

## 📸 Advanced Capabilities & Diagnostics

### Failure Diagnostics & HTML Reports
When an action fails after all retry attempts, `FailureDiagnosticWatcher` and `ScreenplayDiagnosticsListener` capture:
- **Semantics Tree Dump**: Complete hierarchy of Compose nodes.
- **Logcat Tail Capture**: Last N lines of device logcat logs.
- **HTML Failure Report**: Self-contained failure report (`_FAILURE.html`) detailing the exception, timestamp, semantics dump, logcat, and screenshot.

---

## ⚙️ Centralized Lifecycle Management (`ScreenplayEngine`)

Instead of managing raw `ComposeTestRule` instances manually, `ScreenplayEngine` provides zero-config rule and content orchestration:

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenplayTest {

    // 1. Centralized Rule Factory
    @get:Rule
    val composeTestRule = ScreenplayEngine.createRule()

    @Test
    fun testLoginWorkflow() {
        // 2. Centralized Content Setting
        ScreenplayEngine.setContent {
            SampleLoginScreen()
        }

        // 3. Zero-config Ability Binding
        givenThat("Alice") {
            can(BrowseComposeUI.using())
        }

        whenUser {
            attemptsTo(
                EnterText.value("Admin").intoTag("input_username"),
                Click.onTag("btn_login")
            )
        }

        thenUser(
            See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }.expect("Welcome screen visible")
        )
    }
}
```

### Regular vs. Hilt / DI App Support

The **Compose Screenplay Engine** is a single unified library that supports both regular (non-DI) apps and Dagger Hilt DI-based apps out-of-the-box. Consuming apps do not need separate artifacts or dependency variants; instead, you choose the appropriate rule factory method based on your test architecture:

1. **Regular (Non-DI) Compose Apps**: Use `ScreenplayEngine.createRule()`
   ```kotlin
   @get:Rule
   val rule = ScreenplayEngine.createRule()
   ```

2. **Dagger Hilt / DI Compose Apps**: Use `ScreenplayEngine.createHiltRule(Activity::class.java)`
   ```kotlin
   @HiltAndroidTest
   class MyHiltUiTest {
       @get:Rule(order = 0)
       val hiltRule = HiltAndroidRule(this)

       @get:Rule(order = 1)
       val rule = ScreenplayEngine.createHiltRule(MainActivity::class.java)

       @Test
       fun testHiltWorkflow() {
           hiltRule.inject()
           ScreenplayEngine.setComposeRule(rule)
           // ...
       }
   }
   ```


---

## 🛑 Custom Lint Guardrails

The engine includes a custom Lint module (`:engine-lint`) featuring **`DirectUiTestApiUsageDetector`** (`DirectUiTestApiUsage`). It flags direct raw usage of Compose testing APIs (e.g. `performClick()` without Screenplay wrappers) at compile/lint time, enforcing best practices across your test suite.

---

## 📦 Installation & JitPack Integration Guide

### Step 1: Add JitPack Repository
Add `https://jitpack.io` to your consuming app's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Configure Version Catalog (`gradle/libs.versions.toml`)
```toml
[versions]
composeScreenplayEngine = "1.0.0-alpha01"

[libraries]
compose-screenplay-engine = { group = "com.github.rajbirsehmi", name = "Compose-Screenplay-Engine", version.ref = "composeScreenplayEngine" }
```

### Step 3: Add Dependency in `build.gradle.kts`
Add the dependency to your consuming app or module's `build.gradle.kts`:

```kotlin
dependencies {
    androidTestImplementation(libs.compose.screenplay.engine)
    // Or directly via string notation:
    // androidTestImplementation("com.github.rajbirsehmi:Compose-Screenplay-Engine:1.0.0-alpha01")
}
```

### Step 4: Ensure Java 17 Compatibility
Set Java 17 compatibility in your consuming app's `build.gradle.kts`:

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

---

## 📚 Technical References

For detailed class APIs, action builders, and extension methods, see [ENGINE_REFERENCE.md](file:///D:/Android/Projects/ComposeScreenplayEngine/ENGINE_REFERENCE.md).
