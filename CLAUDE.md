# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (minification enabled)
./gradlew installDebug           # Build and install on connected device/emulator
./gradlew lint                   # Run lint checks
./gradlew :app:kspDebugKotlin    # Run KSP code generation (Hilt + Room) without full build
```

There are no tests in this project yet.

## Architecture

FilmeList is a single-module Android app (`:app`) following clean architecture with three layers:

**`data/`** — Implementation details
- `local/` — Room database (`filmelist.db`) with three tables: `lists`, `movies`, and `list_movie_cross_ref` (many-to-many join). Genre IDs are stored as a comma-separated string in `MovieEntity.genreIds`.
- `remote/` — Retrofit client for the TMDB API (v3). The TMDB Bearer token and base URLs are injected as `BuildConfig` fields defined in `app/build.gradle.kts`.
- `repository/` — `MovieRepositoryImpl` wraps only the TMDB API (no local caching); `ListRepositoryImpl` wraps only Room (no network calls).

**`domain/`** — Business logic, no Android dependencies
- `model/` — Pure Kotlin data classes (`Movie`, `MovieList`, `Genre`).
- `repository/` — Interfaces (`MovieRepository`, `ListRepository`) that the data layer implements.
- `usecase/` — One class per operation; each wraps a single repository call.

**`ui/`** — Jetpack Compose screens + ViewModels
- Four screens: `Lists`, `Search`, `ListDetail`, `MovieDetail`. Navigation is managed via `NavGraph.kt` using a sealed `Screen` class with typed route builders.
- Each screen has its own `ViewModel` that injects use-cases via Hilt (`@HiltViewModel`).
- `components/` — Shared composables (e.g., `MoviePosterCard`).
- Images loaded with Coil; image paths are relative and must be prefixed with `BuildConfig.TMDB_IMAGE_BASE_URL + size/` (e.g., `w500`).

**`di/AppModule.kt`** — Single Hilt `@Module` in `SingletonComponent` that wires everything together. On first database creation, a default list ("Favoritos de todos os tempos") is seeded via a `RoomDatabase.Callback`.

## Key Technical Notes

- **TMDB API key** is hardcoded directly in `app/build.gradle.kts` as a `buildConfigField`. To use a different key, replace the value there or override it via `local.properties` and a custom Gradle logic block.
- **KSP** (not KAPT) is used for both Hilt and Room annotation processing.
- `minSdk = 26`, `targetSdk = 35`, Java/Kotlin target is JVM 17.
- Release builds have ProGuard minification enabled; custom rules go in `app/proguard-rules.pro`.
- The `mediaType` field (`"MOVIE"` or `"TV"`) is threaded through navigation arguments and stored in `MovieEntity` to distinguish films from TV series in TMDB API calls.
