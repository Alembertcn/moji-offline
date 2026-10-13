# Moji Offline

A **Kotlin Multiplatform** interview demo that shares **Compose Multiplatform** UI across Android and iOS.

It is a simplified, **offline-first** lookup-history experience inspired by MOJi Dictionary’s history UX. There are **no accounts** and **no membership gates**.

> Not affiliated with MOJi / Wonder Ideas. Built as a portfolio sample for overseas Android / KMP interviews.

## Phase 1 scope

| Area | What ships |
|------|------------|
| History | View, open details, delete one, clear all, seed/demo entries |
| Persistence | Local key-value storage for history JSON + preferences |
| i18n | Chinese / English / Japanese (plus system) |
| Theme | Light / Dark / System |
| UX states | Loading, empty, error + retry |
| Quality | JVM unit tests + GitHub Actions CI |
| Docs | README, ARCHITECTURE, DECISIONS, TESTING, CHANGELOG |

Out of scope for Phase 1: networking, full dictionary corpus, accounts, paywalls, sync.

## Stack

- Kotlin Multiplatform + Compose Multiplatform
- MVI (`State` / `Intent` / `Effect`)
- Koin DI
- multiplatform-settings + kotlinx.serialization
- Navigation Compose + Material 3
- Turbine + kotlinx-coroutines-test

## Project layout

```text
moji-offline/
├── composeApp/          # shared UI + logic; Android application target
│   └── src/
│       ├── commonMain/  # domain, data, MVI features, Compose UI
│       ├── commonTest/  # unit tests (run on JVM)
│       ├── androidMain/
│       ├── iosMain/
│       └── jvmMain/     # Settings factory for JVM tests
├── iosApp/              # thin SwiftUI host for the Compose UIViewController
└── .github/workflows/   # CI
```

## Run

### Prerequisites

- JDK 17+
- Android Studio / IntelliJ with KMP plugin (recommended)
- Xcode 15+ for iOS

### Android

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

Or open the project in Android Studio and run the `composeApp` configuration.

### iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode (generate/open via the KMP IDE wizard if the project file is missing on a fresh clone).
2. Ensure the `ComposeApp` framework is linked from the Gradle iOS targets.
3. Run on a simulator.

From the command line (Mac):

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Tests

```bash
./gradlew :composeApp:jvmTest
```

## How to demo in an interview (5 minutes)

1. Launch → seeded history appears (offline persistence).
2. Open an item → reading + localized meaning.
3. Clear all → empty state; add demo entries again.
4. Settings → switch language (zh/en/ja) and theme (dark).
5. Enable **Simulate history error** → History shows error + retry.
6. Point at `ARCHITECTURE.md` / `DECISIONS.md` and walk the MVI flow.

## License

MIT — see [LICENSE](LICENSE).
