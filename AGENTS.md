# Repository Guidelines

## Project Structure & Module Organization

This is a Kotlin Android application built with Jetpack Compose and Gradle Kotlin DSL. The single application module is `app/`.

- Production Kotlin code: `app/src/main/java/com/se114/cafe/`
- Compose screens and state: `feature/`; shared visual tokens: `ui/theme/`
- Navigation: `navigation/`; Retrofit APIs and DTOs: `network/`; data access: `repository/`
- Android resources and manifest: `app/src/main/res/` and `app/src/main/AndroidManifest.xml`
- Local JVM tests: `app/src/test/`; device/emulator tests: `app/src/androidTest/`

Keep features self-contained (for example, `feature/auth/LoginScreen.kt`, `LoginViewModel.kt`, and `LoginUiState.kt`). Read `DESIGN.md` before changing UI: it defines the approved palette, flat styling, spacing, accessibility, and role-specific visual rules.

## Build, Test, and Development Commands

Use the Gradle Wrapper; do not rely on a system Gradle install.

```powershell
.\gradlew.bat assembleDebug       # build the debug APK
.\gradlew.bat test                # run local unit tests
.\gradlew.bat connectedAndroidTest # run instrumented tests on a connected device/emulator
.\gradlew.bat clean               # remove generated build outputs
```

Open the repository root—not `app/` alone—in Android Studio, then allow Gradle Sync to finish. The debug APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

## Coding Style & Naming Conventions

Write idiomatic Kotlin with four-space indentation. Use `PascalCase` for classes, composables, and data classes; `camelCase` for functions, properties, packages, and files that contain a single top-level concept. Name Compose screens with the `Screen` suffix and view models with `ViewModel` (for example, `LoginScreen`, `LoginViewModel`). Prefer immutable UI state and expose `StateFlow` read-only outside a view model. No formatter or linter is currently configured; format code consistently with nearby files and Android Studio.

## Testing Guidelines

Use JUnit 4 for local tests and AndroidX JUnit/Espresso/Compose tooling for instrumented tests. Name test files `*Test.kt` and test methods descriptively, such as `login_rejectsBlankEmail`. Add unit tests for view-model and repository behavior; use instrumented tests when Android framework or UI behavior is required. There is no enforced coverage threshold, but new logic should include focused tests.

## Commit & Pull Request Guidelines

Recent history uses short, imperative summaries such as `add: dto, repository, viewModel` and `Add readme file to guide how to clone and run fe`. Follow that style: one concise change per commit, optionally using a lowercase scope like `add:`. PRs should describe the change, link the related issue when available, list validation commands, and include screenshots or recordings for UI changes. Do not commit `local.properties`, build output, signing keys, tokens, or API secrets.
