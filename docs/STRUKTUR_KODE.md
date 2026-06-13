# Struktur Kode

## Root project

```text
.
├── androidApp/
├── iosApp/
├── composeApp/
├── docs/
├── .github/workflows/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── VERSIONING.md
└── PLAN.md
```

## `androidApp`

Modul aplikasi Android. Berisi entry point Android, manifest, konfigurasi package, resource Android-specific, dan build task aplikasi Android.

## `iosApp`

Project Xcode untuk target iOS. SwiftUI host memanggil shared Compose `MainViewController()` dari framework KMP.

File penting:

```text
iosApp/Configuration/Config.xcconfig
iosApp/iosApp/ContentView.swift
iosApp/iosApp/iOSApp.swift
iosApp/iosApp/Info.plist
```

## `composeApp`

Shared module KMP.

```text
composeApp/src/
├── commonMain/
│   ├── kotlin/id/my/sinanonym/mybawanggacha/
│   │   ├── core/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── composeResources/
│   └── sqldelight/
├── androidMain/
├── iosMain/
└── commonTest/
```

## Package `core`

```text
core/
├── build/
├── coroutines/
├── di/
├── network/
└── util/
```

Berisi dependency injection, Ktor client factory, platform dispatchers, generated build info, API config abstraction, dan database driver abstraction.

## Package `domain`

```text
domain/
├── ai/
├── anime/
├── gacha/
├── library/
├── manga/
├── note/
├── search/
└── settings/
```

Berisi model domain, repository contract, dan use case. Domain tidak membawa DTO Jikan/Gemini atau entity SQLDelight ke UI.

## Package `data`

```text
data/
├── local/
├── remote/
└── repository/
```

Berisi implementasi repository, SQLDelight local source, DataStore preferences, remote service, DTO, mapper, dan cache policy.

## Package `presentation`

```text
presentation/
├── component/
├── navigation/
├── screen/
└── theme/
```

Berisi UI Compose, ViewModel, route, NavHost, theme, color scheme, dan komponen reusable.

## SQLDelight

Schema ada di:

```text
composeApp/src/commonMain/sqldelight/id/my/sinanonym/mybawanggacha/
```

Schema utama:

- `Note.sq`
- `Library.sq`
- `Anime.sq`
- `Manga.sq`
- `MediaCache.sq`
- `AiChat.sq`

Migration ada di:

```text
composeApp/src/commonMain/sqldelight/migrations/
```
