# Codebase Overview

## Entry point

| Platform | Entry point |
| --- | --- |
| Common UI | `composeApp/src/commonMain/kotlin/id/my/sinanonym/mybawanggacha/App.kt` |
| Android | `androidApp` module dan actual platform setup di `composeApp/src/androidMain` |
| iOS | `composeApp/src/iosMain/kotlin/id/my/sinanonym/mybawanggacha/MainViewController.kt` |

## Package utama

```text
id.my.sinanonym.mybawanggacha
├── core
├── data
├── domain
└── presentation
```

## Alur dependency runtime

```text
initKoin(platformModules)
-> sharedModules
-> App()
-> AppNavHost
-> Screen
-> ViewModel
-> UseCase/Repository
-> Local/Remote Source
```

## Shared modules

`sharedModules` menggabungkan module berikut:

- dispatchers
- network
- database
- preferences
- repository
- use case
- view model

## Area fitur

| Area | Domain package | Presentation package | Data package |
| --- | --- | --- | --- |
| Anime | `domain/anime` | `presentation/screen/anime*` | `data/remote/jikan`, `data/repository/anime` |
| Manga | `domain/manga` | `presentation/screen/manga*` | `data/remote/jikan`, `data/repository/manga` |
| Search | `domain/search` | `presentation/screen/search` | `data/repository/search` |
| Library | `domain/library` | `presentation/screen/library` | `data/repository/library`, SQLDelight |
| Notes | `domain/note` | `presentation/screen/note` | `data/repository/note`, SQLDelight |
| Gacha | `domain/gacha` | `presentation/screen/gacha` | `data/repository/gacha`, DataStore |
| AI | `domain/ai` | `presentation/screen/ai` | `data/remote/gemini`, SQLDelight, DataStore |
| Settings | `domain/settings` | `presentation/screen/settings` | DataStore, GitHub Release API |
