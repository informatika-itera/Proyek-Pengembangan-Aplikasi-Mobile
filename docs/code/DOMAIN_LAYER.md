# Domain Layer

Domain layer berisi model bisnis dan contract aplikasi. Domain tidak bergantung pada Ktor, SQLDelight, DataStore, atau Compose UI.

## Package domain

```text
domain/
├── ai
├── anime
├── gacha
├── library
├── manga
├── note
├── search
└── settings
```

## Repository contract

Repository interface berada di domain agar presentation/use case tidak bergantung pada implementasi data.

Contoh contract:

- `AnimeRepository`
- `MangaRepository`
- `SearchRepository`
- `LibraryRepository`
- `NoteRepository`
- `GachaRepository`
- `AIRepository`
- `SettingsRepository`
- `AiTokenUsageRepository`

## Use case

Use case dipakai ketika logic domain lebih dari sekadar memanggil repository.

Use case utama:

- note use cases
- `RunGachaUseCase`

## Domain model utama

| Area | Model penting |
| --- | --- |
| Anime | `AnimeSummary`, `AnimeDetail`, `AnimeEpisode`, `AnimeRelation` |
| Manga | `MangaSummary`, `MangaDetail`, `MangaRelation` |
| Search | `MediaSearchItem`, `MediaSearchFilters`, `SearchMediaType`, `SearchFilterOption` |
| Library | `LibraryEntry`, `LibraryStatus`, `MediaType`, `UserProgress`, `UserScore` |
| Gacha | `GachaPreference`, `GachaResultItem`, `GachaRunResult`, `GachaHistoryEntry` |
| AI | `ChatMessage`, `MessageSender`, `WritingStyle` |
| Settings | `ThemeMode`, `AppColorScheme`, `NetworkMode`, `AiApiModel`, `AiPersonality`, `GitHubRelease` |

## Mapping boundary

DTO dan SQLDelight entity harus dikonversi menjadi domain model sebelum sampai ke ViewModel/UI.
