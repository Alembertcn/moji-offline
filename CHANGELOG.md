# Changelog

All notable changes to this project are documented in this file.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project uses [Semantic Versioning](https://semver.org/).

## [0.1.0] — 2026-10-13

### Added

- Kotlin Multiplatform + Compose Multiplatform app shell for Android and iOS.
- MVI history feature: list, details dialog, delete, clear, demo seeding.
- Local persistence for history and preferences via multiplatform-settings.
- Settings: language (system/zh/en/ja), theme (system/light/dark).
- Empty, loading, and error UI states with retry.
- Debug toggle to simulate history storage errors.
- JVM unit tests for store, repository, and History ViewModel.
- GitHub Actions CI (`jvmTest` + `assembleDebug`).
- English docs: README, ARCHITECTURE, DECISIONS, TESTING, CHANGELOG.

### Notes

- Phase 1 intentionally excludes networking, full dictionary data, and membership flows.
