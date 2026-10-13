# Decisions

Architecture Decision Records (light form) for Phase 1.

## ADR-001 — Compose Multiplatform shared UI (not native UI per platform)

**Context:** The demo must show Android + iOS delivery from one codebase for overseas interviews.

**Decision:** Share both business logic **and** UI with Compose Multiplatform.

**Why:**  
- Matches the story “I can ship product UI once and keep platform shells thin.”  
- Avoids maintaining two parallel history screens for a Phase 1 demo.  
- Lets MVI ViewModels drive the same Compose tree on both platforms.

**Trade-off:** iOS look is Compose/Material, not pure SwiftUI. Acceptable for a KMP portfolio sample; native polish can come later if a job requires it.

## ADR-002 — MVI over “just ViewModel + LiveData-style state”

**Context:** Interviewers often probe unidirectional data flow and testability.

**Decision:** Explicit `State` / `Intent` / `Effect` on every feature ViewModel.

**Why:**  
- Intents make user actions searchable and testable (`onIntent(ClearAll)`).  
- Effects keep snackbars out of durable state.  
- Matches patterns used in production Compose codebases without bringing Orbit/Ballast unless needed.

**Trade-off:** Slightly more boilerplate than a single mutable state holder. Worth it for clarity.

## ADR-003 — Settings + JSON instead of SQLDelight in Phase 1

**Context:** History needs local persistence. SQLDelight is excellent for relational queries.

**Decision:** Persist history as a JSON list in `multiplatform-settings`; preferences as primitive keys.

**Why:**  
- History is a small ordered list — no joins, no FTS yet.  
- Fewer native drivers to wire for Android/iOS/JVM CI.  
- Keeps Linux CI green with in-memory `MapSettings` and no SQLite linking.

**Follow-up:** Move to SQLDelight (or Room Multiplatform) when search/filter/pagination appears.

## ADR-004 — Koin for DI

**Context:** Need multiplatform DI that works with Compose and ViewModels.

**Decision:** Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`).

**Why:** Lightweight, KMP-friendly, familiar to Android interviewers, easy to replace with kotlin-inject later.

## ADR-005 — JVM target dedicated to unit tests

**Context:** GitHub-hosted Linux runners cannot run iOS tests easily; Android unit tests need more setup.

**Decision:** Add a `jvm()` target and run `:composeApp:jvmTest` in CI.

**Why:** Fast feedback, no emulator, same commonMain code paths with `MapSettings`.

## ADR-006 — Debug “force history error” toggle

**Context:** Empty/error states are hard to show reliably in a live interview if storage never fails.

**Decision:** Settings includes a debug switch that makes `HistoryRepository` emit `AppError.Storage`.

**Why:** Demonstrates error UX on demand without corrupting files or mocking the whole graph in the UI.

**Trade-off:** Not a production setting — labeled as Debug and documented as interview tooling.

## ADR-007 — Seeded offline sample data

**Context:** A blank first launch fails the “history” story.

**Decision:** Seed five Japanese headwords with zh/en/ja meanings on first launch; expose “Add demo entries”.

**Why:** Offline-first narrative without shipping a full dictionary database in Phase 1.

## ADR-008 — No membership / no network

**Context:** MOJi-like products often gate features behind membership.

**Decision:** Explicitly remove accounts, IAP, and remote APIs from Phase 1.

**Why:** Keeps the demo honest, offline-capable, and focused on architecture signals recruiters can verify in the repo.

## ADR-009 — In-app i18n via CompositionLocal strings (not CMP ResourceEnvironment)

**Context:** Compose Multiplatform resource locale override APIs are internal/unstable across versions.

**Decision:** Drive UI copy through `AppStrings` + `LocalAppStrings`, selected by `AppLanguage`. Keep XML resource files as a parallel source for a future CMP-resources migration.

**Why:** Guarantees zh/en/ja switching works the same on Android, iOS, and JVM previews without depending on internal CMP APIs.
