# Compose Screenplay Engine Release

Initial stable release of Compose Screenplay Engine (`com.sehmi.engine:compose-screenplay-engine:1.0.0-alpha01`).

### Features
- Screenplay Pattern (Actor, Ability, Task, Action, Question, Consequence) for Jetpack Compose UI Testing.
- Centralized Rule & Content Management (`ScreenplayEngine.createRule()`, `ScreenplayEngine.setContent()`).
- Unified `runRobustly` Flakiness Pipeline (Idle Synchronization, Auto-Scrolling, Polling & Retry, and Automatic Failure Diagnostics).
- BDD Kotlin DSL (`givenThat`, `whenUser`, `thenUser`).
- Custom Lint guardrails (`:engine-lint`).
