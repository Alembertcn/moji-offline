# Architecture

## Goals

1. Share **UI + business logic** between Android and iOS with Compose Multiplatform.
2. Keep a **unidirectional** presentation layer that is easy to unit-test.
3. Make every Phase 1 requirement (history, persistence, i18n, theme, empty/error) visible in the architecture, not only in screenshots.

## High-level diagram

```text
┌──────────────────────────────────────────────────────────┐
│ Compose UI (commonMain)                                  │
│  HistoryRoute / SettingsRoute / MojiTheme / ProvideLocale│
└───────────────▲────────────────────────────▲─────────────┘
                │ StateFlow                  │ one-shot Effect
┌───────────────┴────────────────────────────┴─────────────┐
│ MVI ViewModels                                           │
│  HistoryViewModel / SettingsViewModel                    │
│  Intent → reduce → State (+ optional Effect)             │
└───────────────▲──────────────────────────────────────────┘
                │ repository APIs (interfaces)
┌───────────────┴──────────────────────────────────────────┐
│ Domain                                                   │
│  HistoryEntry, AppPreferences, repository contracts      │
└───────────────▲──────────────────────────────────────────┘
                │
┌───────────────┴──────────────────────────────────────────┐
│ Data                                                     │
│  Default*Repository → LocalStore → multiplatform-settings│
└──────────────────────────────────────────────────────────┘
```

## Layers

### Presentation (MVI)

Each feature owns:

| Type | Role |
|------|------|
| `UiState` | Everything the screen needs to render (loading, data, empty, error, selection) |
| `UiIntent` | User/system actions (`Refresh`, `ClearAll`, `SetTheme`, …) |
| `UiEffect` | One-off events that must **not** survive rotation as state (snackbars) |
| `*ViewModel` | Single writer of state; maps repository results into UI state |

`MviViewModel` extends AndroidX `ViewModel` so `viewModelScope` and Koin `viewModel { }` work on both platforms through the JetBrains lifecycle multiplatform artifacts.

### Domain

- Immutable models (`HistoryEntry`, `AppPreferences`, enums).
- Repository interfaces only — no Android/iOS types.

### Data

- `HistoryLocalStore` serializes the history list as JSON in settings.
- `PreferencesLocalStore` stores language, theme, and a debug “force error” flag.
- `DefaultHistoryRepository` seeds sample offline entries on first launch and exposes a reactive `Flow<AppResult<List<HistoryEntry>>>`.

### Platform entry

- **Android:** `MainActivity` initializes settings + Koin, hosts `App()`.
- **iOS:** `MainViewController()` returns a `ComposeUIViewController`; SwiftUI `ContentView` embeds it.
- **JVM:** settings factory returns `MapSettings` so CI tests stay in-memory.

## Navigation

Bottom bar with two destinations (`history`, `settings`) via Navigation Compose. Phase 1 deliberately avoids a deep graph so the interview story stays about MVI + persistence, not navigation gymnastics.

## Error model

Repositories return `AppResult` (`Success` / `Failure`). Failures become an `error` field on `HistoryUiState`. The UI renders a dedicated error composable with retry; it does not crash the process.

## i18n & theme

- UI copy is provided by `AppStrings` via `LocalAppStrings` / `ProvideAppLocale` (zh / en / ja).
- XML Compose resources under `composeResources/` mirror the same copy for a future CMP-resources migration (see ADR-009).
- Theme mode is applied in `MojiTheme` using Material 3 color schemes.
- Entry meanings also switch with the selected language so the history list demonstrates localization beyond chrome strings.

## Dependency injection

Koin module `appModule` wires stores → repositories → ViewModels. Screens obtain ViewModels with `koinViewModel()`.

## Why this shape for interviews

- One screen → one ViewModel → one state type is easy to narrate in English.
- Empty/error are **first-class state**, not afterthoughts.
- Persistence is behind interfaces, so tests use fakes without Robolectric.
