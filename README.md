# 🎭 Compose Screenplay Engine

**Compose Screenplay Engine** (`com.sehmi.engine.screenplay`) is a modern, production-grade test automation framework implementing **Screenplay Pattern** (Serenity-style) semantics specifically built for Jetpack Compose UI testing on Android.

---

## 🚀 Features

- **Actor/Task/Action/Question/Consequence Semantics**: Clean separation of concerns modeling real user behavior and business workflows.
- **Jetpack Compose Native Integration**: Built on top of `ComposeTestRule` (`BrowseComposeUI`), supporting both standard Compose rules and Hilt `@HiltAndroidTest`.
- **Expressive BDD Kotlin DSL**: Fluent phrasing (`givenThat`, `whenUser`, `thenUser`, `attempts`) making UI tests read like plain English specifications.
- **Industrial Robustness & Flakiness Pipeline**: Automatic retry engine (`ScreenplayFlakinessPipeline`) with customizable delay and backoff.
- **Diagnostic Capture Engine**: Automatic capture of Compose semantics text tree dumps and screenshot bitmaps upon failure (`ScreenplayDiagnosticsListener`).
- **Zero-Config JitPack Hosting**: Ready for publication with built-in ProGuard/R8 rules (`consumer-rules.pro`).

---

## 📦 Installation

Add the dependency to your app or test module's `build.gradle.kts`:

```kotlin
dependencies {
    androidTestImplementation("com.github.rajbirsehmi:Compose-Screenplay-Engine:1.0.0-alpha01")
}
```

---

## ⚡ Quick Start: Writing Screenplay Tests in Jetpack Compose

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUserLoginWorkflow() {
        // 1. Setup UI content
        composeTestRule.setContent {
            SampleLoginScreen()
        }

        // 2. Given an Actor equipped with Compose browsing abilities
        givenThat("Alice") {
            can(BrowseComposeUI.using(composeTestRule))
        }

        // 3. When the actor attempts business actions
        whenUser {
            attemptsTo(
                EnterText.value("Rajbir").intoTag("input_username"),
                Click.onTag("btn_login")
            )
        }

        // 4. Then verify consequences using assertions
        thenUser(
            See.ifThe(NodeExistence.existsTag("txt_welcome")) { it }.expect("Welcome banner should exist"),
            See.ifThe(NodeText.ofTag("txt_welcome")) { it.contains("Rajbir") }.expect("Welcome text should greet Rajbir")
        )
    }
}
```

---

## 🛠️ Running Tests

- **Unit Tests**:
  ```bash
  ./gradlew test
  ```
- **Instrumented UI Tests**:
  ```bash
  ./gradlew :app:connectedAndroidTest
  ```
