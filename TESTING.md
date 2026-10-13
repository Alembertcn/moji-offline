# Testing

## Strategy

| Layer | What we test | How |
|-------|--------------|-----|
| Local store | JSON round-trip / clear | JVM unit tests + `MapSettings` |
| Repository | Seed, clear, force-error mapping | JVM unit tests + Turbine |
| ViewModel | Intent → state transitions | JVM unit tests + Turbine + `StandardTestDispatcher` |
| UI | Manual for Phase 1 | Device / simulator walkthrough |

Compose UI tests are intentionally deferred to Phase 2 to keep CI fast and the first demo focused on MVI + persistence.

## Commands

```bash
# All common tests on the JVM target (primary CI gate)
./gradlew :composeApp:jvmTest

# Verbose
./gradlew :composeApp:jvmTest --info
```

## Test inventory (Phase 1)

### `HistoryLocalStoreTest`

- Writes entries and reads them back unchanged.
- `clear()` leaves an empty list.

### `DefaultHistoryRepositoryTest`

- First launch seeds non-empty history.
- `forceHistoryError` preference emits `AppResult.Failure(Storage)`.
- `clear()` emits an empty success list.

### `HistoryViewModelTest`

- Successful repository emission fills `entries`.
- Storage failure maps to `error` state.
- `ClearAll` intent reaches the repository.
- Empty list sets `isEmpty == true`.

## Fakes vs real collaborators

- ViewModel tests use a **fake** `HistoryRepository` to isolate presentation rules.
- Repository/store tests use **real** `Default*` + `MapSettings` to prove persistence wiring.

## CI

GitHub Actions (`.github/workflows/ci.yml`):

1. `./gradlew :composeApp:jvmTest`
2. `./gradlew :composeApp:assembleDebug`

iOS compile is Mac-only and not required for the Linux PR gate in Phase 1.

## How to add a test

1. Prefer `commonTest` so the same test runs on JVM CI.
2. For coroutines, use `runTest` + `Dispatchers.setMain(StandardTestDispatcher())`.
3. For `StateFlow` / `Flow`, assert with Turbine (`test { awaitItem() ... }`).
4. Keep tests behavior-focused (“clear empties history”), not implementation-focused.
